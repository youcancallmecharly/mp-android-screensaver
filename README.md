# Matrix Screensaver für Android

Ein benutzerdefinierter Android-Bildschirmschoner mit Matrix-ähnlichem Effekt, bei dem Zeichen kontinuierlich von oben nach unten über den Bildschirm fallen. Speziell mit BITCOIN-Thematik.

## Features

- 🎬 Matrix-ähnlicher Effekt mit fallenden Zeichen
- 🪙 BITCOIN-Buchstaben (B, I, T, C, O, N) in Orange (#FF6600), groß und fett
- 🎨 Normale Zeichen in dunklerem Orange (#b54b04) auf schwarzem Hintergrund
- ⚡ Flüssige Animation mit ~60 FPS
- 📐 Variable Fallgeschwindigkeit (oben 1.2x, unten 2.0x)
- 🔤 BITCOIN-Buchstaben erscheinen 9x häufiger in ihren zugewiesenen Spalten
- 📱 Optimiert für Google Pixel 6 (1080x2400), funktioniert auf allen Android 12+ Geräten
- 🎯 BITCOIN-Buchstaben-Verteilung:
  - B: Spalte 1, 9
  - I: Spalte 2, 6, 10, 14
  - T: Spalte 3, 11
  - C: Spalte 4, 12
  - O: Spalte 5, 13
  - N: Spalte 7, 15
  - Spalte 8: Keine BITCOIN-Buchstaben

## Anforderungen

- Android 12 (API 31) oder höher
- Google Pixel 6 oder kompatibles Gerät

## Installation

### Option 1: APK installieren (einfachste Methode)

1. Lade die neueste `app-release.apk` aus dem [Releases](../../releases) Bereich herunter
2. Auf deinem Android-Gerät:
   - Öffne die Einstellungen → Sicherheit
   - Aktiviere "Unbekannte Quellen" oder "Installation von Apps aus unbekannten Quellen erlauben"
   - Öffne die heruntergeladene APK-Datei
   - Folge den Installationsanweisungen

3. Bildschirmschoner aktivieren:
   - Einstellungen → Display → Bildschirmschoner
   - Wähle "Matrix Screensaver" aus

### Option 2: Aus dem Quellcode kompilieren

1. Klone dieses Repository:
   ```bash
   git clone https://github.com/youcancallmecharly/mp-android-screensaver.git
   cd mp-android-screensaver
   ```

2. Öffne das Projekt in Android Studio

3. Verbinde dein Android-Gerät per USB und aktiviere USB-Debugging

4. Klicke auf "Run" (oder drücke Shift+F10) um die App zu kompilieren und zu installieren

5. Alternativ: Erstelle eine APK:
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Die APK findest du unter `app/build/outputs/apk/release/app-release.apk`

## Verwendung

1. Öffne die App "Matrix Screensaver" auf deinem Gerät
2. Tippe auf "Bildschirmschoner starten" um den Bildschirmschoner sofort zu testen
3. Oder gehe zu: Einstellungen → Display → Bildschirmschoner → Matrix Screensaver

## Technische Details

- **Programmiersprache**: Kotlin
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 34
- **Architektur**: DreamService API mit Custom View Rendering
- **Zeichen-Pool**: Kleinbuchstaben (a-z außer b,i,t,c,o,n), Zahlen (0-9), Sonderzeichen
- **BITCOIN-Buchstaben**: Erscheinen 9x häufiger in ihren zugewiesenen Spalten
- **Farben**: 
  - BITCOIN-Buchstaben: #FF6600 (Orange)
  - Normale Zeichen: #b54b04 (dunkleres Orange)
  - Hintergrund: #000000 (Schwarz)
- **Geschwindigkeit**: Variable Fallgeschwindigkeit (1.2x oben, 2.0x unten)
- **Rendering**: Canvas-basiert mit Hardware-Beschleunigung

## Projektstruktur

```
app/
├── src/main/
│   ├── java/com/matrixscreensaver/
│   │   ├── MainActivity.kt          # Setup-Activity
│   │   └── MatrixDreamService.kt   # DreamService-Implementierung
│   ├── res/
│   │   ├── xml/
│   │   │   └── dream_preferences.xml
│   │   └── values/
│   │       └── strings.xml
│   └── AndroidManifest.xml
└── build.gradle
```

## Lizenz

Dieses Projekt steht unter der MIT-Lizenz. Siehe [LICENSE](LICENSE) für Details.

## Beitragen

Beiträge sind willkommen! Bitte erstelle einen Pull Request oder öffne ein Issue.

## Autor

Erstellt für Google Pixel 6 mit Android 12+

