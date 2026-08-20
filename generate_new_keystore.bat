@echo off
setlocal EnableDelayedExpansion

title VisionCut AI - Automated Release Keystore Creator
color 0A

echo ==============================================================================
echo       VisionCut AI - Automatic Keystore Generator & Base64 Secret Exporter
echo ==============================================================================
echo.

set "KEYSTORE_NAME=visioncutai_release.jks"
set "KEY_ALIAS=visioncutai_release"
set "KEY_PASS=VisionCut2026MasterKeyPass"
set "RAW_FILE=temp_raw.b64"
set "CLEAN_FILE=keystore_github_secret.txt"

echo [1/4] Generating new release keystore '%KEYSTORE_NAME%'...
if exist "%KEYSTORE_NAME%" del /f /q "%KEYSTORE_NAME%" 2>nul

keytool -genkeypair -v ^
    -keystore "%KEYSTORE_NAME%" ^
    -alias "%KEY_ALIAS%" ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity 10000 ^
    -storepass "%KEY_PASS%" ^
    -keypass "%KEY_PASS%" ^
    -dname "CN=VisionCut AI, OU=Development, O=VisionCut, L=Islamabad, ST=Punjab, C=PK" >nul 2>&1

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] keytool failed! Make sure Java JDK is installed and added to PATH.
    pause
    exit /b 1
)
echo [SUCCESS] '%KEYSTORE_NAME%' created successfully with clean password!

echo.
echo [2/4] Encoding keystore to Base64 using certutil...
if exist "%RAW_FILE%" del /f /q "%RAW_FILE%" 2>nul
if exist "%CLEAN_FILE%" del /f /q "%CLEAN_FILE%" 2>nul

certutil -encode "%KEYSTORE_NAME%" "%RAW_FILE%" >nul 2>&1

echo [3/4] Cleaning PEM headers and newlines...
powershell -NoProfile -Command "$lines = Get-Content '%RAW_FILE%' | Where-Object { $_ -notmatch '-----' -and $_.Trim() -ne '' }; $single = ($lines -join '').Trim(); [System.IO.File]::WriteAllText('%CLEAN_FILE%', $single)"
del /f /q "%RAW_FILE%" 2>nul

echo [4/4] Copying clean Base64 string to Windows Clipboard...
type "%CLEAN_FILE%" | clip

echo.
echo ==============================================================================
echo            ALL 5 GITHUB SECRETS (EXACT VALUES TO PASTE IN GITHUB)
echo ==============================================================================
echo.
echo 1. Secret Name:  KEYSTORE_FILE
echo    Secret Value: [ALREADY COPIED TO YOUR CLIPBOARD! Just press Ctrl+V]
echo.
echo 2. Secret Name:  KEYSTORE_PASSWORD
echo    Secret Value: VisionCut2026MasterKeyPass
echo.
echo 3. Secret Name:  KEY_ALIAS
echo    Secret Value: visioncutai_release
echo.
echo 4. Secret Name:  KEY_PASSWORD
echo    Secret Value: VisionCut2026MasterKeyPass
echo.
echo 5. Secret Name:  GOOGLE_API_KEY
echo    Secret Value: [Apni Google / Gemini API Key yahan paste karein]
echo.
echo ==============================================================================
echo All values saved to: %CD%\keystore_github_secret.txt
echo ==============================================================================
pause
