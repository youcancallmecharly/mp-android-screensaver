# APK erstellen - Schritt-für-Schritt Anleitung

## Voraussetzungen
- Android Studio ist installiert
- Projekt ist geöffnet

## Debug-APK erstellen (zum Testen)

### Schritt 1: Projekt öffnen
1. Android Studio starten
2. **File → Open** (oder "Open an Existing Project")
3. Wähle das Projektverzeichnis: `/home/sk/Documents/Cursor/android Bildschirmschoner`
4. Klicke auf **OK**
5. Warte, bis Gradle synchronisiert (unten in der Statusleiste)

### Schritt 2: Debug-APK bauen
1. In der Menüleiste: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Warte auf "Build completed successfully" (unten rechts)
3. Klicke auf **"locate"** in der Benachrichtigung, um die APK zu finden
   - Oder öffne manuell: `app/build/outputs/apk/debug/app-debug.apk`

**Hinweis:** Debug-APK ist nur zum Testen, nicht für die Verteilung!

---

## Release-APK erstellen (für Freunde/GitHub)

### Schritt 1: Signierten Build starten
1. In der Menüleiste: **Build → Generate Signed Bundle / APK...**
2. Wähle **APK** → Klicke auf **Next**

### Schritt 2: Keystore erstellen (beim ersten Mal)
1. Klicke auf **Create new...**
2. **Key store path:** Wähle einen Speicherort und Namen (z.B. `android-release-key.jks`)
3. **Passwords:**
   - **Key store password:** Wähle ein sicheres Passwort (MERKEN!)
   - **Key password:** Wähle ein sicheres Passwort (MERKEN!)
4. **Key:**
   - **Alias:** z.B. "release"
   - **Validity:** z.B. 25 Jahre
   - **Certificate:** Fülle die Felder aus (Name, Organisation, etc.)
5. Klicke auf **OK**

**WICHTIG:** Bewahre die Passwörter sicher auf! Du brauchst sie für zukünftige Updates!

### Schritt 3: Keystore auswählen (wenn bereits vorhanden)
1. Klicke auf **Choose existing...**
2. Wähle deine `.jks` Datei
3. Gib die Passwörter ein

### Schritt 4: Build-Varianten auswählen
1. **Build variant:** Wähle **release**
2. **Signature Versions:** Aktiviere **V1 (Jar Signature)** und **V2 (Full APK Signature)**
3. Klicke auf **Finish**

### Schritt 5: APK finden
1. Warte auf "APK(s) generated successfully"
2. Klicke auf **"locate"** in der Benachrichtigung
   - Oder öffne manuell: `app/build/outputs/apk/release/app-release.apk`

---

## APK auf GitHub hochladen

1. Gehe zu deinem GitHub Repository: `https://github.com/youcancallmecharly/mp-android-screensaver`
2. Klicke auf **Releases** (rechts)
3. Klicke auf **Create a new release**
4. **Tag:** z.B. `v1.0.0`
5. **Release title:** z.B. "Version 1.0.0"
6. **Description:** Beschreibung hinzufügen (optional)
7. **Attach binaries:** Ziehe die `app-release.apk` hier hinein
8. Klicke auf **Publish release**

---

## APK auf dem Handy installieren

1. Kopiere die APK-Datei auf dein Android-Gerät (USB, E-Mail, Cloud, etc.)
2. Auf dem Gerät:
   - Öffne die Datei-App
   - Navigiere zur APK-Datei
   - Tippe darauf
   - Erlaube "Installation aus unbekannten Quellen" (falls gefragt)
   - Tippe auf **Installieren**

3. Bildschirmschoner aktivieren:
   - **Einstellungen → Display → Bildschirmschoner**
   - Wähle **Matrix Screensaver**

---

## Troubleshooting

### "Gradle sync failed"
- Warte, bis Android Studio alle Dependencies heruntergeladen hat
- Prüfe die Internetverbindung
- File → Invalidate Caches / Restart

### "SDK not found"
- File → Settings → Appearance & Behavior → System Settings → Android SDK
- Prüfe, ob Android SDK installiert ist

### "Build failed"
- Prüfe die Fehlermeldungen in der "Build" Ansicht unten
- Stelle sicher, dass alle Dependencies korrekt sind

