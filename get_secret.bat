@echo off
setlocal EnableDelayedExpansion

title VisionCut AI - Flutter Keystore Secret Generator
color 0A

echo ==============================================================================
echo        VisionCut AI - Flutter Keystore Base64 Encoder (Windows)
echo ==============================================================================
echo.

:: ------------------------------------------------------------------------------
:: مرحلہ 1: چیک کرو کہ کیا keystore فائل اسی فولڈر میں موجود ہے یا نہیں
:: ------------------------------------------------------------------------------
if not exist "visioncutai_release.jks" (
    color 0C
    echo [ERROR] 'visioncutai_release.jks' فائل اس فولڈر میں نہیں ملی!
    echo موجودہ راستہ: %CD%
    echo.
    echo برائے مہربانی اس .bat فائل کو اسی فولڈر میں رکھیں جہاں keystore موجود ہے۔
    echo ==============================================================================
    echo.
    pause
    exit /b 1
)

echo [1/4] 'visioncutai_release.jks' مل گئی ہے...
echo [2/4] certutil کے ذریعے Base64 انکوڈنگ ہو رہی ہے...

:: پرانی عارضی فائلز ڈیلیٹ کریں اگر موجود ہوں
if exist "temp.b64" del /f /q "temp.b64" 2>nul
if exist "flutter_keystore_secret.txt" del /f /q "flutter_keystore_secret.txt" 2>nul

:: ------------------------------------------------------------------------------
:: مرحلہ 2: ونڈوز کے اصلی certutil ٹول سے انکوڈ کریں
:: ------------------------------------------------------------------------------
certutil -encode "visioncutai_release.jks" "temp.b64" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    color 0C
    echo [ERROR] certutil انکوڈنگ ناکام ہو گئی!
    pause
    exit /b 1
)

echo [3/4] اضافی ہیڈرز (BEGIN/END CERTIFICATE) اور خالی جگہیں ختم کی جا رہی ہیں...

:: ------------------------------------------------------------------------------
:: مرحلہ 3: پاور شیل کے ذریعے تمام ہیڈرز ہٹا کر ایک ہی لمبی لائن بنائیں
:: ------------------------------------------------------------------------------
powershell -NoProfile -Command ^
    "$raw = Get-Content -Path 'temp.b64' | Where-Object { $_ -notmatch '-----' -and $_.Trim() -ne '' };" ^
    "$oneLine = ($raw -join '').Trim();" ^
    "[System.IO.File]::WriteAllText('flutter_keystore_secret.txt', $oneLine);"

if not exist "flutter_keystore_secret.txt" (
    color 0C
    echo [ERROR] کلین فائل بنانے میں مسئلہ پیش آیا!
    del /f /q "temp.b64" 2>nul
    pause
    exit /b 1
)

:: عارضی فائل ڈیلیٹ کریں
del /f /q "temp.b64" 2>nul

echo [4/4] آپ کے ونڈوز کلپ بورڈ میں ڈیٹا کاپی کیا جا رہا ہے...

:: ------------------------------------------------------------------------------
:: مرحلہ 4: سیدھا ونڈوز کلپ بورڈ میں کاپی کریں
:: ------------------------------------------------------------------------------
type "flutter_keystore_secret.txt" | clip

:: ------------------------------------------------------------------------------
:: مرحلہ 5: سکرین پر کامیابی کا پیغام دکھائیں
:: ------------------------------------------------------------------------------
echo.
echo ==============================================================================
echo                   SUCCESS! مبارک ہو، کلپ بورڈ میں کاپی ہو گیا
echo ==============================================================================
echo.
echo آپ کا صاف سنگل لائن Base64 خودکار طریقے سے کاپی ہو چکا ہے!
echo.
echo اب سیدھا GitHub کھولیں اور پیسٹ (Ctrl+V) کریں:
echo   GitHub Repo -^> Settings -^> Secrets and variables -^> Actions -^> KEYSTORE_FILE
echo.
echo فائل کا بیک اپ یہاں محفوظ ہے: %CD%\flutter_keystore_secret.txt
echo ==============================================================================
echo.
pause
