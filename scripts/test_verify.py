#!/usr/bin/env python3
import contextlib
import hashlib
import importlib.util
import io
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch


spec = importlib.util.spec_from_file_location('verify', Path(__file__).with_name('verify.py'))
verify = importlib.util.module_from_spec(spec)
spec.loader.exec_module(verify)


class VerificationTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='devpulse-verify-fixture-')
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.git('init', '-q')
        self.git('config', 'user.name', 'Verification fixture')
        self.git('config', 'user.email', 'fixture@example.invalid')
        self.write('seed', 'original')
        self.git('add', '.')
        self.git('commit', '-qm', 'fixture')
        self.base = self.git('rev-parse', 'HEAD').strip()
        self.registry = {'modules': {
            ':core': {'path': 'core', 'dependencies': []},
            ':app': {'path': 'app', 'dependencies': [':core']}}}

    def git(self, *args):
        return verify.git(self.root, *args).decode()

    def write(self, path, content):
        target = self.root / path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(content)

    def test_excluded_modules_skip_checks_but_keep_consumers(self):
        self.registry['excluded_modules'] = [':core']
        self.assertEqual([':app'], verify.select_modules(self.registry, {'core/change'}, False))
        self.assertEqual([':app'], verify.select_modules(self.registry, set(), True))

    def test_clean_explicit_base(self):
        base, _ = verify.resolve_base(self.root, self.base)
        self.assertEqual(set(), verify.changed_paths(self.root, base))
        self.assertEqual([], verify.select_modules(self.registry, set(), False))

    def test_staged_unstaged_untracked_and_unusual_names(self):
        self.write('core/staged', 'staged')
        self.git('add', 'core/staged')
        self.write('seed', 'unstaged')
        names = ['app/a b', 'app/λ', 'app/-leading', 'app/new\nline', 'app/$(touch nope)']
        for name in names:
            self.write(name, 'untracked')
        self.assertEqual(set(names + ['seed', 'core/staged']), verify.changed_paths(self.root, self.base))
        self.assertFalse((self.root / 'nope').exists())

    def test_renames_and_deletions_retain_both_scopes(self):
        self.write('core/old', 'same')
        self.git('add', '.')
        self.git('commit', '-qm', 'old')
        base = self.git('rev-parse', 'HEAD').strip()
        (self.root / 'app').mkdir()
        self.git('mv', 'core/old', 'app/new')
        (self.root / 'seed').unlink()
        self.assertEqual({'core/old', 'app/new', 'seed'}, verify.changed_paths(self.root, base))

    def test_branch_commits_are_included(self):
        self.write('core/change', 'committed')
        self.git('add', '.')
        self.git('commit', '-qm', 'change')
        self.assertEqual({'core/change'}, verify.changed_paths(self.root, self.base))

    def test_missing_default_detached_head_and_missing_base(self):
        self.git('checkout', '--detach', '-q')
        self.assertIsNone(verify.resolve_base(self.root, None)[0])
        with self.assertRaises(subprocess.CalledProcessError):
            verify.resolve_base(self.root, '--bad-ref')

    def test_shallow_missing_ancestor_fails(self):
        (self.root / '.git/shallow').write_text(self.base + '\n')
        with self.assertRaises(subprocess.CalledProcessError):
            verify.resolve_base(self.root, 'HEAD~1')

    def test_consumers_and_uncertain_root_change(self):
        self.assertEqual([':app', ':core'], verify.select_modules(self.registry, {'core/item'}, False))
        self.assertEqual([':app'], verify.select_modules(self.registry, {'app/item'}, False))
        self.assertEqual([':app', ':core'], verify.select_modules(self.registry, {'new.config'}, False))
        self.assertEqual([], verify.select_modules(self.registry, {'AGENTS.md'}, False))

    def test_default_stages_compile_production_without_test_tasks(self):
        registry = {'modules': {':core': {
            'path': 'core', 'dependencies': [],
            'tasks': ['compileKotlinJvm', 'jvmTest', 'lintJvm', 'compileAndroidMain',
                      'lintAndroidMain', 'testAndroidHostTest']}}}
        self.write('core/src/commonTest/kotlin/FailureTest.kt', 'not valid Kotlin')
        with patch.dict(verify.os.environ, {'DEVPULSE_VERIFY_ANDROID': '1'}):
            stages, _ = verify.stages(self.root, registry, [':core'])
        self.assertEqual([('jvm', [':core:lintJvm', ':core:compileKotlinJvm']),
                          ('android', [':core:lintAndroidMain', ':core:compileAndroidMain'])], stages)

    def test_android_stage_selects_flavored_app_checks(self):
        registry = {'modules': {':androidApp': {
            'path': 'androidApp', 'dependencies': [],
            'tasks': ['compileStagingDebugKotlin', 'lintStagingDebug']}}}
        with patch.dict(verify.os.environ, {'DEVPULSE_VERIFY_ANDROID': '1'}):
            stages, _ = verify.stages(self.root, registry, [':androidApp'])
        self.assertEqual([('android', [':androidApp:lintStagingDebug',
                                       ':androidApp:compileStagingDebugKotlin'])], stages)

    def test_missing_tool_and_failure_are_nonzero(self):
        with contextlib.redirect_stdout(io.StringIO()):
            with self.assertRaises(FileNotFoundError):
                verify.run_step(self.root, 'missing', ['./missing-program'], 3)
            with self.assertRaisesRegex(RuntimeError, 'exit 7') as failure:
                verify.run_step(self.root, 'failure', [sys.executable, '-c', 'print("token=private"); raise SystemExit(7)'], 3)
        log = Path(str(failure.exception).split('sanitized log ', 1)[1])
        self.addCleanup(log.unlink)
        self.assertNotIn('private', log.read_text())

    def test_timeout_terminates_owned_process(self):
        with contextlib.redirect_stdout(io.StringIO()):
            with self.assertRaisesRegex(RuntimeError, 'did not complete'):
                verify.run_step(self.root, 'timeout', [sys.executable, '-c', 'import time; time.sleep(10)'], 0.01)

    def prepare_registry(self):
        paths = ['settings.gradle.kts', 'build.gradle.kts', 'buildSrc/build.gradle.kts',
                 'buildSrc/settings.gradle.kts', 'gradle.properties', 'gradle/libs.versions.toml',
                 'gradle/wrapper/gradle-wrapper.properties', 'core/build.gradle.kts',
                 'gradlew', 'gradlew.bat', 'gradle/wrapper/gradle-wrapper.jar']
        for path in paths:
            self.write(path, 'fixture')
        self.write('settings.gradle.kts', 'include(":core")')
        self.write('.agents/skills/verify-change/SKILL.md', '---\nname: verify-change\ndescription: Use when verifying.\n---\n')
        self.write('.claude/skills/verify-change/SKILL.md', '---\nname: verify-change\ndescription: Use when verifying.\n---\n../../../.agents/skills/verify-change/SKILL.md\n')
        self.write('AGENTS.md', 'Read verify-change.\n')
        self.write('.claude/CLAUDE.md', '@../AGENTS.md\n')
        registry = {'version': 1, 'modules': {':core': {'path': 'core', 'dependencies': [], 'tasks': ['jvmTest']}},
                    'configuration': {p: hashlib.sha256((self.root / p).read_bytes()).hexdigest() for p in paths},
                    'references': {'skill': '.agents/skills/verify-change/SKILL.md'},
                    'workflows': {'verify-change': {'path': '.agents/skills/verify-change/SKILL.md',
                                                  'adapter': '.claude/skills/verify-change/SKILL.md', 'sources': []}},
                    'guidance': ['AGENTS.md', '.claude/CLAUDE.md'], 'editor_adapters': {},
                    'gradle_options': ['--offline'], 'timeout_seconds': 3}
        self.write('scripts/verification.json', json.dumps(registry))
        return registry

    def test_drift_accepts_valid_and_rejects_changed_config_or_missing_reference(self):
        registry = self.prepare_registry()
        verify.check_registry(self.root, registry)
        self.write('core/build.gradle.kts', 'changed')
        with self.assertRaisesRegex(ValueError, 'drift'):
            verify.check_registry(self.root, registry)
        self.write('core/build.gradle.kts', 'fixture')
        (self.root / registry['references']['skill']).unlink()
        with self.assertRaisesRegex(ValueError, 'Missing canonical'):
            verify.check_registry(self.root, registry)

    def test_ci_configuration_requires_audit_and_rejects_later_drift(self):
        registry = self.prepare_registry()
        path = '.github/workflows/quality.yml'
        self.write(path, 'name: Quality\n')
        with self.assertRaisesRegex(ValueError, 'file set changed'):
            verify.check_registry(self.root, registry)
        registry['configuration'][path] = hashlib.sha256((self.root / path).read_bytes()).hexdigest()
        verify.check_registry(self.root, registry)
        self.write(path, 'name: Changed\n')
        with self.assertRaisesRegex(ValueError, 'drift'):
            verify.check_registry(self.root, registry)
        self.write('.github/scripts/quality.sh', 'exit 0\n')
        with self.assertRaisesRegex(ValueError, 'file set changed'):
            verify.check_registry(self.root, registry)

    def test_removed_build_command_definition_rejected(self):
        registry = self.prepare_registry()
        (self.root / 'core/build.gradle.kts').unlink()
        with self.assertRaisesRegex(ValueError, 'Missing module build'):
            verify.check_registry(self.root, registry)

    def test_removed_or_renamed_task_rejected(self):
        registry = self.prepare_registry()
        verify.check_tasks(registry, [':core'], 'core:jvmTest - Runs tests\n')
        with self.assertRaisesRegex(RuntimeError, 'Task drift'):
            verify.check_tasks(registry, [':core'], 'core:renamedTest - Runs tests\n')

    def test_tracked_artifact_writer_is_refused(self):
        verify.check_graph(':core:common:jvmTest SKIPPED')
        with self.assertRaisesRegex(RuntimeError, 'tracked generated artifacts'):
            verify.check_graph(':core:data:db:copyRoomSchemas SKIPPED')

    def test_broken_skill_link_is_rejected(self):
        registry = self.prepare_registry()
        path = self.root / registry['references']['skill']
        path.write_text(path.read_text() + '[Missing](../missing/SKILL.md)\n')
        with self.assertRaisesRegex(ValueError, 'Broken guidance link'):
            verify.check_guidance(self.root, registry)

    def test_adapter_metadata_and_target_drift_are_rejected(self):
        registry = self.prepare_registry()
        path = self.root / registry['workflows']['verify-change']['adapter']
        original = path.read_text()
        path.write_text(original.replace('Use when verifying.', 'Use when testing.'))
        with self.assertRaisesRegex(ValueError, 'Adapter metadata drift'):
            verify.check_guidance(self.root, registry)
        path.write_text(original.replace('../../../.agents', '../../../missing'))
        with self.assertRaisesRegex(ValueError, 'Adapter routing drift'):
            verify.check_guidance(self.root, registry)

    def test_missing_root_route_or_source_is_rejected(self):
        registry = self.prepare_registry()
        self.write('AGENTS.md', 'No routes.\n')
        with self.assertRaisesRegex(ValueError, 'Missing root workflow route'):
            verify.check_guidance(self.root, registry)
        self.write('AGENTS.md', 'Read verify-change.\n')
        registry['workflows']['verify-change']['sources'] = ['missing.kt']
        with self.assertRaisesRegex(ValueError, 'Missing workflow source'):
            verify.check_guidance(self.root, registry)

    def test_ignored_skill_or_invalid_metadata_is_rejected(self):
        registry = self.prepare_registry()
        self.write('.gitignore', '.agents/\n')
        with self.assertRaisesRegex(ValueError, 'ignored by Git'):
            verify.check_guidance(self.root, registry)
        self.write('.gitignore', '')
        path = self.root / registry['references']['skill']
        path.write_text(path.read_text().replace('description:', 'unexpected:'))
        with self.assertRaisesRegex(ValueError, 'Invalid skill metadata'):
            verify.check_guidance(self.root, registry)

    def test_strict_skips_fail_and_plan_does_not_execute(self):
        self.prepare_registry()
        with patch.object(verify, 'ROOT', self.root), patch.object(sys, 'argv', ['verify', '--base', self.base, '--strict']), \
                patch.object(verify, 'stages', return_value=([], ['required platform disabled'])), \
                patch.object(verify, 'run_step') as run, contextlib.redirect_stdout(io.StringIO()):
            self.assertEqual(1, verify.main())
            self.assertEqual(1, run.call_count)
        with patch.object(verify, 'ROOT', self.root), patch.object(sys, 'argv', ['verify', '--base', self.base, '--plan']), \
                patch.object(verify, 'run_step') as run, contextlib.redirect_stdout(io.StringIO()):
            self.assertEqual(0, verify.main())
            run.assert_not_called()


if __name__ == '__main__':
    unittest.main()
