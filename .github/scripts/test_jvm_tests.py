#!/usr/bin/env python3
import os
from pathlib import Path
import subprocess
import tempfile
import unittest


SCRIPT = Path(__file__).resolve().with_name('jvm-tests.sh')


class JvmTestsTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='devpulse-jvm-tests-')
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        gradlew = self.root / 'gradlew'
        gradlew.write_text('#!/bin/sh\nprintf "%s\\n" "$@"\nexit "${TEST_EXIT:-0}"\n')
        gradlew.chmod(0o755)

    def run_tests(self, exit_code='0'):
        return subprocess.run(['bash', str(SCRIPT)], cwd=self.root, capture_output=True,
                              text=True, env=dict(os.environ, TEST_EXIT=exit_code))

    def test_runs_production_behavior_suites_before_release(self):
        result = self.run_tests()
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual([
            ':core:data:db:jvmTest',
            ':core:data:feed:jvmTest', ':core:network:jvmTest', '--continue', '--console=plain',
        ], result.stdout.splitlines())

    def test_gradle_failure_fails_the_ci_step(self):
        result = self.run_tests(exit_code='7')
        self.assertEqual(7, result.returncode)


if __name__ == '__main__':
    unittest.main()
