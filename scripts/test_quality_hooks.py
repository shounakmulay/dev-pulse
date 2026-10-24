#!/usr/bin/env python3
from pathlib import Path
import shutil
import os
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parent.parent


class QualityHooksTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='devpulse-hooks-')
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.run_command('git', 'init', '-q')
        self.run_command('git', 'config', 'user.name', 'Hook fixture')
        self.run_command('git', 'config', 'user.email', 'fixture@example.invalid')
        (self.root / '.githooks').mkdir()
        shutil.copy(ROOT / '.githooks/pre-push', self.root / '.githooks/pre-push')
        (self.root / 'gradlew').write_text('#!/bin/sh\nprintf "%s\\n" "$*"\nexit "${QUALITY_EXIT:-0}"\n')
        (self.root / 'gradlew').chmod(0o755)

    def run_command(self, *args, **kwargs):
        return subprocess.run(args, cwd=self.root, text=True, capture_output=True, **kwargs)

    def install(self):
        return self.run_command('sh', str(ROOT / 'scripts/install-hooks.sh'))

    def test_install_is_idempotent_and_preserves_existing_hook(self):
        self.assertEqual(0, self.install().returncode)
        self.assertEqual(0, self.install().returncode)
        hook = self.root / '.git/hooks/pre-push'
        hook.chmod(0o644)
        self.assertEqual(0, self.install().returncode)
        self.assertTrue(os.access(hook, os.X_OK))
        hook.write_text('existing hook')
        self.assertEqual(1, self.install().returncode)
        self.assertEqual('existing hook', hook.read_text())

    def test_custom_hooks_path_is_respected(self):
        self.run_command('git', 'config', 'core.hooksPath', '.custom-hooks')
        self.assertEqual(0, self.install().returncode)
        self.assertTrue((self.root / '.custom-hooks/pre-push').is_file())
        self.assertFalse((self.root / '.git/hooks/pre-push').exists())

    def test_hook_preserves_arguments_output_and_failure(self):
        hook = str(self.root / '.githooks/pre-push')
        result = self.run_command('sh', hook, 'origin', 'unused', input='ref sha ref sha\n')
        self.assertEqual(0, result.returncode)
        self.assertIn('qualityQuick --console=plain', result.stdout)
        result = self.run_command('sh', hook, env=dict(os.environ, QUALITY_EXIT='1'))
        self.assertEqual(1, result.returncode)
        self.assertIn('Push blocked', result.stderr)
        self.assertIn('qualityQuick --console=plain', result.stdout)

    def test_worktree_installs_shared_hook_without_changing_config(self):
        self.run_command('git', 'add', '.')
        self.run_command('git', 'commit', '-qm', 'fixture')
        checkout = self.root / 'checkout'
        result = self.run_command('git', 'worktree', 'add', '-q', '--detach', str(checkout))
        self.assertEqual(0, result.returncode, result.stderr)
        result = subprocess.run(['sh', str(ROOT / 'scripts/install-hooks.sh')], cwd=checkout,
                                text=True, capture_output=True)
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertTrue((self.root / '.git/hooks/pre-push').is_file())
        result = self.run_command('git', 'config', '--get', 'core.hooksPath')
        self.assertEqual(1, result.returncode)


if __name__ == '__main__':
    unittest.main()
