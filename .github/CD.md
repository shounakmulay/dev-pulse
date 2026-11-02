# Set up Android internal testing

This workflow builds the production app `dev.shounakmulay.devpulse` and publishes its AAB to Google Play's internal track. A push to `release` publishes automatically. A manual run must use `main` and can disable publishing.

## Configure GitHub environments

1. Open the repository's **Settings > Environments**.
2. Use the existing `release-signing` environment for signing and Play publishing. There is no need to recreate `release`.
3. Restrict this environment to the `main` and `release` branches. If you use **Protected branches only**, protect both branches first.
4. Require pull requests and passing quality checks on both branches. Add an environment reviewer if you want an approval before signing or publishing.
5. Add the following values under this environment. Do not add them under repository or organization secrets.

GitHub applies an environment's protection rules before making its secrets available. See [GitHub environment setup](https://docs.github.com/en/actions/how-tos/deploy/configure-and-manage-deployments/manage-environments).

### Add upload signing values to `release-signing`

| Kind | Name | Value |
|---|---|---|
| Secret | `ANDROID_UPLOAD_KEYSTORE_BASE64` | The existing PKCS#12 upload keystore encoded as one line of standard base64. |
| Secret | `ANDROID_UPLOAD_STORE_PASSWORD` | The keystore password. |
| Secret | `ANDROID_UPLOAD_KEY_ALIAS` | The private-key alias for the Play upload certificate. |
| Secret | `ANDROID_UPLOAD_KEY_PASSWORD` | The private-key password. |
| Variable | `ANDROID_UPLOAD_CERT_SHA256` | The SHA-256 fingerprint of the **upload key certificate** shown in Play Console's app integrity settings. Uppercase and colon separators are accepted. |
| Variable | `ANDROID_VERSION_CODE_BASE` | A nonnegative integer above every versionCode already uploaded to Play, including other tracks. |

The generated versionCode is `ANDROID_VERSION_CODE_BASE + GITHUB_RUN_NUMBER`. It must exceed the checked-in catalog version and stay at or below `2100000000`. A rerun keeps the same versionCode. Do not decrease the base after using this workflow.

The workflow restores the original bytes to `upload.p12` and explicitly selects `PKCS12` in keytool and Gradle. No conversion is needed.

Use the upload key, not Google's app-signing private key. The workflow checks the keystore certificate before building and the AAB signer afterward.

To load the keystore without printing it, run this command locally from the repository and replace the private file path:

```sh
base64 < /absolute/private/path/upload.p12 | tr -d '\n' | gh secret set ANDROID_UPLOAD_KEYSTORE_BASE64 --env release-signing
```

Set each password and alias through the GitHub UI or these interactive prompts. Do not put their values in command arguments:

```sh
gh secret set ANDROID_UPLOAD_STORE_PASSWORD --env release-signing
gh secret set ANDROID_UPLOAD_KEY_ALIAS --env release-signing
gh secret set ANDROID_UPLOAD_KEY_PASSWORD --env release-signing
```

### Add Play credentials to `release-signing`

Both jobs use `release-signing`, but the build step receives only signing values and the publishing step receives only Play authentication values. Separate environments can be introduced later if they need independent approval policies.

Use your existing Google service account if it already has the correct app access. No new account is required solely for this workflow.

1. Enable the Google Play Android Developer API in the service account's Google Cloud project.
2. In Play Console's **Users and permissions**, invite the service account email.
3. Limit its access to `dev.shounakmulay.devpulse`. Grant app visibility and permission to release to testing tracks. Production release, billing, and financial permissions are unnecessary.
4. Complete the app's Play Console setup and any required first manual upload. This workflow requires a completed internal-track release and fails when Play only accepts a draft.
5. Configure exactly one authentication option below.

The [Google Play API setup guide](https://developers.google.com/android-publisher/getting_started) describes the separate Cloud API and Play Console access steps.

For the existing JSON key option, add one environment secret:

| Kind | Name | Value |
|---|---|---|
| Secret | `PLAY_SERVICE_ACCOUNT_JSON` | The complete existing service-account key JSON, preferably minified. |

Load the file without displaying it:

```sh
gh secret set PLAY_SERVICE_ACCOUNT_JSON --env release-signing < /absolute/private/path/play-service-account.json
```

The workflow uses the existing JSON private key to exchange a signed JWT directly for an Android Publisher OAuth access token. It uses [Google's service-account credentials library](https://google-auth.readthedocs.io/en/latest/reference/google.oauth2.service_account.html), masks the token, and passes it to the independent Play readback steps. This JSON-key flow does not require `roles/iam.serviceAccountTokenCreator`. The auth action creates and cleans up the credential file; it does not impersonate the service account to mint this token.

For workload identity federation instead, remove `PLAY_SERVICE_ACCOUNT_JSON` and add both environment variables:

| Kind | Name | Value |
|---|---|---|
| Variable | `GCP_WORKLOAD_IDENTITY_PROVIDER` | The full `projects/NUMBER/locations/global/workloadIdentityPools/POOL/providers/PROVIDER` identifier. |
| Variable | `GCP_SERVICE_ACCOUNT` | The same Play-enabled service account email. |

Configure the provider to trust this repository's numeric repository and owner IDs, environment subject `repo:OWNER/REPO:environment:release-signing`, and the `main` or `release` ref. Grant that trusted principal `roles/iam.workloadIdentityUser` on the Play service account. Keep the GitHub environment branch restrictions. Follow [Google's federation setup](https://github.com/google-github-actions/auth#workload-identity-federation-through-a-service-account). Direct federation without a service account is not supported by this workflow.

The two authentication options are mutually exclusive. Authentication happens after artifact validation. The upload job has a 30-minute limit, and auth generates the readback token for that job. The auth action removes its generated credential file at job completion.

### Restore Firebase client configuration in distribution jobs

The `google-services.json` files are no longer tracked. Add the following **environment secrets** before merging this change. These are *client configuration files*, not service-account private keys.

| Environment | Secret | Source file |
|---|---|---|
| `release-signing` | `FIREBASE_ANDROID_PRODUCTION_CONFIG_B64` | `androidApp/src/production/google-services.json` |
| `staging-distribution` | `FIREBASE_ANDROID_STAGING_CONFIG_B64` | `androidApp/src/staging/google-services.json` |

From the repository root, after retaining local copies of the Firebase configuration:

```sh
base64 < androidApp/src/production/google-services.json | tr -d '\\n' | gh secret set FIREBASE_ANDROID_PRODUCTION_CONFIG_B64 --env release-signing
base64 < androidApp/src/staging/google-services.json | tr -d '\\n' | gh secret set FIREBASE_ANDROID_STAGING_CONFIG_B64 --env staging-distribution
```

The release workflows refuse to build without a valid matching Firebase configuration.
Non-distribution checks (including PRs from forks) generate inert temporary configurations
and do not require secrets. iOS currently has no CI build; its local
`iosApp/iosApp/GoogleService-Info.plist` must be downloaded from Firebase Console.
No custom GitHub PAT is required.

## Run the workflow

1. Merge the workflow into `main` so GitHub exposes **Android internal testing** in Actions.
2. Configure both environments and all required values first.
3. Run **Actions > Android internal testing > Run workflow**, choose `main`, and disable **publish** for the first build-only verification.
4. Inspect the quality, production test, build, and AAB validation results.
5. Run again with publishing enabled when you intend to distribute to testers. Alternatively, merge reviewed changes into `release` to publish automatically.
6. After publication, install through Play's internal-testing link and run your app smoke checks.

Equivalent manual commands are:

```sh
gh workflow run android-internal.yml --ref main -f publish=false
gh workflow run android-internal.yml --ref main -f publish=true
```

The workflow reuses `quality.yml` for the existing Kotlin quality gate. A separate production job executes `lintProductionRelease`, the database JVM tests, feed JVM tests, and network JVM tests. Those jobs run without release credentials.

Only after both gates pass does the signing job invoke the existing `scripts/build-android.sh production release aab`. It uses temporary signing files with mode `0600`, disables Gradle's configuration cache, and removes those signing files on success and failure. The shared setup action restores Gradle caches read-only in the signing job. Credential-free jobs can populate the cache.

The candidate contains one `app.aab` and immutable metadata for its source SHA, workflow run, version, package, AAB digest, and signer. The helper checks the JAR signature and pinned bundletool 1.18.3 checksum, validates the bundle, and checks its manifest package, version, and non-debuggable flag.

**The Actions artifact is retained for one day and is downloadable by anyone with repository read access. In a public repository this includes public readers.** It contains the signed AAB and non-secret candidate metadata. It never contains the keystore, signing properties, service-account JSON, or access tokens. The workflow's GitHub prerelease contains metadata and links, with no binary assets.

The publishing job downloads the candidate from the same run and checks its provenance and AAB digest. The Google Play upload action handles publication. A separate API readback checks the bundle SHA-256, versionCode, and completed internal release. Its temporary Play edit is deleted. Only then can the final job create the `internal/vVERSION-build.VERSION_CODE` annotated tag and prerelease with the exact source SHA.

## Recover a failed run

- If signing fails, check the upload certificate, alias, and passwords in `release-signing`. The Play app-signing certificate is a different certificate.
- If authentication fails, check API enablement, the service account's Play permissions, and the selected JSON or federation setup.
- If a candidate is already published with the same digest, rerun only the failed jobs. The publisher recognizes the existing completed internal release.
- If Play has the versionCode with another digest, increase the version base if needed and start a new run. The workflow never replaces a different bundle.
- If the bundle exists but the internal release is incomplete, resolve that release in Play Console or start a new run. The workflow does not label drafts as successful.
- If only GitHub recording failed, rerun the failed job. A matching prerelease is preserved. A different tag target or prerelease body causes a failure instead of an overwrite.
- If the one-day candidate artifact has expired, start a new workflow run. Do not rebuild an expired candidate under the old versionCode.

The workflow serializes all production internal releases and does not cancel an active run. GitHub's default concurrency behavior keeps one pending run, so newer requests replace older pending requests. It does not promise a release for every push. See [GitHub concurrency behavior](https://docs.github.com/en/actions/how-tos/write-workflows/choose-when-workflows-run/control-workflow-concurrency).

## Verify changes locally

Use Python 3.12 and install the pinned Play authentication dependency:

```sh
python3 -m pip install -r .github/scripts/requirements-play-auth.txt
python3 -B .github/scripts/test_android_cd.py
python3 -B .github/scripts/test_play_auth.py
actionlint .github/workflows/android-internal.yml .github/workflows/quality.yml
shellcheck .github/scripts/quality.sh scripts/build-android.sh
```

A first usable CD workflow does not provide release-device smoke tests, Play-installed upgrade verification, or runtime 16 KB native-library validation. The JVM tests cover existing migration behavior. The workflow does not claim Android installation or runtime verification. Keep those checks in the release checklist until their real device tests are implemented.
