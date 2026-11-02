#!/usr/bin/env python3
"""Tests for Firebase client configuration handling; no real keys required."""

import base64
import json
from pathlib import Path
import tempfile
import unittest

import firebase_config as config


class FirebaseConfigTest(unittest.TestCase):
    def setUp(self):
        temp = tempfile.TemporaryDirectory(prefix="devpulse-firebase-test-")
        self.addCleanup(temp.cleanup)
        self.root = Path(temp.name)

    def test_test_mode_generates_inert_configs_for_both_flavors(self):
        config.generate_test_configs(self.root)
        for env, (project, package) in config.ENVIRONMENTS.items():
            parsed = json.loads(config.destination(self.root, env).read_text())
            self.assertEqual(project, parsed["project_info"]["project_id"])
            self.assertEqual(package, parsed["client"][0]["client_info"]["android_client_info"]["package_name"])
            self.assertEqual("ci-placeholder-not-a-real-api-key", parsed["client"][0]["api_key"][0]["current_key"])
        with self.assertRaises(config.FirebaseConfigError):
            config.generate_test_configs(self.root)

    def test_restore_preserves_real_config_bytes_and_refuses_overwrite(self):
        raw = config.test_configuration("production").replace(b"ci-placeholder-not-a-real-api-key", b"example-production-key")
        config.restore_config(self.root, "production", base64.b64encode(raw).decode())
        self.assertEqual(raw, config.destination(self.root, "production").read_bytes())
        with self.assertRaises(config.FirebaseConfigError):
            config.restore_config(self.root, "production", base64.b64encode(raw).decode())

    def test_restore_rejects_missing_invalid_and_wrong_project(self):
        with self.assertRaisesRegex(config.FirebaseConfigError, "Missing"):
            config.restore_config(self.root, "staging", "")
        with self.assertRaisesRegex(config.FirebaseConfigError, "Invalid"):
            config.restore_config(self.root, "staging", "not-base64")
        wrong = base64.b64encode(config.test_configuration("production")).decode()
        with self.assertRaisesRegex(config.FirebaseConfigError, "project mismatch"):
            config.restore_config(self.root, "staging", wrong)
        self.assertFalse(config.destination(self.root, "staging").exists())


if __name__ == "__main__":
    unittest.main()
