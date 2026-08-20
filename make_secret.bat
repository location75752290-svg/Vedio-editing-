@echo off
setlocal EnableDelayedExpansion

title VisionCut AI - GitHub Keystore Secret Generator
color 0A

echo ==============================================================================
echo       VisionCut AI - Automatic Keystore Base64 Encoder for GitHub CI
echo ==============================================================================
echo.

set "KEYSTORE_NAME=visioncutai_release.jks"
set "RAW_FILE=temp_raw_base64.txt"
set "CLEAN_FILE=keystore_github_secret.txt"

:: Step 1: Check if the keystore file exists in the current folder
if not exist "%KEYSTORE_NAME%" (
    echo [ERROR] Keystore file '%KEYSTORE_NAME%' was not found in this directory!
    echo.
    echo Please make sure '%KEYSTORE_NAME%' is in the same folder as this script:
    echo %CD%
    echo.
    pause
    exit /b 1
)

echo [1/4] Found keystore: %KEYSTORE_NAME%
echo [2/4] Encoding with certutil...

:: Step 2: Encode to Base64 using Windows certutil
if exist "%RAW_FILE%" del /f /q "%RAW_FILE%" 2>nul
if exist "%CLEAN_FILE%" del /f /q "%CLEAN_FILE%" 2>nul

certutil -encode "%KEYSTORE_NAME%" "%RAW_FILE%" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] certutil encoding failed!
    pause
    exit /b 1
)

echo [3/4] Stripping PEM headers (-----BEGIN/END CERTIFICATE-----) and formatting...

:: Step 3: Remove headers and whitespace using PowerShell
powershell -NoProfile -Command "$lines = Get-Content '%RAW_FILE%' | Where-Object { $_ -notmatch '-----' -and $_.Trim() -ne '' }; $singleLine = ($lines -join '').Trim(); [System.IO.File]::WriteAllText('%CLEAN_FILE%', $singleLine)"

if not exist "%CLEAN_FILE%" (
    echo [ERROR] Failed to generate clean Base64 output!
    del /f /q "%RAW_FILE%" 2>nul
    pause
    exit /b 1
)

:: Clean up temporary raw file
del /f /q "%RAW_FILE%" 2>nul

echo [4/4] Copying clean Base64 string directly to your Windows Clipboard...

:: Step 4: Copy clean single-line Base64 to Windows Clipboard
type "%CLEAN_FILE%" | clip

echo.
echo ==============================================================================
echo        SUCCESS: COPY THIS TO GITHUB 'KEYSTORE_FILE' REPOSITORY SECRET!
echo ==============================================================================
echo.
echo [STATUS] Keystore Base64 has been automatically copied to your CLIPBOARD!
echo [STATUS] Clean text also saved to: %CD%\%CLEAN_FILE%
echo.
echo ------------------------------------------------------------------------------
echo BASE64 PREVIEW (First 200 characters):
powershell -NoProfile -Command "$txt = Get-Content '%CLEAN_FILE%'; if ($txt.Length -gt 200) { Write-Host ($txt.Substring(0, 200) + '... [TRUNCATED]') } else { Write-Host $txt }"
echo ------------------------------------------------------------------------------
echo.
echo INSTRUCTIONS:
echo 1. Open your GitHub Repository in your browser.
echo 2. Go to: Settings -> Secrets and variables -> Actions
echo 3. Click 'New repository secret'
echo 4. Name:   KEYSTORE_FILE
echo 5. Secret: Press Ctrl+V (Paste directly from clipboard)
echo 6. Click 'Add secret'
echo.
echo ==============================================================================
pause
