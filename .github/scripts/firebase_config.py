#!/usr/bin/env python3
"""Create inert Firebase configs for CI checks or restore real configs for distribution."""

import argparse
import base64
import binascii
import json
import os
from pathlib import Path


ENVIRONMENTS = {
    "staging": ("dev-pulse-b6b18-staging", "dev.shounakmulay.devpulse.staging"),
    "production": ("dev-pulse-b6b18", "dev.shounakmulay.devpulse"),
}
ROOT = Path(__file__).resolve().parents[2]


class FirebaseConfigError(ValueError):
    pass


def destination(root: Path, environment: str) -> Path:
    return root / "androidApp" / "src" / environment / "google-services.json"


def test_configuration(environment: str) -> bytes:
    project, package = ENVIRONMENTS[environment]
    config = {
        "project_info": {"project_number": "1234567890", "project_id": project},
        "client": [{
            "client_info": {
                "mobilesdk_app_id": "1:1234567890:android:0000000000000000",
                "android_client_info": {"package_name": package},
            },
            "api_key": [{"current_key": "ci-placeholder-not-a-real-api-key"}],
        }],
        "configuration_version": "1",
    }
    return (json.dumps(config, indent=2) + "\n").encode("utf-8")


def write_new(path: Path, content: bytes) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.exists():
        raise FirebaseConfigError(f"Refusing to overwrite existing Firebase configuration: {path}")
    path.write_bytes(content)
    path.chmod(0o600)


def generate_test_configs(root: Path) -> None:
    """Used ONLY by non-distribution CI checks, including untrusted fork PRs."""
    for environment in ENVIRONMENTS:
        write_new(destination(root, environment), test_configuration(environment))


def restore_config(root: Path, environment: str, encoded: str) -> None:
    """Fail closed: distribution jobs must provide their own genuine configuration."""
    if not encoded:
        raise FirebaseConfigError(f"Missing Firebase config secret for {environment}")
    try:
        raw = base64.b64decode(encoded.strip(), validate=True)
        config = json.loads(raw)
    except (binascii.Error, UnicodeDecodeError, json.JSONDecodeError) as exc:
        raise FirebaseConfigError("Invalid base64-encoded Firebase client configuration") from exc
    project, package = ENVIRONMENTS[environment]
    if not isinstance(config, dict) or config.get("project_info", {}).get("project_id") != project:
        raise FirebaseConfigError(f"Firebase project mismatch for {environment}")
    clients = config.get("client", [])
    if not isinstance(clients, list) or not any(
        c.get("client_info", {}).get("android_client_info", {}).get("package_name") == package
        and c.get("client_info", {}).get("mobilesdk_app_id")
        and any(k.get("current_key") for k in c.get("api_key", []))
        for c in clients
        if isinstance(c, dict)
    ):
        raise FirebaseConfigError(f"Firebase Android package or API key mismatch for {environment}")
    write_new(destination(root, environment), raw)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mode", choices=("test", "restore"))
    parser.add_argument("environment", nargs="?", choices=tuple(ENVIRONMENTS))
    args = parser.parse_args()
    try:
        if args.mode == "test":
            if args.environment:
                parser.error("Test configs are generated for both flavors")
            generate_test_configs(ROOT)
        else:
            if not args.environment:
                parser.error("Restore requires an environment")
            restore_config(ROOT, args.environment, os.getenv("FIREBASE_CONFIG_BASE64", ""))
    except FirebaseConfigError as exc:
        parser.exit(1, f"Firebase config setup failed: {exc}\n")


if __name__ == "__main__":
    main()
