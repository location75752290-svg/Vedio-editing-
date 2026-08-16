# VisionCut AI — Professional AI Video Editor for Android

VisionCut AI is a production-ready mobile video editing application built natively for Android using Kotlin and Jetpack Compose. Inspired by desktop non-linear editing (NLE) tools like CapCut and Premiere Pro, VisionCut AI features a multi-layer timeline, hardware-accelerated MediaCodec rendering, Room local persistence, and Gemini AI integration.

---

## 🚀 Key Features

### 🎬 Non-Linear Timeline & Multi-Track Editing
- **Multi-Layer Tracks**: Video, PIP Overlays, Audio, Subtitles/Captions, and Visual Effects.
- **Ripple Editing**: Automatically shifts subsequent clips back when deleting or trimming.
- **Magnetic Timeline**: Snapping clips to track boundaries and clip edges.
- **Keyframe Animation**: Animate position (X, Y), scale, opacity, and rotation with custom curves (`Linear`, `Ease-In`, `Ease-Out`, `Ease-In-Out`).
- **Speed Ramping & Reverse**: Fast/Slow motion adjustments (0.1x to 10.0x) and 1-tap reverse playback.
- **Freeze Frame**: Instant freeze frame insertion at current playhead timestamp.

### 🎨 Visual FX, Filters & Compositing
- **Blend Modes**: Screen, Multiply, Overlay, Darken, Lighten, and Color Dodge.
- **Chroma Key**: Green screen background removal with customizable color picker.
- **Neural LUTs**: Cinematic color grading (Teal & Orange, Cyberpunk Neon, Vintage 35mm, Monochrome Noir).
- **Masking**: Geometric clip masking (Circle, Rectangle, Linear, Split).
- **Motion Blur & Stabilization**: Hardware-accelerated frame motion smoothing and shake reduction.

### 🎵 Audio Engine & Beat Detection
- **Interactive Audio Waveforms**: High-fidelity amplitude visualization.
- **Beat Detection**: Automated transient detection for rhythmic marker snapping.
- **Vocal Enhancement & Noise Reduction**: Algorithmic audio filtering.

### 🚀 MediaCodec Hardware Rendering & Direct Share
- **MediaCodec H.264/AVC Encoder**: Native GPU-accelerated video rendering.
- **Custom Presets**: 1080p, 4K UHD, 720p at 24/30/60 fps.
- **Multi-Platform Direct Share**: Native Android share intents for TikTok, YouTube Shorts, Instagram Reels, WhatsApp, and Facebook.
- **Export History**: Local Room database log of all generated exports.

### 🤖 Gemini AI Integration Layer
- **Text-to-Video Prompts**: Uses Gemini 3.5 Flash to expand user ideas into cinematographic prompts.
- **Auto-Captions**: Automatic script subtitle generation.
- **AI Voice Script Synthesizer**: Generates narrative scripts for voiceover recording.

---

## 🛠 Project Architecture & Tech Stack

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM + Clean Architecture + Unidirectional Data Flow (StateFlow)
- **Database**: Android Room DB (SQLite) for Project Drafts, Media Library, and Export Records
- **Networking & API**: OkHttp3 + Google Gemini 3.5 Flash REST API
- **Video Processing**: Android MediaCodec + MediaMuxer (Hardware Acceleration Pipeline)
- **Dependency Management**: Gradle Version Catalog (`libs.versions.toml`)

---

## 💻 Running in Android Studio

1. **Clone or Download the Repository** from GitHub / AI Studio.
2. **Open in Android Studio**:
   - Launch Android Studio (Ladybug or newer recommended).
   - Select **Open** and select the root directory of this project.
3. **Configure Secrets / Gemini API Key** (Optional for AI features):
   - Copy `.env.example` to `.env` in the root folder.
   - Set `GEMINI_API_KEY=your_actual_gemini_api_key`.
4. **Sync Gradle**:
   - Android Studio will automatically sync dependencies.
5. **Run the App**:
   - Select a physical Android device or Emulator (API 24+) and click **Run (Shift + F10)**.

---

## 📦 Generating Release APK / AAB for Google Play Store

### Option 1: Via AI Studio Settings
- Open the AI Studio project settings menu and click **Export APK / AAB**.

### Option 2: Via Command Line Gradle
```bash
# Build Release APK
./gradlew assembleRelease

# Build Google Play App Bundle (AAB)
./gradlew bundleRelease
```
The output files will be created in:
- **APK**: `app/build/outputs/apk/release/app-release-unsigned.apk`
- **AAB**: `app/build/outputs/bundle/release/app-release.aab`

---

## 📄 License & Attribution
Designed & Engineered with Google AI Studio & Jetpack Compose.
fix: regenerate official gradle wrapper v8.7
