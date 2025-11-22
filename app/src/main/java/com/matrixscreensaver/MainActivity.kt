package com.matrixscreensaver

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Main Activity für den Matrix Screensaver
 * 
 * Ermöglicht es dem Benutzer, den Bildschirmschoner zu aktivieren
 * oder direkt zu starten.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Einfache UI mit Buttons
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(64, 64, 64, 64)
            gravity = android.view.Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 32)
        }

        val description = TextView(this).apply {
            text = getString(R.string.screensaver_description)
            textSize = 16f
            setPadding(0, 0, 0, 48)
        }

        val startButton = Button(this).apply {
            text = getString(R.string.start_screensaver)
            setOnClickListener {
                startScreensaver()
            }
        }

        val settingsButton = Button(this).apply {
            text = "Bildschirmschoner-Einstellungen öffnen"
            setOnClickListener {
                openScreensaverSettings()
            }
        }

        layout.addView(title)
        layout.addView(description)
        layout.addView(startButton)
        layout.addView(settingsButton)

        setContentView(layout)
    }

    /**
     * Startet den Bildschirmschoner direkt
     */
    private fun startScreensaver() {
        val intent = Intent(Settings.ACTION_DREAM_SETTINGS)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback: Versuche DreamService direkt zu starten
            val dreamIntent = Intent(this, MatrixDreamService::class.java)
            startActivity(dreamIntent)
        }
    }

    /**
     * Öffnet die Android-Bildschirmschoner-Einstellungen
     */
    private fun openScreensaverSettings() {
        val intent = Intent(Settings.ACTION_DREAM_SETTINGS)
        startActivity(intent)
    }
}

