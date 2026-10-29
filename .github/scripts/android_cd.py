#!/usr/bin/env python3
import argparse
import base64
import binascii
from dataclasses import asdict, dataclass
from enum import Enum
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile
import time
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from urllib.parse import urlencode
import xml.etree.ElementTree as ET


PACKAGE = 'dev.shounakmulay.devpulse'
MAX_VERSION_CODE = 2_100_000_000
BUNDLETOOL_URL = 'https://github.com/google/bundletool/releases/download/1.18.3/bundletool-all-1.18.3.jar'
BUNDLETOOL_SHA256 = 'a099cfa1543f55593bc2ed16a70a7c67fe54b1747bb7301f37fdfd6d91028e29'
ANDROID_NS = '{http://schemas.android.com/apk/res/android}'
ROOT = Path(__file__).resolve().parents[2]


class ReleaseError(ValueError):
    pass


@dataclass(frozen=True)
class Candidate:
    source_sha: str
    run_id: str
    run_number: int
    version_code: int
    version_name: str
    package_name: str
    aab_sha256: str
    signer_sha256: str

    @classmethod
    def parse(cls, raw):
        try:
            candidate = cls(**raw)
        except (TypeError, ValueError) as error:
            raise ReleaseError('Invalid candidate metadata fields.') from error
        if not isinstance(candidate.source_sha, str) or not re.fullmatch('[0-9a-f]{40}', candidate.source_sha):
            raise ReleaseError('Candidate source SHA is invalid.')
        if not isinstance(candidate.run_id, str) or not re.fullmatch('[1-9][0-9]*', candidate.run_id):
            raise ReleaseError('Candidate run ID is invalid.')
        for value in (candidate.version_code, candidate.run_number):
            if type(value) is not int or not 0 < value <= MAX_VERSION_CODE:
                raise ReleaseError('Candidate version or run number is invalid.')
        if not isinstance(candidate.version_name, str) or not re.fullmatch('[0-9A-Za-z._+-]{1,100}', candidate.version_name):
            raise ReleaseError('Candidate version name is invalid.')
        if candidate.package_name != PACKAGE:
            raise ReleaseError('Candidate package must be the production application.')
        for value in (candidate.aab_sha256, candidate.signer_sha256):
            if not isinstance(value, str) or not re.fullmatch('[0-9a-f]{64}', value):
                raise ReleaseError('Candidate digest or signer is invalid.')
        return candidate

    def json(self):
        return json.dumps(asdict(self), sort_keys=True, separators=(',', ':'))

    @property
    def tag(self):
        return f'internal/v{self.version_name}-build.{self.version_code}'


class PlayState(Enum):
    MISSING = 'missing'
    BUNDLE_ONLY = 'bundle-only'
    PUBLISHED = 'published'


def required(name):
    value = os.environ.get(name, '')
    if not value:
        raise ReleaseError(f'Missing environment setting {name}.')
    return value


def positive_integer(value, name, allow_zero=False):
    if not re.fullmatch('[0-9]+', value):
        raise ReleaseError(f'{name} must contain only decimal digits.')
    result = int(value)
    if not (0 if allow_zero else 1) <= result <= MAX_VERSION_CODE:
        raise ReleaseError(f'{name} is outside the supported Android version range.')
    return result


def version_code(track):
    if not isinstance(track, dict) or not isinstance(track.get('releases', []), list):
        raise ReleaseError('Play returned an invalid internal track.')
    codes = []
    for release in track.get('releases', []):
        if not isinstance(release, dict) or not isinstance(release.get('versionCodes', []), list):
            raise ReleaseError('Play returned invalid internal release version codes.')
        for code in release.get('versionCodes', []):
            if not isinstance(code, str):
                raise ReleaseError('Play returned an invalid internal version code.')
            codes.append(positive_integer(code, 'Play internal versionCode'))
    value = max(codes, default=0) + 1
    if value > MAX_VERSION_CODE:
        raise ReleaseError('Play internal versionCode has reached the Android limit.')
    return value


def next_play_version(token):
    base = f'https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}/edits'
    edit = json_request(base, token, 'POST', {})
    if not isinstance(edit, dict) or not re.fullmatch('[0-9A-Za-z_-]+', edit.get('id', '')):
        raise ReleaseError('Play returned an invalid edit ID.')
    url = f'{base}/{edit["id"]}'
    try:
        track = json_request(url + '/tracks/internal', token, missing_ok=True)
        version = version_code({'releases': []} if track is None else track)
        bundles = json_request(url + '/bundles', token)
        maximum = max((bundle['versionCode'] for bundle in bundles.get('bundles', [])), default=0)
        if version <= maximum:
            raise ReleaseError(f'Internal track selects versionCode {version}, but Play already contains bundle {maximum}. Resolve the track or bundle conflict in Play Console.')
        return version
    finally:
        json_request(url, token, 'DELETE')


def preflight():
    event = required('GITHUB_EVENT_NAME')
    ref = required('GITHUB_REF')
    if (event, ref) not in {('push', 'refs/heads/release'), ('workflow_dispatch', 'refs/heads/main')}:
        raise ReleaseError('Internal releases require a push to release or a manual run from main.')
    if not re.fullmatch('[0-9a-f]{40}', required('GITHUB_SHA')):
        raise ReleaseError('GITHUB_SHA must be a full source commit SHA.')


def output(name, value):
    if '\n' in str(value) or '\r' in str(value):
        raise ReleaseError('Workflow output must occupy one line.')
    path = os.environ.get('GITHUB_OUTPUT')
    if path:
        with open(path, 'a', encoding='utf-8') as handle:
            handle.write(f'{name}={value}\n')


def summary(value):
    path = os.environ.get('GITHUB_STEP_SUMMARY')
    if path:
        with open(path, 'a', encoding='utf-8') as handle:
            handle.write(value + '\n')


def sha256(path):
    with path.open('rb') as handle:
        digest = hashlib.sha256()
        for chunk in iter(lambda: handle.read(1024 * 1024), b''):
            digest.update(chunk)
        return digest.hexdigest()


def checked(command, root=ROOT, env=None, capture=True):
    result = subprocess.run(command, cwd=root, env=env, capture_output=capture)
    if result.returncode:
        raise ReleaseError(f'{Path(command[0]).name} failed with exit code {result.returncode}. See its job output when available.')
    return result.stdout if capture else b''


def certificate_fingerprint(value):
    normalized = value.replace(':', '').lower()
    if not re.fullmatch('[0-9a-f]{64}', normalized):
        raise ReleaseError('ANDROID_UPLOAD_CERT_SHA256 must be the SHA-256 upload certificate fingerprint.')
    return normalized


def property_value(value):
    escaped = []
    for character in value:
        if character in '\\ =:#!':
            escaped.append('\\' + character)
        elif character in '\n\r\t':
            escaped.append({'\n': '\\n', '\r': '\\r', '\t': '\\t'}[character])
        elif 32 <= ord(character) <= 126:
            escaped.append(character)
        else:
            encoded = character.encode('utf-16-be')
            escaped.extend(f'\\u{int.from_bytes(encoded[index:index + 2], "big"):04x}' for index in range(0, len(encoded), 2))
    return ''.join(escaped)


def private_write(path, content):
    descriptor = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    with os.fdopen(descriptor, 'wb') as handle:
        handle.write(content)


def signing_properties(directory, root):
    try:
        keystore_bytes = base64.b64decode(required('ANDROID_UPLOAD_KEYSTORE_BASE64'), validate=True)
    except (ValueError, binascii.Error) as error:
        raise ReleaseError('ANDROID_UPLOAD_KEYSTORE_BASE64 is not valid base64.') from error
    if not keystore_bytes:
        raise ReleaseError('Upload keystore is empty.')
    expected = certificate_fingerprint(required('ANDROID_UPLOAD_CERT_SHA256'))
    keystore = directory / 'upload.p12'
    private_write(keystore, keystore_bytes)
    certificate = checked([
        'keytool', '-exportcert', '-alias', required('ANDROID_UPLOAD_KEY_ALIAS'),
        '-keystore', str(keystore), '-storetype', 'PKCS12', '-storepass:env', 'ANDROID_UPLOAD_STORE_PASSWORD',
    ], root=root)
    if hashlib.sha256(certificate).hexdigest() != expected:
        raise ReleaseError('Upload keystore certificate does not match ANDROID_UPLOAD_CERT_SHA256.')
    properties = {
        'storeFile': str(keystore),
        'storeType': 'PKCS12',
        'storePassword': required('ANDROID_UPLOAD_STORE_PASSWORD'),
        'keyAlias': required('ANDROID_UPLOAD_KEY_ALIAS'),
        'keyPassword': required('ANDROID_UPLOAD_KEY_PASSWORD'),
    }
    path = directory / 'signing.properties'
    private_write(path, ''.join(f'{key}={property_value(value)}\n' for key, value in properties.items()).encode('ascii'))
    return path, expected


def bundletool(directory):
    path = directory / 'bundletool-1.18.3.jar'
    if not path.exists():
        with urlopen(BUNDLETOOL_URL, timeout=60) as response:
            private_write(path, response.read())
    if sha256(path) != BUNDLETOOL_SHA256:
        raise ReleaseError('Downloaded bundletool checksum does not match the pinned release.')
    return path


def validate_bundle(aab, tool, version, version_name, expected_signer, root):
    verified = checked(['jarsigner', '-J-Duser.language=en', '-verify', '-verbose', '-certs', str(aab)], root=root).decode()
    if 'jar verified.' not in verified or 'unsigned entries' in verified:
        raise ReleaseError('Release bundle is unsigned or contains unsigned entries.')
    certificate = checked(['keytool', '-printcert', '-jarfile', str(aab), '-rfc'], root=root).decode()
    pem = re.search(r'-----BEGIN CERTIFICATE-----\s*(.*?)\s*-----END CERTIFICATE-----', certificate, re.DOTALL)
    if pem is None or hashlib.sha256(base64.b64decode(re.sub(r'\s', '', pem[1]), validate=True)).hexdigest() != expected_signer:
        raise ReleaseError('Release bundle signer does not match the expected upload certificate.')
    checked(['java', '-jar', str(tool), 'validate', f'--bundle={aab}'], root=root)
    manifest = checked(['java', '-jar', str(tool), 'dump', 'manifest', '--module=base', f'--bundle={aab}'], root=root)
    validate_manifest(manifest, version, version_name)


def validate_manifest(xml, version, version_name):
    try:
        manifest = ET.fromstring(xml)
    except ET.ParseError as error:
        raise ReleaseError('Bundle manifest could not be parsed.') from error
    expected = {'package': PACKAGE, ANDROID_NS + 'versionCode': str(version), ANDROID_NS + 'versionName': version_name}
    if any(manifest.get(key) != value for key, value in expected.items()):
        raise ReleaseError('Bundle manifest package, versionCode or versionName does not match the candidate.')
    application = manifest.find('application')
    if application is None or application.get(ANDROID_NS + 'debuggable', 'false') != 'false':
        raise ReleaseError('Release bundle must contain a non-debuggable application.')


def verify_production(root):
    checked([
        './gradlew', ':androidApp:lintProductionRelease', ':core:data:db:jvmTest',
        ':core:data:feed:jvmTest', ':core:network:jvmTest', '-Pbuildkonfig.flavor=production',
        '--no-configuration-cache', '--continue', '--console=plain',
    ], root=root, capture=False)


def catalog_versions(path):
    versions = {}
    in_versions = False
    for line in path.read_text().splitlines():
        text = line.strip()
        if text.startswith('['):
            in_versions = text == '[versions]'
        if in_versions:
            match = re.fullmatch(r'(app-versionCode|app-versionName)\s*=\s*"([^"\\]*)"\s*(?:#.*)?', text)
            if match:
                if match[1] in versions:
                    raise ReleaseError('Duplicate app version in the catalog.')
                versions[match[1]] = match[2]
    if set(versions) != {'app-versionCode', 'app-versionName'}:
        raise ReleaseError('Catalog must define quoted app-versionCode and app-versionName values in [versions].')
    positive_integer(versions['app-versionCode'], 'Catalog app-versionCode')
    if not re.fullmatch('[0-9A-Za-z._+-]{1,100}', versions['app-versionName']):
        raise ReleaseError('Catalog app-versionName is invalid.')
    return versions


def build(root, destination):
    preflight()
    source = checked(['git', 'rev-parse', 'HEAD'], root=root).decode().strip()
    if source != required('GITHUB_SHA'):
        raise ReleaseError('Checked-out source does not match GITHUB_SHA.')
    if destination.is_dir() and not any(destination.iterdir()):
        destination.rmdir()
    if destination.exists():
        candidate = load_candidate(destination, source, required('GITHUB_RUN_ID'))
        if candidate.signer_sha256 != certificate_fingerprint(required('ANDROID_UPLOAD_CERT_SHA256')):
            raise ReleaseError('Restored candidate signer differs from the configured upload certificate.')
        output('candidate', candidate.json())
        output('reused', 'true')
        summary(f"Reusing this workflow run's verified candidate versionCode {candidate.version_code}.")
        return
    versions = catalog_versions(root / 'gradle/libs.versions.toml')
    version = next_play_version(required('PLAY_ACCESS_TOKEN'))
    runner_temp = Path(required('RUNNER_TEMP'))
    tool = bundletool(runner_temp)
    destination.mkdir(parents=True, exist_ok=False)
    with tempfile.TemporaryDirectory(prefix='devpulse-signing-', dir=runner_temp) as signing:
        properties, signer = signing_properties(Path(signing), root)
        environment = os.environ.copy()
        environment['DEVPULSE_SIGNING_PROPERTIES'] = str(properties)
        environment['DEVPULSE_VERSION_CODE'] = str(version)
        environment['GRADLE_OPTS'] = environment.get('GRADLE_OPTS', '') + ' -Dorg.gradle.configuration-cache=false'
        checked(['bash', 'scripts/build-android.sh', 'production', 'release', 'aab'], root=root, env=environment, capture=False)
        artifacts = list((root / 'androidApp/build/outputs/bundle/productionRelease').glob('*.aab'))
        if len(artifacts) != 1:
            raise ReleaseError('Expected exactly one production release AAB.')
        aab = artifacts[0]
        validate_bundle(aab, tool, version, versions['app-versionName'], signer, root)
        candidate = Candidate.parse({
            'source_sha': source, 'run_id': required('GITHUB_RUN_ID'),
            'run_number': int(required('GITHUB_RUN_NUMBER')), 'version_code': version,
            'version_name': versions['app-versionName'], 'package_name': PACKAGE,
            'aab_sha256': sha256(aab), 'signer_sha256': signer,
        })
        shutil.copyfile(aab, destination / 'app.aab')
        (destination / 'candidate.json').write_text(candidate.json() + '\n')
    summary(release_notes(candidate) + '\n\nThe build passed artifact validation. Device smoke and Play installation have not been run by this workflow.')


def load_candidate(directory, expected_sha, expected_run):
    candidate = Candidate.parse(json.loads((directory / 'candidate.json').read_text()))
    if candidate.source_sha != expected_sha or candidate.run_id != expected_run:
        raise ReleaseError('Candidate provenance does not match this workflow run.')
    if sha256(directory / 'app.aab') != candidate.aab_sha256:
        raise ReleaseError('Candidate AAB checksum does not match candidate.json.')
    return candidate


def auth_mode():
    key = bool(os.environ.get('PLAY_SERVICE_ACCOUNT_JSON', ''))
    provider = bool(os.environ.get('GCP_WORKLOAD_IDENTITY_PROVIDER', ''))
    account = bool(os.environ.get('GCP_SERVICE_ACCOUNT', ''))
    if provider != account or key == provider:
        raise ReleaseError('Configure exactly one Play authentication method: service-account JSON, or both WIF provider and service account.')
    output('mode', 'json' if key else 'oidc')


def play_access_token(credentials_file):
    from google.auth.exceptions import GoogleAuthError
    from google.auth.transport.requests import Request as GoogleAuthRequest
    from google.oauth2 import service_account

    try:
        credentials = service_account.Credentials.from_service_account_file(
            str(credentials_file), scopes=['https://www.googleapis.com/auth/androidpublisher'],
        )
        credentials.refresh(GoogleAuthRequest())
    except (GoogleAuthError, ValueError, KeyError) as error:
        raise ReleaseError('Could not obtain a Play access token from the service-account key.') from error
    token = credentials.token
    if not token or '\n' in token or '\r' in token:
        raise ReleaseError('Google returned an invalid Play access token.')
    print(f'::add-mask::{token}')
    output('access_token', token)


def json_request(url, token, method='GET', payload=None, missing_ok=False):
    body = None if payload is None else json.dumps(payload).encode()
    request = Request(url, data=body, method=method, headers={
        'Authorization': f'Bearer {token}', 'Content-Type': 'application/json',
        'Accept': 'application/json', 'User-Agent': 'DevPulse-Android-CD',
    })
    for attempt in range(3):
        try:
            with urlopen(request, timeout=30) as response:
                raw = response.read()
                return json.loads(raw) if raw else None
        except HTTPError as error:
            if missing_ok and error.code == 404:
                return None
            if method == 'GET' and (error.code == 429 or error.code >= 500) and attempt < 2:
                time.sleep(2 ** attempt)
                continue
            raise ReleaseError(f'{method} API request failed with HTTP {error.code}.') from error
    raise ReleaseError('API request failed.')


def play_state(candidate, bundles, track):
    candidates = [bundle for bundle in bundles.get('bundles', []) if bundle.get('versionCode') == candidate.version_code]
    if not candidates:
        maximum = max((bundle['versionCode'] for bundle in bundles.get('bundles', [])), default=0)
        if candidate.version_code <= maximum:
            raise ReleaseError('Candidate versionCode does not exceed existing Play bundles. Resolve the Play track or bundle conflict and start a new run.')
        return PlayState.MISSING
    if len(candidates) != 1 or candidates[0].get('sha256', '').lower() != candidate.aab_sha256:
        raise ReleaseError('Play has a different bundle for this versionCode. Start a new run with a higher versionCode.')
    releases = (track or {}).get('releases', [])
    for release in releases:
        if str(candidate.version_code) in release.get('versionCodes', []) and release.get('status') == 'completed':
            return PlayState.PUBLISHED
    return PlayState.BUNDLE_ONLY


def read_play_state(candidate, token):
    base = f'https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{candidate.package_name}/edits'
    edit = json_request(base, token, 'POST', {})
    if not isinstance(edit, dict) or not re.fullmatch('[0-9A-Za-z_-]+', edit.get('id', '')):
        raise ReleaseError('Play returned an invalid edit ID.')
    url = f'{base}/{edit["id"]}'
    try:
        bundles = json_request(url + '/bundles', token)
        track = json_request(url + '/tracks/internal', token, missing_ok=True)
        return play_state(candidate, bundles, track)
    finally:
        json_request(url, token, 'DELETE')


def play_check(directory, require_published):
    candidate = load_candidate(directory, required('GITHUB_SHA'), required('GITHUB_RUN_ID'))
    attempts = 6 if require_published else 1
    for attempt in range(attempts):
        state = read_play_state(candidate, required('PLAY_ACCESS_TOKEN'))
        if state == PlayState.PUBLISHED:
            output('published', 'true')
            summary(f'Google Play independently confirmed versionCode {candidate.version_code}, the exact AAB SHA-256 and completed internal-track status.')
            return
        if not require_published:
            if state == PlayState.BUNDLE_ONLY:
                raise ReleaseError('Play has this bundle but no completed internal release. Resolve the partial release in Play Console or start a new run. Nothing is marked published.')
            output('published', 'false')
            return
        if attempt < attempts - 1:
            time.sleep(5)
    raise ReleaseError('Play did not confirm the exact bundle on the completed internal track. No GitHub prerelease will be recorded.')


def release_notes(candidate):
    repository = required('GITHUB_REPOSITORY')
    return '\n'.join([
        f'Production Android internal testing build {candidate.version_name} ({candidate.version_code}).', '',
        f'Source: https://github.com/{repository}/commit/{candidate.source_sha}',
        f'Workflow: https://github.com/{repository}/actions/runs/{candidate.run_id}',
        f'Package: `{candidate.package_name}`', f'AAB SHA-256: `{candidate.aab_sha256}`',
        f'Upload certificate SHA-256: `{candidate.signer_sha256}`', '',
        'Binary distribution is through Google Play internal testing. This prerelease contains no binary assets.',
    ])


def record(candidate):
    repository = required('GITHUB_REPOSITORY')
    if not re.fullmatch('[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+', repository):
        raise ReleaseError('Invalid GitHub repository.')
    if candidate.source_sha != required('GITHUB_SHA') or candidate.run_id != required('GITHUB_RUN_ID'):
        raise ReleaseError('Prerelease candidate provenance does not match this workflow run.')
    token = required('GH_TOKEN')
    base = f'https://api.github.com/repos/{repository}'
    tag_ref = json_request(f'{base}/git/ref/tags/{candidate.tag}', token, missing_ok=True)
    if tag_ref is not None:
        target = tag_ref['object']
        if target['type'] != 'tag':
            raise ReleaseError('Existing release tag is not annotated. It will not be replaced.')
        target = json_request(f'{base}/git/tags/{target["sha"]}', token)['object']
        if target['type'] != 'commit' or target['sha'] != candidate.source_sha:
            raise ReleaseError('Existing release tag points at different source. It will not be moved.')
    body = release_notes(candidate)
    if tag_ref is None:
        annotated = json_request(f'{base}/git/tags', token, 'POST', {
            'tag': candidate.tag, 'message': body, 'object': candidate.source_sha, 'type': 'commit',
        })
        json_request(f'{base}/git/refs', token, 'POST', {
            'ref': f'refs/tags/{candidate.tag}', 'sha': annotated['sha'],
        })
    release = json_request(f'{base}/releases/tags/{candidate.tag}', token, missing_ok=True)
    if release is not None:
        if tag_ref is None or release.get('body') != body or release.get('prerelease') is not True or release.get('draft') is not False or release.get('assets'):
            raise ReleaseError('Existing prerelease metadata differs. It will not be overwritten.')
    else:
        release = json_request(f'{base}/releases', token, 'POST', {
            'tag_name': candidate.tag, 'target_commitish': candidate.source_sha,
            'name': f'Android internal {candidate.version_name} ({candidate.version_code})',
            'body': body, 'draft': False, 'prerelease': True, 'make_latest': 'false',
        })
    summary(f'GitHub prerelease: {release["html_url"]}')


def reconcile_version(candidate):
    repository = required('GITHUB_REPOSITORY')
    if not re.fullmatch('[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+', repository):
        raise ReleaseError('Invalid GitHub repository.')
    if candidate.source_sha != required('GITHUB_SHA') or candidate.run_id != required('GITHUB_RUN_ID'):
        raise ReleaseError('Version reconciliation provenance does not match this workflow run.')
    token = required('GH_TOKEN')
    base = f'https://api.github.com/repos/{repository}'
    main = json_request(base + '/git/ref/heads/main', token)['object']['sha']
    path = '/contents/gradle/libs.versions.toml'
    original = json_request(base + path + '?ref=' + main, token)
    content = base64.b64decode(original['content']).decode('utf-8')
    pattern = r'(?m)^(app-versionCode\s*=\s*")([0-9]+)("\s*(?:#.*)?)$'
    updated, count = re.subn(pattern, lambda match: match[1] + str(candidate.version_code) + match[3], content)
    if count != 1:
        raise ReleaseError('Main must define exactly one app-versionCode.')
    current = re.search(pattern, content)
    current_code = int(current[2])
    if current_code >= candidate.version_code:
        summary(f'Main already records versionCode {current_code}; no reconciliation is needed.')
        return
    branch = f'chore/android-version-code-{candidate.version_code}'
    ref = json_request(base + '/git/ref/heads/' + branch, token, missing_ok=True)
    if ref is None:
        json_request(base + '/git/refs', token, 'POST', {'ref': 'refs/heads/' + branch, 'sha': main})
        ref = {'object': {'sha': main}}
    existing = json_request(base + path + '?' + urlencode({'ref': branch}), token)
    branch_content = base64.b64decode(existing['content']).decode('utf-8')
    if branch_content != updated:
        if ref['object']['sha'] != main or branch_content != content:
            raise ReleaseError('Existing version reconciliation branch differs. It will not be overwritten.')
        json_request(base + path, token, 'PUT', {
            'message': f'chore: record Android internal version code {candidate.version_code}',
            'content': base64.b64encode(updated.encode('utf-8')).decode('ascii'),
            'sha': existing['sha'], 'branch': branch,
        })
    comparison = json_request(base + '/compare/main...' + branch, token)
    if [file['filename'] for file in comparison.get('files', [])] != ['gradle/libs.versions.toml']:
        raise ReleaseError('Version reconciliation must change only the version catalog.')
    pulls = json_request(base + '/pulls?' + urlencode({
        'state': 'open', 'base': 'main', 'head': repository.split('/')[0] + ':' + branch,
    }), token)
    if pulls:
        pull = pulls[0]
    else:
        pull = json_request(base + '/pulls', token, 'POST', {
            'title': f'chore: record Android internal version code {candidate.version_code}',
            'head': branch, 'base': 'main',
            'body': f'Google Play confirmed internal versionCode {candidate.version_code}. Update the catalog to match the published bundle.\n\n' + release_notes(candidate),
        })
    summary(f'Version reconciliation PR: {pull["html_url"]}')
    open_pulls = json_request(base + '/pulls?state=open&base=main&per_page=100', token)
    for older in open_pulls:
        branch_match = re.fullmatch(r'chore/android-version-code-([0-9]+)', older.get('head', {}).get('ref', ''))
        if branch_match and older.get('head', {}).get('repo', {}).get('full_name') == repository and int(branch_match[1]) < candidate.version_code:
            json_request(base + '/pulls/' + str(older['number']), token, 'PATCH', {'state': 'closed'})


def main():
    parser = argparse.ArgumentParser(description='Build and verify a production Android internal-testing candidate.')
    parser.add_argument('--root', type=Path, default=ROOT)
    commands = parser.add_subparsers(dest='command', required=True)
    commands.add_parser('preflight')
    commands.add_parser('verify-production')
    commands.add_parser('auth-mode')
    play_token = commands.add_parser('play-token')
    play_token.add_argument('--credentials', type=Path, required=True)
    builder = commands.add_parser('build')
    builder.add_argument('--destination', type=Path, required=True)
    for name in ('validate', 'play-check'):
        command = commands.add_parser(name)
        command.add_argument('--directory', type=Path, required=True)
        if name == 'play-check':
            command.add_argument('--require-published', action='store_true')
    record_command = commands.add_parser('record')
    record_command.add_argument('--directory', type=Path, required=True)
    reconcile_command = commands.add_parser('reconcile-version')
    reconcile_command.add_argument('--directory', type=Path, required=True)
    args = parser.parse_args()
    try:
        if args.command == 'preflight':
            preflight()
        elif args.command == 'verify-production':
            verify_production(args.root)
        elif args.command == 'build':
            build(args.root, args.destination)
        elif args.command == 'validate':
            load_candidate(args.directory, required('GITHUB_SHA'), required('GITHUB_RUN_ID'))
        elif args.command == 'auth-mode':
            auth_mode()
        elif args.command == 'play-token':
            play_access_token(args.credentials)
        elif args.command == 'play-check':
            play_check(args.directory, args.require_published)
        elif args.command == 'reconcile-version':
            reconcile_version(load_candidate(args.directory, required('GITHUB_SHA'), required('GITHUB_RUN_ID')))
        elif args.command == 'record':
            record(load_candidate(args.directory, required('GITHUB_SHA'), required('GITHUB_RUN_ID')))
    except (ReleaseError, OSError, json.JSONDecodeError, ET.ParseError) as error:
        print(f'Android CD failed: {error}', file=sys.stderr)
        return 1
    return 0


if __name__ == '__main__':
    sys.exit(main())
