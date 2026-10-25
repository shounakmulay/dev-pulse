#!/usr/bin/env python3
from contextlib import redirect_stdout
import importlib.util
import io
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch
from urllib.parse import parse_qs

from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from google.auth import jwt
import requests


SPEC = importlib.util.spec_from_file_location('android_cd', Path(__file__).with_name('android_cd.py'))
cd = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = cd
SPEC.loader.exec_module(cd)


class PlayAuthTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='devpulse-play-auth-test-')
        self.addCleanup(self.temp.cleanup)
        self.directory = Path(self.temp.name)
        self.output = self.directory / 'output'
        self.credentials = self.directory / 'service-account.json'
        key = rsa.generate_private_key(public_exponent=65537, key_size=2048)
        self.public_key = key.public_key().public_bytes(
            serialization.Encoding.PEM, serialization.PublicFormat.SubjectPublicKeyInfo,
        )
        self.key_info = {
            'type': 'service_account', 'project_id': 'fixture-project',
            'private_key_id': 'fixture-key',
            'private_key': key.private_bytes(
                serialization.Encoding.PEM, serialization.PrivateFormat.PKCS8,
                serialization.NoEncryption(),
            ).decode(),
            'client_email': 'play@example.iam.gserviceaccount.com',
            'token_uri': 'https://oauth2.googleapis.com/token',
        }
        self.credentials.write_text(json.dumps(self.key_info))
        environment = patch.dict(os.environ, {'GITHUB_OUTPUT': str(self.output)}, clear=True)
        environment.start()
        self.addCleanup(environment.stop)

    def response(self, body, status=200):
        response = requests.Response()
        response.status_code = status
        response._content = json.dumps(body).encode()
        return response

    def test_json_key_exchanges_a_signed_jwt_without_iam_and_masks_the_access_token(self):
        console = io.StringIO()
        with patch('requests.sessions.Session.request', return_value=self.response({
            'access_token': 'fixture-access-token', 'expires_in': 3600, 'token_type': 'Bearer',
        })) as transport, redirect_stdout(console):
            cd.play_access_token(self.credentials)

        self.assertEqual(1, transport.call_count)
        request = transport.call_args.kwargs
        self.assertEqual('POST', transport.call_args.args[0])
        self.assertEqual('https://oauth2.googleapis.com/token', transport.call_args.args[1])
        form = parse_qs(request['data'].decode())
        self.assertEqual(['urn:ietf:params:oauth:grant-type:jwt-bearer'], form['grant_type'])
        claims = jwt.decode(form['assertion'][0], certs=self.public_key)
        self.assertEqual('play@example.iam.gserviceaccount.com', claims['iss'])
        self.assertEqual('https://oauth2.googleapis.com/token', claims['aud'])
        self.assertEqual('https://www.googleapis.com/auth/androidpublisher', claims['scope'])
        self.assertNotIn('sub', claims)
        self.assertEqual('::add-mask::fixture-access-token\n', console.getvalue())
        self.assertEqual('access_token=fixture-access-token\n', self.output.read_text())

    def test_oauth_failure_emits_no_token_or_credential_details(self):
        console = io.StringIO()
        with patch('requests.sessions.Session.request', return_value=self.response({
            'error': 'invalid_grant', 'error_description': 'fixture-sensitive-detail',
        }, status=400)), redirect_stdout(console):
            with self.assertRaisesRegex(cd.ReleaseError, '^Could not obtain a Play access token from the service-account key.$'):
                cd.play_access_token(self.credentials)
        self.assertEqual('', console.getvalue())
        self.assertFalse(self.output.exists())

    def test_invalid_key_fails_before_any_http_request(self):
        self.key_info['private_key'] = 'invalid-private-key'
        self.credentials.write_text(json.dumps(self.key_info))
        with patch('requests.sessions.Session.request') as transport:
            with self.assertRaisesRegex(cd.ReleaseError, '^Could not obtain a Play access token from the service-account key.$'):
                cd.play_access_token(self.credentials)
        transport.assert_not_called()
        self.assertFalse(self.output.exists())

    def test_multiline_token_is_rejected_before_workflow_output(self):
        console = io.StringIO()
        with patch('requests.sessions.Session.request', return_value=self.response({
            'access_token': 'fixture-token\ninjected-output', 'expires_in': 3600, 'token_type': 'Bearer',
        })), redirect_stdout(console):
            with self.assertRaisesRegex(cd.ReleaseError, '^Google returned an invalid Play access token.$'):
                cd.play_access_token(self.credentials)
        self.assertEqual('', console.getvalue())
        self.assertFalse(self.output.exists())

    def test_cli_reports_auth_failure_without_printing_the_key_or_traceback(self):
        self.key_info['private_key'] = 'fixture-sensitive-key'
        self.credentials.write_text(json.dumps(self.key_info))
        result = subprocess.run([
            sys.executable, '-B', str(Path(__file__).with_name('android_cd.py')),
            'play-token', '--credentials', str(self.credentials),
        ], capture_output=True, text=True)
        self.assertEqual(1, result.returncode)
        self.assertEqual('', result.stdout)
        self.assertEqual(
            'Android CD failed: Could not obtain a Play access token from the service-account key.\n',
            result.stderr,
        )


if __name__ == '__main__':
    unittest.main()
