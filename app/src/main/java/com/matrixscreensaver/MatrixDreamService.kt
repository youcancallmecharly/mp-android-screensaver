package com.matrixscreensaver

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.service.dreams.DreamService
import android.view.View
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

/**
 * Matrix-ähnlicher Bildschirmschoner mit fallenden Zeichen
 * 
 * Zeichen fallen kontinuierlich von oben nach unten:
 * - Zeichen-Pool: Kleinbuchstaben (a-z), Zahlen (0-9), Sonderzeichen
 * - BCITON werden in GROSSBUCHSTABEN und FETT dargestellt
 * - Alle anderen Zeichen werden klein und normal dargestellt
 * - Farbschema: Orange (#FF6600) auf schwarzem Hintergrund
 */
class MatrixDreamService : DreamService() {

    // Zeichen-Pool: nur Kleinbuchstaben, Zahlen und Sonderzeichen
    private val characterPool = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?/~`"

    // Zeichen, die in GROSSBUCHSTABEN und FETT dargestellt werden
    private val boldUppercaseChars = setOf('b', 'c', 'i', 't', 'o', 'n')

    // Farbschema
    private val backgroundColor = Color.BLACK
    private val textColor = Color.parseColor("#FF6600") // Orange

    // Rendering-Komponenten
    private lateinit var matrixView: MatrixView
    private val handler = Handler(Looper.getMainLooper())
    private var isRendering = false

    // Zeichen-Spalten
    private data class CharacterColumn(
        var x: Float,
        var y: Float,
        var speed: Float,
        var characters: MutableList<Char>
    )

    private val columns = mutableListOf<CharacterColumn>()
    private var columnWidth = 0f

    // Paint-Objekte für verschiedene Zeichen-Typen
    private lateinit var normalPaint: Paint
    private lateinit var boldPaint: Paint

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        
        // Vollbild-Modus
        isFullscreen = true
        isInteractive = false
        isScreenBright = true

        // Paint-Objekte initialisieren
        normalPaint = Paint().apply {
            color = textColor
            textSize = 40f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        boldPaint = Paint().apply {
            color = textColor
            textSize = 40f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // Custom View für Rendering
        matrixView = MatrixView(this)
        setContentView(matrixView)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        startRendering()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        stopRendering()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRendering()
    }

    /**
     * Initialisiert die Zeichen-Spalten basierend auf Bildschirmgröße
     */
    private fun initializeColumns(width: Int, height: Int) {
        columns.clear()
        
        if (width == 0 || height == 0) return

        // Spaltenbreite basierend auf Textgröße (etwa 30 Pixel)
        columnWidth = 30f
        
        // Anzahl der Spalten berechnen
        val numColumns = (width / columnWidth).toInt()
        
        // Spalten erstellen
        for (i in 0 until numColumns) {
            val x = i * columnWidth
            val speed = Random.nextFloat() * 4f + 1f // Geschwindigkeit zwischen 1-5 Pixel pro Frame
            val startY = Random.nextFloat() * height // Zufällige Startposition
            
            // Initiale Zeichen für diese Spalte generieren
            val chars = mutableListOf<Char>()
            var currentY = startY
            while (currentY < height + 200) {
                chars.add(characterPool.random())
                currentY += columnWidth
            }
            
            columns.add(CharacterColumn(x, startY, speed, chars))
        }
    }

    /**
     * Startet den Rendering-Thread
     */
    private fun startRendering() {
        if (isRendering) return
        
        isRendering = true
        val runnable = object : Runnable {
            override fun run() {
                if (isRendering) {
                    matrixView.invalidate()
                    handler.postDelayed(this, 16) // ~60 FPS
                }
            }
        }
        handler.post(runnable)
    }

    /**
     * Stoppt den Rendering-Thread
     */
    private fun stopRendering() {
        isRendering = false
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * Custom View für Matrix-Rendering
     */
    private inner class MatrixView(context: android.content.Context) : View(context) {

        init {
            setBackgroundColor(backgroundColor)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            if (w > 0 && h > 0) {
                initializeColumns(w, h)
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            
            val width = width
            val height = height
            
            if (width == 0 || height == 0 || columns.isEmpty()) {
                return
            }

            // Hintergrund zeichnen
            canvas.drawColor(backgroundColor)
            
            // Alle Spalten rendern
            for (column in columns) {
                renderColumn(canvas, column, width, height)
            }
        }

        /**
         * Rendert eine einzelne Spalte mit fallenden Zeichen
         */
        private fun renderColumn(canvas: Canvas, column: CharacterColumn, screenWidth: Int, screenHeight: Int) {
            var currentY = column.y
            
            for (char in column.characters) {
                // Zeichen nur rendern, wenn es auf dem Bildschirm sichtbar ist
                if (currentY >= -columnWidth && currentY <= screenHeight + columnWidth) {
                    if (boldUppercaseChars.contains(char.lowercaseChar())) {
                        // BCITON in Großbuchstaben und fett
                        canvas.drawText(
                            char.uppercaseChar().toString(),
                            column.x,
                            currentY,
                            boldPaint
                        )
                    } else {
                        // Alle anderen Zeichen klein und normal
                        canvas.drawText(
                            char.toString(),
                            column.x,
                            currentY,
                            normalPaint
                        )
                    }
                }
                
                currentY += columnWidth
            }
            
            // Spalte nach unten bewegen
            column.y += column.speed
            
            // Recycling: Wenn Spalte den unteren Rand verlässt, oben neu positionieren
            if (column.y > screenHeight) {
                column.y = -Random.nextInt(200).toFloat()
                
                // Neue zufällige Zeichen generieren
                column.characters.clear()
                var y = column.y
                while (y < screenHeight + 200) {
                    column.characters.add(characterPool.random())
                    y += columnWidth
                }
            }
        }
    }
}

