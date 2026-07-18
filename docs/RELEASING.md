# Releasing Notificity

This project ships to the Google Play Store automatically via GitHub Actions.

- **CI** (`.github/workflows/ci.yml`) runs on every push/PR to `main`: Spotless,
  lint, unit tests, and a debug build.
- **Release** (`.github/workflows/release.yml`) runs when you push a tag matching
  `v*` (e.g. `v1.3.0`). It builds a signed **AAB** and uploads it to the Play
  **internal testing** track.

Once you're happy with an internal build, promote it to closed/production from
the Play Console (or change `track:` in `release.yml`).

---

## One-time setup

You only need to do these steps once. After that, releasing is just tagging.

### 1. Create an upload keystore

If you don't already have your Play upload key, generate one:

```bash
keytool -genkey -v -keystore upload-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

Keep this file and its passwords safe and **never commit it** (it's git-ignored).
If you use Play App Signing (recommended, and the default for new apps), this is
your *upload* key — Google holds the final app-signing key.

### 2. Do the first upload manually

Play requires the very first `.aab`/`.apk` for a package to be uploaded by hand
so the app listing exists. Build one locally and upload it once via the Play
Console:

```bash
./gradlew bundleRelease   # produces app/build/outputs/bundle/release/app-release.aab
```

(For a local signed build, create a `keystore.properties` file in the repo root —
see [Local signing](#local-signing) below.)

### 3. Create a Google Play service account

1. Enable the **Google Play Android Developer API**:
   https://console.cloud.google.com/apis/library/androidpublisher.googleapis.com
2. In Google Cloud Console → **IAM & Admin → Service Accounts**, create a new
   service account (no roles needed at the GCP level).
3. Open the account → **Keys → Add key → JSON**, and download the JSON file.
4. In the **Play Console → Users and permissions**, invite the service account
   email and grant it access to this app with at least **Release to testing
   tracks** (and production if you later publish that way).

### 4. Add the GitHub secrets

In the repo: **Settings → Secrets and variables → Actions → New repository secret**.

| Secret | What it is | How to produce it |
| --- | --- | --- |
| `GOOGLE_SERVICES_JSON` | Base64 of your real Firebase `app/google-services.json` | `base64 -w0 app/google-services.json` |
| `KEYSTORE_BASE64` | Base64 of your upload keystore | `base64 -w0 upload-keystore.jks` |
| `KEYSTORE_PASSWORD` | Keystore password | — |
| `KEY_ALIAS` | Key alias (e.g. `upload`) | — |
| `KEY_PASSWORD` | Key password | — |
| `PLAY_SERVICE_ACCOUNT_JSON` | The **contents** of the service-account JSON from step 3 | paste the file contents |

> On macOS, `base64` has no `-w0` flag — use `base64 -i file` (it outputs a
> single line already), or `base64 file | tr -d '\n'`.

The `release.yml` job uses a `production` GitHub Environment. Either create an
Environment named `production` (Settings → Environments) — optionally with a
required-reviewer gate for a manual approval step before each Play upload — or
remove the `environment: production` line from the workflow.

---

## Cutting a release

1. Bump `versionCode` (and, if you like, the default `versionName`) in
   `app/build.gradle.kts`. **`versionCode` must be higher than the last value
   uploaded to Play**, or the upload is rejected.
2. Commit and merge to `main`.
3. Tag and push:

   ```bash
   git tag v1.3.0
   git push origin v1.3.0
   ```

The `versionName` shown to users comes from the tag (`v1.3.0` → `1.3.0`), so keep
the tag and your intended version in sync. Watch the run under the **Actions**
tab; on success the build lands on the Play internal track.

---

## Local signing

To produce a signed build locally without exporting env vars, create a
`keystore.properties` file in the repo root (git-ignored):

```properties
storeFile=/absolute/path/to/upload-keystore.jks
storePassword=********
keyAlias=upload
keyPassword=********
```

Then `./gradlew bundleRelease` will pick it up automatically. Without it (and
without the CI env vars), release builds fall back to unsigned output so the
project still builds for contributors.
