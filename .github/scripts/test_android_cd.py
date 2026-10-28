#!/usr/bin/env python3
import base64
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch


SCRIPT = Path(__file__).with_name('android_cd.py')
SPEC = importlib.util.spec_from_file_location('android_cd', SCRIPT)
cd = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = cd
SPEC.loader.exec_module(cd)
SOURCE = 'a' * 40
CERTIFICATE = b'fixture certificate bytes'
SIGNER = hashlib.sha256(CERTIFICATE).hexdigest()


class AndroidCdTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='devpulse-cd-test-')
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.output = self.root / 'output'
        self.environment = {
            'GITHUB_EVENT_NAME': 'workflow_dispatch', 'GITHUB_REF': 'refs/heads/main',
            'GITHUB_SHA': SOURCE, 'GITHUB_RUN_NUMBER': '9', 'GITHUB_RUN_ID': '123',
            'GITHUB_REPOSITORY': 'example/DevPulse', 'GITHUB_OUTPUT': str(self.output),
            'RUNNER_TEMP': str(self.root), 'PLAY_ACCESS_TOKEN': 'fixture play token',
            'ANDROID_UPLOAD_CERT_SHA256': SIGNER,
            'ANDROID_UPLOAD_KEYSTORE_BASE64': base64.b64encode(b'private keystore bytes').decode(),
            'ANDROID_UPLOAD_STORE_PASSWORD': 'store secret', 'ANDROID_UPLOAD_KEY_ALIAS': 'upload',
            'ANDROID_UPLOAD_KEY_PASSWORD': 'key secret', 'GH_TOKEN': 'fixture token',
        }
        self.env_patch = patch.dict(os.environ, self.environment, clear=True)
        self.env_patch.start()
        self.addCleanup(self.env_patch.stop)

    def candidate(self):
        return cd.Candidate(SOURCE, '123', 9, 109, '1.0.0', cd.PACKAGE, hashlib.sha256(b'candidate bytes').hexdigest(), SIGNER)

    def write_candidate(self):
        directory = self.root / 'candidate'
        directory.mkdir()
        (directory / 'app.aab').write_bytes(b'candidate bytes')
        (directory / 'candidate.json').write_text(self.candidate().json())
        return directory

    def test_given_internal_releases_when_allocating_then_only_highest_code_matters(self):
        self.assertEqual(7, cd.version_code({'releases': [{'versionCodes': ['4', '6']}, {'versionCodes': ['5']}]}))
        self.assertEqual(1, cd.version_code({}))
        self.assertEqual(1, cd.version_code({'releases': []}))
        self.assertEqual(2_100_000_000, cd.version_code({'releases': [{'versionCodes': ['2099999999']}]}))
        for track in [None, {'releases': None}, {'releases': [None]},
                      {'releases': [{'versionCodes': [7]}]},
                      {'releases': [{'versionCodes': ['invalid']}]},
                      {'releases': [{'versionCodes': ['2100000000']}]}]:
            with self.subTest(track=track), self.assertRaises(cd.ReleaseError):
                cd.version_code(track)

    def test_given_play_track_when_selecting_then_edit_is_deleted_on_success_or_collision(self):
        for maximum in (6, 7):
            calls = []
            def api(url, token, method='GET', payload=None, missing_ok=False):
                calls.append((url, method))
                if method == 'POST':
                    return {'id': 'edit-1'}
                if url.endswith('/tracks/internal'):
                    return {'releases': [{'versionCodes': ['6']}]}
                if url.endswith('/bundles'):
                    return {'bundles': [{'versionCode': maximum}]}
            with patch.object(cd, 'json_request', side_effect=api):
                if maximum == 6:
                    self.assertEqual(7, cd.next_play_version('token'))
                else:
                    with self.assertRaisesRegex(cd.ReleaseError, 'already contains bundle 7'):
                        cd.next_play_version('token')
            self.assertTrue(calls[-1][0].endswith('/edit-1'))
            self.assertEqual('DELETE', calls[-1][1])

    def test_given_missing_or_failed_track_when_selecting_then_cleanup_is_guaranteed(self):
        for state in ('missing', 'failure', 'invalid'):
            calls = []
            def api(url, token, method='GET', payload=None, missing_ok=False):
                calls.append((url, method))
                if method == 'POST':
                    return {'id': 'edit-1'}
                if url.endswith('/tracks/internal'):
                    self.assertTrue(missing_ok)
                    if state == 'failure':
                        raise cd.ReleaseError('fixture API failure')
                    return None if state == 'missing' else {'releases': None}
                if url.endswith('/bundles'):
                    return {'bundles': []}
            with patch.object(cd, 'json_request', side_effect=api):
                if state == 'missing':
                    self.assertEqual(1, cd.next_play_version('token'))
                else:
                    with self.assertRaises(cd.ReleaseError):
                        cd.next_play_version('token')
            self.assertEqual('DELETE', calls[-1][1])

    def test_cli_rejects_untrusted_event_and_ref_without_starting_external_tools(self):
        for event, ref, expected in [
            ('workflow_dispatch', 'refs/heads/main', 0),
            ('push', 'refs/heads/release', 0),
            ('workflow_dispatch', 'refs/heads/release', 1),
            ('push', 'refs/heads/feature', 1),
            ('pull_request', 'refs/pull/1/merge', 1),
            ('workflow_dispatch', 'refs/tags/v1', 1),
        ]:
            with self.subTest(event=event, ref=ref):
                env = dict(os.environ, GITHUB_EVENT_NAME=event, GITHUB_REF=ref, PATH='')
                result = subprocess.run([sys.executable, '-B', str(SCRIPT), 'preflight'], env=env, capture_output=True)
                self.assertEqual(expected, result.returncode, result.stderr)

    def test_candidate_validation_detects_modified_bundle_and_different_run(self):
        directory = self.write_candidate()
        self.assertEqual(self.candidate(), cd.load_candidate(directory, SOURCE, '123'))
        for sha, run in [('b' * 40, '123'), (SOURCE, '124')]:
            with self.assertRaises(cd.ReleaseError):
                cd.load_candidate(directory, sha, run)
        (directory / 'app.aab').write_bytes(b'tampered')
        with self.assertRaises(cd.ReleaseError):
            cd.load_candidate(directory, SOURCE, '123')

    def test_candidate_rejects_wrong_package_invalid_metadata_and_extra_fields(self):
        original = json.loads(self.candidate().json())
        for change in [{'package_name': cd.PACKAGE + '.staging'}, {'version_code': True},
                       {'aab_sha256': 'not-a-digest'}, {'version_name': '1\n2'},
                       {'source_sha': 'main'}, {'unexpected': 'field'}]:
            with self.subTest(change=change), self.assertRaises(cd.ReleaseError):
                cd.Candidate.parse({**original, **change})
        self.assertEqual(self.candidate(), cd.Candidate.parse(original))

    def test_manifest_must_match_production_metadata_and_disable_debugging(self):
        def manifest(package=cd.PACKAGE, code='109', name='1.0.0', debug='false'):
            return f'<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="{package}" android:versionCode="{code}" android:versionName="{name}"><application android:debuggable="{debug}"/></manifest>'
        cd.validate_manifest(manifest(), 109, '1.0.0')
        for xml in [manifest(package=cd.PACKAGE + '.staging'), manifest(code='108'),
                    manifest(name='2.0.0'), manifest(debug='true'), '<invalid']:
            with self.subTest(xml=xml), self.assertRaises(cd.ReleaseError):
                cd.validate_manifest(xml, 109, '1.0.0')

    def test_signing_files_preserve_passwords_without_logging_them(self):
        directory = self.root / 'signing'
        directory.mkdir()
        os.environ['ANDROID_UPLOAD_KEY_PASSWORD'] = 'key\\:= secret\n\u00e9\U0001f955'
        with patch.object(cd, 'checked', return_value=CERTIFICATE) as boundary:
            properties, fingerprint = cd.signing_properties(directory, self.root)
        self.assertEqual(SIGNER, fingerprint)
        self.assertEqual(0o600, properties.stat().st_mode & 0o777)
        self.assertEqual(b'private keystore bytes', (directory / 'upload.p12').read_bytes())
        self.assertEqual(
            f'storeFile={directory}/upload.p12\nstoreType=PKCS12\nstorePassword=store\\ secret\nkeyAlias=upload\nkeyPassword=key\\\\\\:\\=\\ secret\\n\\u00e9\\ud83e\\udd55\n',
            properties.read_text(),
        )
        command = boundary.call_args[0][0]
        self.assertEqual(['-storepass:env', 'ANDROID_UPLOAD_STORE_PASSWORD'], command[-2:])
        self.assertEqual('PKCS12', command[command.index('-storetype') + 1])
        self.assertNotIn('store secret', command)
        self.assertNotIn('key secret', command)

    def test_bad_keystore_and_wrong_certificate_fail_before_gradle(self):
        directory = self.root / 'signing'
        directory.mkdir()
        os.environ['ANDROID_UPLOAD_KEYSTORE_BASE64'] = 'bad base64!'
        with self.assertRaises(cd.ReleaseError):
            cd.signing_properties(directory, self.root)
        os.environ['ANDROID_UPLOAD_KEYSTORE_BASE64'] = base64.b64encode(b'keystore').decode()
        with patch.object(cd, 'checked', return_value=b'wrong certificate'), self.assertRaises(cd.ReleaseError):
            cd.signing_properties(directory, self.root)

    def test_authentication_requires_exactly_one_complete_method(self):
        os.environ['PLAY_SERVICE_ACCOUNT_JSON'] = '{"type":"service_account"}'
        cd.auth_mode()
        self.assertEqual('mode=json\n', self.output.read_text())
        self.output.unlink()
        del os.environ['PLAY_SERVICE_ACCOUNT_JSON']
        os.environ['GCP_WORKLOAD_IDENTITY_PROVIDER'] = 'projects/1/locations/global/workloadIdentityPools/pool/providers/github'
        os.environ['GCP_SERVICE_ACCOUNT'] = 'play@example.iam.gserviceaccount.com'
        cd.auth_mode()
        self.assertEqual('mode=oidc\n', self.output.read_text())
        os.environ['PLAY_SERVICE_ACCOUNT_JSON'] = '{}'
        with self.assertRaises(cd.ReleaseError):
            cd.auth_mode()
        del os.environ['PLAY_SERVICE_ACCOUNT_JSON']
        del os.environ['GCP_SERVICE_ACCOUNT']
        with self.assertRaises(cd.ReleaseError):
            cd.auth_mode()

    def test_production_gate_runs_real_release_lint_and_existing_jvm_suites(self):
        with patch.object(cd, 'checked') as boundary:
            cd.verify_production(self.root)
        self.assertEqual([
            './gradlew', ':androidApp:lintProductionRelease', ':core:data:db:jvmTest',
            ':core:data:feed:jvmTest', ':core:network:jvmTest', '-Pbuildkonfig.flavor=production',
            '--no-configuration-cache', '--continue', '--console=plain',
        ], boundary.call_args[0][0])

    def test_build_uses_existing_helper_and_removes_private_signing_files(self):
        (self.root / 'gradle').mkdir()
        (self.root / 'gradle/libs.versions.toml').write_text('[versions]\napp-versionCode = "200"\napp-versionName = "1.0.0"\n')
        artifact = self.root / 'androidApp/build/outputs/bundle/productionRelease/production.aab'
        artifact.parent.mkdir(parents=True)
        artifact.write_bytes(b'candidate bytes')
        destination = self.root / 'built-candidate'
        calls = []
        signing_paths = []

        def external(command, root, env=None, capture=True):
            calls.append(command)
            if command[:2] == ['git', 'rev-parse']:
                return SOURCE.encode() + b'\n'
            if command[0] == 'keytool':
                return CERTIFICATE
            if command[0] == 'bash':
                self.assertEqual(['bash', 'scripts/build-android.sh', 'production', 'release', 'aab'], command)
                self.assertEqual('109', env['DEVPULSE_VERSION_CODE'])
                self.assertIn('-Dorg.gradle.configuration-cache=false', env['GRADLE_OPTS'])
                properties = Path(env['DEVPULSE_SIGNING_PROPERTIES'])
                self.assertTrue(properties.is_file())
                signing_paths.append(properties.parent)
                return b''
            self.fail(f'Unexpected external operation {command}')

        with patch.object(cd, 'next_play_version', return_value=109), patch.object(cd, 'checked', side_effect=external), patch.object(cd, 'bundletool', return_value=self.root / 'tool.jar'), patch.object(cd, 'validate_bundle') as validator:
            cd.build(self.root, destination)
        self.assertEqual(self.candidate(), cd.load_candidate(destination, SOURCE, '123'))
        self.assertFalse(signing_paths[0].exists())
        self.assertEqual(artifact, validator.call_args[0][0])
        self.assertEqual('109', str(validator.call_args[0][2]))
        self.assertEqual(self.candidate().json(), self.output.read_text().strip()[len('candidate='):])
        self.assertEqual(1, sum(command[0] == 'bash' for command in calls))

    def test_given_restored_candidate_when_retrying_build_then_reuse_without_allocating(self):
        directory = self.write_candidate()
        with patch.object(cd, 'checked', return_value=SOURCE.encode()), patch.object(cd, 'next_play_version') as allocator:
            cd.build(self.root, directory)
        allocator.assert_not_called()
        self.assertIn('reused=true', self.output.read_text())
        self.assertIn(self.candidate().json(), self.output.read_text())
        (directory / 'app.aab').write_bytes(b'tampered')
        with patch.object(cd, 'checked', return_value=SOURCE.encode()), self.assertRaises(cd.ReleaseError):
            cd.build(self.root, directory)

    def test_given_empty_restore_directory_when_retrying_then_allocate_a_new_candidate(self):
        directory = self.root / 'empty-restore'
        directory.mkdir()
        (self.root / 'gradle').mkdir()
        (self.root / 'gradle/libs.versions.toml').write_text('[versions]\napp-versionCode = "7"\napp-versionName = "1.0.0"\n')
        with patch.object(cd, 'checked', return_value=SOURCE.encode()), patch.object(cd, 'next_play_version', side_effect=cd.ReleaseError('allocation reached')) as allocator:
            with self.assertRaisesRegex(cd.ReleaseError, 'allocation reached'):
                cd.build(self.root, directory)
        allocator.assert_called_once_with('fixture play token')
        self.assertFalse(directory.exists())
        directory.mkdir()
        (directory / 'partial').write_text('incomplete download')
        with patch.object(cd, 'checked', return_value=SOURCE.encode()), patch.object(cd, 'next_play_version') as allocator:
            with self.assertRaises(OSError):
                cd.build(self.root, directory)
        allocator.assert_not_called()

    def test_build_failure_also_removes_private_signing_files(self):
        (self.root / 'gradle').mkdir()
        (self.root / 'gradle/libs.versions.toml').write_text('[versions]\napp-versionCode = "1"\napp-versionName = "1.0.0"\n')
        def external(command, **kwargs):
            if command[0] == 'git':
                return SOURCE.encode()
            if command[0] == 'keytool':
                return CERTIFICATE
            raise cd.ReleaseError('fixture build failed')
        with patch.object(cd, 'next_play_version', return_value=109), patch.object(cd, 'checked', side_effect=external), patch.object(cd, 'bundletool', return_value=self.root / 'tool.jar'), self.assertRaises(cd.ReleaseError):
            cd.build(self.root, self.root / 'failed-candidate')
        self.assertEqual([], list(self.root.glob('devpulse-signing-*')))
        self.assertFalse(self.output.exists())

    def test_play_readback_requires_matching_digest_and_completed_internal_release(self):
        candidate = self.candidate()
        bundle = {'bundles': [{'versionCode': 109, 'sha256': candidate.aab_sha256}]}
        self.assertEqual(cd.PlayState.MISSING, cd.play_state(candidate, {'bundles': [{'versionCode': 108}]}, None))
        self.assertEqual(cd.PlayState.BUNDLE_ONLY, cd.play_state(candidate, bundle, {'releases': [{'versionCodes': ['109'], 'status': 'draft'}]}))
        self.assertEqual(cd.PlayState.PUBLISHED, cd.play_state(candidate, bundle, {'releases': [{'versionCodes': ['109'], 'status': 'completed'}]}))
        with self.assertRaises(cd.ReleaseError):
            cd.play_state(candidate, {'bundles': [{'versionCode': 109, 'sha256': 'b' * 64}]}, {})
        with self.assertRaises(cd.ReleaseError):
            cd.play_state(candidate, {'bundles': [{'versionCode': 110}]}, {})

    def test_play_edit_is_deleted_even_when_readback_rejects_digest(self):
        requests = []
        def api(url, token, method='GET', payload=None, missing_ok=False):
            requests.append((url, method, payload))
            if method == 'POST':
                return {'id': 'edit-1'}
            if url.endswith('/bundles'):
                return {'bundles': [{'versionCode': 109, 'sha256': 'b' * 64}]}
            return None
        with patch.object(cd, 'json_request', side_effect=api), self.assertRaises(cd.ReleaseError):
            cd.read_play_state(self.candidate(), 'token')
        self.assertEqual(('https://androidpublisher.googleapis.com/androidpublisher/v3/applications/dev.shounakmulay.devpulse/edits/edit-1', 'DELETE', None), requests[-1])

    def test_rerun_recognizes_published_candidate_but_rejects_partial_release(self):
        directory = self.write_candidate()
        os.environ['PLAY_ACCESS_TOKEN'] = 'test access token'
        with patch.object(cd, 'read_play_state', return_value=cd.PlayState.PUBLISHED):
            cd.play_check(directory, False)
        self.assertEqual('published=true\n', self.output.read_text())
        self.output.unlink()
        with patch.object(cd, 'read_play_state', return_value=cd.PlayState.BUNDLE_ONLY), self.assertRaises(cd.ReleaseError):
            cd.play_check(directory, False)
        self.assertFalse(self.output.exists())

    def test_post_upload_readback_waits_for_committed_state(self):
        directory = self.write_candidate()
        os.environ['PLAY_ACCESS_TOKEN'] = 'test access token'
        with patch.object(cd, 'read_play_state', side_effect=[cd.PlayState.MISSING, cd.PlayState.BUNDLE_ONLY, cd.PlayState.PUBLISHED]), patch.object(cd.time, 'sleep'):
            cd.play_check(directory, True)
        self.assertEqual('published=true\n', self.output.read_text())

    def test_prerelease_creation_targets_exact_source_without_binary_assets(self):
        writes = []
        def api(url, token, method='GET', payload=None, missing_ok=False):
            if method == 'POST':
                writes.append(payload)
                if url.endswith('/git/tags'):
                    return {'sha': 'd' * 40}
                if url.endswith('/git/refs'):
                    return {}
                return {'html_url': 'https://github.com/example/DevPulse/releases/tag/internal/v1.0.0-build.109'}
            return None
        candidate = self.candidate()
        with patch.object(cd, 'json_request', side_effect=api):
            cd.record(candidate)
        self.assertEqual([
            {'tag': candidate.tag, 'message': cd.release_notes(candidate), 'object': SOURCE, 'type': 'commit'},
            {'ref': 'refs/tags/' + candidate.tag, 'sha': 'd' * 40},
            {
            'tag_name': 'internal/v1.0.0-build.109', 'target_commitish': SOURCE,
            'name': 'Android internal 1.0.0 (109)', 'body': cd.release_notes(self.candidate()),
            'draft': False, 'prerelease': True, 'make_latest': 'false',
        }], writes)
        self.assertIn('AAB SHA-256: `' + self.candidate().aab_sha256 + '`', writes[-1]['body'])

    def test_prerelease_rerun_preserves_matching_record_and_rejects_mismatch(self):
        candidate = self.candidate()
        tag = {'object': {'type': 'tag', 'sha': 'd' * 40}}
        annotated = {'object': {'type': 'commit', 'sha': SOURCE}}
        release = {'body': cd.release_notes(candidate), 'prerelease': True, 'draft': False, 'assets': [], 'html_url': 'https://example.com/release'}
        with patch.object(cd, 'json_request', side_effect=[tag, annotated, release]) as api:
            cd.record(candidate)
        self.assertEqual(['GET', 'GET', 'GET'], [call[1].get('method', 'GET') for call in api.call_args_list])
        with patch.object(cd, 'json_request', side_effect=[tag, annotated, {**release, 'body': 'different'}]), self.assertRaises(cd.ReleaseError):
            cd.record(candidate)
        with patch.object(cd, 'json_request', return_value={'object': {'type': 'commit', 'sha': 'b' * 40}}), self.assertRaises(cd.ReleaseError):
            cd.record(candidate)

    def test_given_published_candidate_when_reconciling_then_pr_changes_only_main_version(self):
        original = '[versions]\napp-versionCode = "7"\napp-versionName = "1.0.3"\n'
        updated = original.replace('"7"', '"109"')
        for rerun in (False, True):
            writes = []
            def api(url, token, method='GET', payload=None, missing_ok=False):
                if method != 'GET':
                    writes.append((url, method, payload))
                    if url.endswith('/pulls'):
                        return {'html_url': 'https://example.com/pr/1'}
                    return {}
                if url.endswith('/git/ref/heads/main'):
                    return {'object': {'sha': SOURCE}}
                if '/git/ref/heads/chore/' in url:
                    return {'object': {'sha': 'b' * 40}} if rerun else None
                if '/contents/' in url:
                    content = updated if rerun and 'ref=chore' in url else original
                    return {'sha': 'c' * 40, 'content': base64.b64encode(content.encode()).decode()}
                if '/compare/' in url:
                    return {'files': [{'filename': 'gradle/libs.versions.toml'}]}
                if 'head=' in url:
                    return [{'html_url': 'https://example.com/pr/1'}] if rerun else []
                if '/pulls?' in url:
                    return [{'number': 2, 'head': {'ref': 'chore/android-version-code-108', 'repo': {'full_name': 'example/DevPulse'}}}]
                self.fail(url)
            with patch.object(cd, 'json_request', side_effect=api):
                cd.reconcile_version(self.candidate())
            puts = [payload for _, method, payload in writes if method == 'PUT']
            self.assertEqual(0 if rerun else 1, len(puts))
            if puts:
                self.assertEqual(updated, base64.b64decode(puts[0]['content']).decode())
                self.assertEqual('chore/android-version-code-109', puts[0]['branch'])
            prs = [payload for url, _, payload in writes if url.endswith('/pulls')]
            self.assertEqual(0 if rerun else 1, len(prs))
            if prs:
                self.assertEqual('main', prs[0]['base'])
            self.assertEqual(('PATCH', {'state': 'closed'}), writes[-1][1:])

    def test_given_main_matching_or_ahead_when_reconciling_then_no_writes(self):
        for version in (109, 110):
            original = f'[versions]\napp-versionCode = "{version}"\n'
            with patch.object(cd, 'json_request', side_effect=[
                {'object': {'sha': SOURCE}}, {'content': base64.b64encode(original.encode()).decode()},
            ]) as api:
                cd.reconcile_version(self.candidate())
            self.assertEqual(2, api.call_count)

    def test_given_modified_reconciliation_branch_when_reconciling_then_no_overwrite(self):
        original = '[versions]\napp-versionCode = "7"\n'
        with patch.object(cd, 'json_request', side_effect=[
            {'object': {'sha': SOURCE}}, {'content': base64.b64encode(original.encode()).decode()},
            {'object': {'sha': 'b' * 40}}, {'content': base64.b64encode(b'manual changes').decode()},
        ]) as api, self.assertRaisesRegex(cd.ReleaseError, 'will not be overwritten'):
            cd.reconcile_version(self.candidate())
        self.assertEqual(4, api.call_count)


if __name__ == '__main__':
    unittest.main()
