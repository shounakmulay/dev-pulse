#!/usr/bin/env python3
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shlex
import signal
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parent.parent


def git(root, *args):
    return subprocess.check_output(['git', '-C', str(root)] + list(args), stderr=subprocess.DEVNULL)


def changed_paths(root, base):
    result = set()
    commands = [('diff', '--name-only', '--no-renames', '-z', '--'),
                ('diff', '--cached', '--name-only', '--no-renames', '-z', '--'),
                ('ls-files', '--others', '--exclude-standard', '-z')]
    if base:
        commands.append(('diff', '--name-only', '--no-renames', '-z', base, 'HEAD', '--'))
    for command in commands:
        result.update(os.fsdecode(p) for p in git(root, *command).split(b'\0') if p)
    return result


def resolve_base(root, requested):
    if requested:
        commit = git(root, 'rev-parse', '--verify', '--end-of-options', requested + '^{commit}').decode().strip()
        return git(root, 'merge-base', 'HEAD', commit).decode().strip(), 'explicit base ' + requested
    try:
        ref = git(root, 'symbolic-ref', 'refs/remotes/origin/HEAD').decode().strip()
        return git(root, 'merge-base', 'HEAD', ref).decode().strip(), ref
    except subprocess.CalledProcessError:
        return None, 'No verified remote default branch; selecting all. Use --base REF.'


def select_modules(registry, paths, all_scopes):
    modules = registry['modules']
    selected = set(modules) if all_scopes else set()
    for path in paths:
        matches = [m for m, v in modules.items() if path.startswith(v['path'] + '/')]
        if matches:
            selected.update(matches)
        elif not (path.startswith(('docs/', '.agents/', '.claude/', '.cursor/', '.agent/'))
                  or path in ('AGENTS.md', 'CLAUDE.md', 'README.md', 'CONTEXT.md')):
            selected.update(modules)
    while True:
        consumers = {m for m, v in modules.items() if selected.intersection(v['dependencies'])}
        expanded = selected | consumers
        if expanded == selected:
            return sorted(selected)
        selected = expanded


def check_registry(root, registry):
    if registry['version'] != 1 or not registry['modules']:
        raise ValueError('Unsupported or empty verification registry')
    for module, item in registry['modules'].items():
        if not re.fullmatch(r'(?::[A-Za-z][A-Za-z0-9]*)+', module):
            raise ValueError('Invalid module identifier')
        if item['path'] != module[1:].replace(':', '/'):
            raise ValueError('Module path mismatch: ' + module)
        if not set(item['dependencies']).issubset(registry['modules']):
            raise ValueError('Unknown dependency: ' + module)
        if not (root / item['path'] / 'build.gradle.kts').is_file():
            raise ValueError('Missing module build: ' + module)
    actual = {str(p.relative_to(root)) for p in (root / 'buildSrc/src').rglob('*.kt')}
    includes = re.findall(r'include\("(:[^"\n]+)"\)', (root / 'settings.gradle.kts').read_text())
    if set(includes) != set(registry['modules']):
        raise ValueError('Included modules changed; re-audit the dependency graph')
    actual.update(v['path'] + '/build.gradle.kts' for v in registry['modules'].values())
    actual.update(('build.gradle.kts', 'buildSrc/build.gradle.kts'))
    actual.update(('settings.gradle.kts', 'buildSrc/settings.gradle.kts', 'gradle.properties',
                   'gradle/libs.versions.toml', 'gradle/wrapper/gradle-wrapper.properties',
                   'gradlew', 'gradlew.bat', 'gradle/wrapper/gradle-wrapper.jar'))
    if actual != set(registry['configuration']):
        raise ValueError('Build configuration file set changed; re-audit and refresh verification.json')
    for path, expected in registry['configuration'].items():
        if hashlib.sha256((root / path).read_bytes()).hexdigest() != expected:
            raise ValueError('Verification configuration drift: ' + path + '; re-audit before refreshing its digest')
    if (root / '.github/workflows').exists():
        raise ValueError('CI configuration added; audit parity before updating this runner')
    for path in registry['references'].values():
        if not (root / path).is_file():
            raise ValueError('Missing canonical reference: ' + path)
    check_guidance(root, registry)


def skill_metadata(path):
    text = path.read_text()
    frontmatter = text.split('---', 2)
    if len(frontmatter) != 3 or not text.startswith('---\n'):
        raise ValueError('Missing skill frontmatter: ' + str(path))
    lines = frontmatter[1].strip().splitlines()
    if len(lines) != 2 or any(':' not in line for line in lines):
        raise ValueError('Invalid skill metadata: ' + str(path))
    fields = dict((key.strip(), value.strip()) for key, value in (line.split(':', 1) for line in lines))
    if set(fields) != {'name', 'description'} or not fields['description'].startswith('Use when '):
        raise ValueError('Invalid skill metadata: ' + str(path))
    if len(text.splitlines()) > 150:
        raise ValueError('Skill exceeds 150 lines: ' + str(path))
    return fields


def check_links(path):
    for target in re.findall(r'\[[^\]]*\]\(([^)]+)\)', path.read_text()):
        if re.match(r'https?://', target) or target.startswith('#'):
            continue
        destination = (path.parent / target.split('#', 1)[0]).resolve()
        if not destination.exists():
            raise ValueError('Broken guidance link: ' + str(path) + ' -> ' + target)


def check_guidance(root, registry):
    instructions = (root / 'AGENTS.md').read_text()
    if len(instructions.splitlines()) > 60:
        raise ValueError('Root instructions exceed 60 lines')
    for name, workflow in registry['workflows'].items():
        canonical = root / workflow['path']
        adapter = root / workflow['adapter']
        if skill_metadata(canonical)['name'] != name:
            raise ValueError('Skill identity drift: ' + name)
        if skill_metadata(adapter) != skill_metadata(canonical):
            raise ValueError('Adapter metadata drift: ' + name)
        expected = '../../../' + workflow['path']
        if expected not in adapter.read_text() or (adapter.parent / expected).resolve() != canonical.resolve():
            raise ValueError('Adapter routing drift: ' + name)
        if name != 'ktlint-fix' and name not in instructions:
            raise ValueError('Missing root workflow route: ' + name)
        for source in workflow['sources']:
            if not (root / source).is_file():
                raise ValueError('Missing workflow source: ' + source)
        check_links(canonical)
    for path in registry['guidance']:
        if not (root / path).is_file():
            raise ValueError('Missing guidance: ' + path)
        check_links(root / path)
    claude = (root / '.claude/CLAUDE.md').read_text()
    if claude.strip() != '@../AGENTS.md':
        raise ValueError('Claude root guidance must import the canonical AGENTS.md')
    for path, names in registry['editor_adapters'].items():
        text = (root / path).read_text()
        for name in names:
            if '../../' + registry['workflows'][name]['path'] not in text:
                raise ValueError('Editor adapter routing drift: ' + path + ' -> ' + name)
    ignored = subprocess.run(['git', '-C', str(root), 'check-ignore', '--'] +
                             [item['path'] for item in registry['workflows'].values()],
                             stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if ignored.returncode not in (0, 1):
        raise ValueError('Could not verify skill tracking rules')
    if ignored.stdout:
        raise ValueError('Canonical skills are ignored by Git: ' + ignored.stdout.decode().strip())


def sanitize(text, root):
    text = re.sub(r'\x1b\[[0-9;]*[A-Za-z]', '', text)
    text = text.replace(str(root), '<repo>').replace(str(Path.home()), '<home>')
    return re.sub(r'(?i)((?:password|token|secret|authorization)[=:]\s*)\S+', r'\1<redacted>', text)


def check_tasks(registry, selected, catalog):
    known = set(re.findall(r'^([\w:]+)(?: -|$)', catalog, re.M))
    for module in selected:
        for task in registry['modules'][module]['tasks']:
            if module[1:] + ':' + task not in known:
                raise RuntimeError('Task drift: ' + module + ':' + task)


def check_graph(graph):
    unsafe = ('copyRoomSchemas', 'generateIosXcconfig', 'exportLibraryDefinitions',
              'syncSyntheticPackageResolvedToPersisted')
    if any(token in graph for token in unsafe):
        raise RuntimeError('Selected task graph may write tracked generated artifacts. Review it in an isolated checkout; no stage executed.')


def run_step(root, name, argv, timeout):
    print('==> ' + name, flush=True)
    process = subprocess.Popen(argv, cwd=str(root), stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                               start_new_session=True)
    try:
        output, _ = process.communicate(timeout=timeout)
    except (subprocess.TimeoutExpired, KeyboardInterrupt):
        os.killpg(process.pid, signal.SIGTERM)
        try:
            output, _ = process.communicate(timeout=10)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGKILL)
            output, _ = process.communicate()
        print('FAIL: interrupted/timed out; terminated owned process group', flush=True)
        raise RuntimeError('Stage did not complete: ' + name)
    clean = sanitize(output.decode(errors='replace'), root)
    if process.returncode:
        fd, log = tempfile.mkstemp(prefix='devpulse-verify-', suffix='.log')
        with os.fdopen(fd, 'w') as stream:
            stream.write(clean)
        print('\n'.join(clean.splitlines()[-40:]))
        raise RuntimeError('Stage failed: {}; exit {}; sanitized log {}'.format(name, process.returncode, log))
    print('PASS: ' + name, flush=True)
    return clean


def stages(root, registry, selected):
    result, skipped = [], []
    for platform, flag in [('jvm', None), ('android', 'DEVPULSE_VERIFY_ANDROID'),
                           ('ios', 'DEVPULSE_VERIFY_IOS')]:
        tasks = []
        for module in selected:
            item = registry['modules'][module]
            available = item['tasks']
            tests = any(list((root / item['path'] / 'src' / source).rglob('*.kt'))
                        for source in ('commonTest', 'jvmTest'))
            if platform == 'jvm' and 'compileKotlinJvm' in available:
                names = (['lintJvm'] if 'lintJvm' in available else [])
                names += ['jvmTest'] if tests else ['compileKotlinJvm']
                if not tests:
                    skipped.append(module + ': no common/JVM behavioral tests; compilation only')
            elif platform == 'android':
                names = [t for t in ('lintAndroidMain', 'lintDebug', 'compileAndroidMain',
                                     'compileDebugKotlin', 'testAndroidHostTest') if t in available]
            elif platform == 'ios' and 'compileKotlinIosSimulatorArm64' in available:
                names = ['compileKotlinIosSimulatorArm64']
            else:
                names = []
            tasks.extend(module + ':' + t for t in names)
        if tasks:
            if flag and os.environ.get(flag) != '1':
                skipped.append(platform + ' checks disabled; enable ' + flag + '=1')
            else:
                result.append((platform, tasks))
    if selected:
        skipped.append('UI/device/runtime checks require a manual session; no automatic device access')
    return result, skipped


def main():
    parser = argparse.ArgumentParser(description='Offline scoped verification. Requires cached Gradle 9.3.1, Python 3.7+, Git and JDK. No bootstrap.')
    parser.add_argument('--all', action='store_true', help='Select all modules; heavy platform flags still apply')
    parser.add_argument('--base', help='Reviewed integration ref, resolved to its merge-base with HEAD')
    parser.add_argument('--plan', action='store_true', help='Print scope and commands without executing checks')
    parser.add_argument('--strict', action='store_true', help='Fail on omitted applicable checks, including manual runtime checks')
    parser.add_argument('--test', nargs=2, metavar=('MODULE', 'CLASS'), help='Run one JVM class only; explicitly narrower than change verification')
    args = parser.parse_args()
    registry = json.loads((ROOT / 'scripts/verification.json').read_text())
    check_registry(ROOT, registry)
    base, reason = resolve_base(ROOT, args.base)
    paths = changed_paths(ROOT, base)
    selected = select_modules(registry, paths, args.all or base is None)
    plan, skipped = stages(ROOT, registry, selected)
    if args.test:
        module, pattern = args.test
        if module not in registry['modules'] or 'jvmTest' not in registry['modules'][module]['tasks']:
            parser.error('Unknown module or unsupported JVM test task')
        if not re.fullmatch(r'[A-Za-z_][A-Za-z0-9_.*$]*', pattern):
            parser.error('Use a qualified class name or Gradle class wildcard')
        selected = [module]
        plan = [('targeted JVM test', [module + ':jvmTest', '--tests', pattern])]
        skipped = ['Consumer, other-suite and platform checks omitted by --test']
    print('Base: {} ({})'.format(base or 'ALL', reason))
    print('Changed paths: ' + str(len(paths)))
    print('Scope: ' + (', '.join(selected) or 'setup/docs only'))
    gradle = ['./gradlew'] + registry['gradle_options']
    for name, tasks in plan:
        print('PLAN {}: {}'.format(name, shlex.join(gradle + tasks) if hasattr(shlex, 'join') else ' '.join(shlex.quote(a) for a in gradle + tasks)))
    for item in skipped:
        print('SKIP: ' + item)
    print('UNSUPPORTED: ktlint, dead-code and architecture gates are not configured. Gradle --offline does not isolate test network I/O.')
    if args.plan:
        print('PLAN ONLY: no verification executed')
        return 0
    run_step(ROOT, 'runner regression fixtures', [sys.executable, '-B', 'scripts/test_verify.py'], 60)
    if plan:
        if not (ROOT / 'gradlew').is_file():
            raise RuntimeError('Missing Gradle wrapper')
        distribution = 'gradle-9.3.1-bin'
        gradle_home = Path(os.environ.get('GRADLE_USER_HOME', str(Path.home() / '.gradle')))
        if not list((gradle_home / 'wrapper/dists' / distribution).glob('*/gradle-9.3.1/bin/gradle')):
            raise RuntimeError('Missing cached Gradle 9.3.1; bootstrap separately using gradle/wrapper/gradle-wrapper.properties')
        catalog = run_step(ROOT, 'task drift discovery', gradle + ['tasks', '--all'], registry['timeout_seconds'])
        check_tasks(registry, selected, catalog)
        for name, tasks in plan:
            graph = run_step(ROOT, name + ' task graph', gradle + tasks + ['--dry-run'], registry['timeout_seconds'])
            check_graph(graph)
            run_step(ROOT, name, gradle + tasks, registry['timeout_seconds'])
    if skipped and args.strict:
        print('FAIL: strict verification incomplete; resolve the listed skips and perform manual runtime checks separately')
        return 1
    print('PASS: setup fixtures and selected executable checks. Omitted: {}. Full validation: {}.'.format(len(skipped), 'incomplete' if skipped else 'not claimed'))
    return 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except (ValueError, KeyError, OSError, RuntimeError, subprocess.SubprocessError) as error:
        print('FAIL: ' + str(error), file=sys.stderr)
        sys.exit(1)
