# AIZORA AI Camera V1.1 — Android RTSP viewer

This is a **source project**, not an APK. It has **not been compiled or device-tested** in this environment.

## Tested separately by owner
V380 stream was confirmed playing in VLC: `rtsp://192.168.0.102:554/live/ch00_1`.

## Included
- RTSP video using AndroidX Media3 ExoPlayer
- Connect / Stop, saved RTSP URL, fullscreen, snapshot to app-specific Pictures directory
- GitHub Actions debug APK build workflow

## Not yet included
- PTZ/ONVIF control, AI detection, recording, Facebook Live, public gallery export

## Build with GitHub Actions
1. Unzip locally.
2. Upload **contents** (including hidden `.github` folder) to an empty GitHub repository, keeping folders intact. GitHub web upload can be awkward for folders; use GitHub Desktop, git, or an Android Git client.
3. Open Actions → Build Android APK → Run workflow (or push to main).
4. Download the `AIZORA-AI-Camera-V1.1-debug` artifact after a successful run, unzip it, then install `app-debug.apk`.
5. Phone and camera must be on the same Wi-Fi. Camera IP is LAN-only and may change.

## Notes
The snapshot is stored in Android's app-specific Pictures folder, not automatically in Gallery. This debug build is for private testing, not Play Store release. RTSP credentials, if any, should not be committed to public GitHub repositories. The initial camera URL is included for convenience and should be edited if the IP changes.
