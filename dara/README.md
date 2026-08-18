# Dara Helper Tool

Dara Helper Tool is a mobile utility featuring an AMOLED-dark UI, dynamic Termux shell terminal, custom obfuscated package sync (.ba / .lm dual format), and remote admin telemetry control.

## Project Structure
- `.github/workflows/build-apk.yml` - GitHub Actions workflow to auto-build APK on push.
- `admin_panel/` - Flask Admin Web Server and device telemetry control dashboard.
- `backend_tools/` - Python obfuscation & file splitter tool (.gz -> .ba + .lm).
- `android_app/` - Jetpack Compose + Termux Android App source code.

## How to Build APK via GitHub Actions
1. Push this entire repository to GitHub.
2. Go to the **Actions** tab in your repository.
3. The workflow **Build Dara Helper Tool APK** will automatically trigger.
4. Download the compiled `DaraHelperTool-Debug-APK` from the workflow run artifacts.
