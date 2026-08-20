#!/usr/bin/env bash
# ==============================================================================
# VisionCut AI - Automatic Keystore Base64 Encoder for macOS / Linux
# ==============================================================================

set -euo pipefail

KEYSTORE_NAME="visioncutai_release.jks"
CLEAN_FILE="keystore_github_secret.txt"

echo "=============================================================================="
echo "      VisionCut AI - Automatic Keystore Base64 Encoder for GitHub CI"
echo "=============================================================================="
echo ""

if [ ! -f "$KEYSTORE_NAME" ]; then
    echo "❌ [ERROR] Keystore file '$KEYSTORE_NAME' not found in $(pwd)!"
    exit 1
fi

echo "[1/3] Found keystore: $KEYSTORE_NAME"
echo "[2/3] Encoding to clean single-line Base64..."

if [[ "$OSTYPE" == "darwin"* ]]; then
    base64 -i "$KEYSTORE_NAME" | tr -d '\r\n ' > "$CLEAN_FILE"
else
    base64 -w 0 "$KEYSTORE_NAME" | tr -d '\r\n ' > "$CLEAN_FILE"
fi

echo "[3/3] Copying to system clipboard..."

if command -v pbcopy &> /dev/null; then
    pbcopy < "$CLEAN_FILE"
    echo "📋 Base64 string automatically copied to macOS clipboard!"
elif command -v xclip &> /dev/null; then
    xclip -selection clipboard < "$CLEAN_FILE"
    echo "📋 Base64 string automatically copied to Linux clipboard!"
fi

echo ""
echo "=============================================================================="
echo "       SUCCESS: COPY THIS TO GITHUB 'KEYSTORE_FILE' REPOSITORY SECRET!"
echo "=============================================================================="
echo ""
echo "File saved to: $(pwd)/$CLEAN_FILE"
echo ""
echo "INSTRUCTIONS:"
echo "1. Go to: GitHub Repository -> Settings -> Secrets and variables -> Actions"
echo "2. Name:   KEYSTORE_FILE"
echo "3. Secret: Paste (Ctrl+V or Cmd+V from clipboard)"
echo "4. Click 'Add secret'"
echo "=============================================================================="
