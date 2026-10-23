#!/usr/bin/env python3
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


class BuildAndroidTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='devpulse-build-fixture-')
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        (self.root / 'scripts').mkdir()
        self.script = self.root / 'scripts/build-android.sh'
        shutil.copyfile(Path(__file__).with_name('build-android.sh'), self.script)
        wrapper = self.root / 'gradlew'
        wrapper.write_text('#!/usr/bin/env bash\nprintf "%s\\n" "$PWD" "$@"\n')
        wrapper.chmod(0o755)

    def run_build(self, *args):
        return subprocess.run(['bash', str(self.script), *args], cwd='/',
                              text=True, capture_output=True)

    def test_default_build_selects_staging_debug_from_project_root(self):
        result = self.run_build()
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual([str(self.root), ':androidApp:assembleStagingDebug',
                          '-Pbuildkonfig.flavor=staging'], result.stdout.splitlines())

    def test_release_apks_and_production_bundle_select_matching_environment(self):
        for environment, flavor in [('staging', 'Staging'), ('production', 'Production')]:
            for format, task in [('apk', 'assemble'), ('aab', 'bundle')]:
                with self.subTest(environment=environment, format=format):
                    result = self.run_build(environment, 'release', format)
                    self.assertEqual(0, result.returncode, result.stderr)
                    self.assertEqual([str(self.root), f':androidApp:{task}{flavor}Release',
                                      f'-Pbuildkonfig.flavor={environment}'], result.stdout.splitlines())

    def test_invalid_arguments_do_not_start_gradle(self):
        for args in [('qa',), ('staging', 'fast'), ('production', 'release', 'zip'),
                     ('staging', 'debug', 'apk', '-Pbuildkonfig.flavor=production')]:
            with self.subTest(args=args):
                result = self.run_build(*args)
                self.assertNotEqual(0, result.returncode)
                self.assertEqual('', result.stdout)


if __name__ == '__main__':
    unittest.main()
