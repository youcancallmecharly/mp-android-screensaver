# Matrix Screensaver für Android

Ein benutzerdefinierter Android-Bildschirmschoner mit Matrix-ähnlichem Effekt, bei dem Zeichen kontinuierlich von oben nach unten über den Bildschirm fallen.

## Features

- 🎬 Matrix-ähnlicher Effekt mit fallenden Zeichen
- 🎨 Orange Zeichen (#FF6600) auf schwarzem Hintergrund
- ⚡ Flüssige Animation mit ~60 FPS
- 🔤 Spezielle Darstellung: BCITON werden groß und fett dargestellt, alle anderen Zeichen klein und normal
- 📱 Optimiert für Google Pixel 6 (1080x2400), funktioniert auf allen Android 12+ Geräten

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
   git clone https://github.com/dein-username/matrix-screensaver.git
   cd matrix-screensaver
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
- **Zeichen-Pool**: Kleinbuchstaben (a-z), Zahlen (0-9), Sonderzeichen
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

