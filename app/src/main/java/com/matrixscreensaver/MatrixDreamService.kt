package com.matrixscreensaver

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.service.dreams.DreamService
import android.view.View
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlin.random.Random

/**
 * Matrix-ähnlicher Bildschirmschoner mit fallenden Zeichen und BITCOIN-Thematik
 * 
 * Zeichen fallen kontinuierlich von oben nach unten:
 * - Zeichen-Pool: Kleinbuchstaben (a-z außer b,i,t,c,o,n), Zahlen (0-9), Sonderzeichen
 * - BITCOIN-Buchstaben (B, I, T, C, O, N) erscheinen 9x häufiger in ihren zugewiesenen Spalten
 * - BITCOIN-Buchstaben: Großbuchstaben, fett, Farbe #FF6600 (Orange)
 * - Alle anderen Zeichen: klein, normal, Farbe #b54b04 (dunkleres Orange)
 * - Hintergrund: Schwarz (#000000)
 * - Variable Fallgeschwindigkeit: oben 1.2x, unten 2.0x
 * - BITCOIN-Verteilung: B(1,9), I(2,6,10,14), T(3,11), C(4,12), O(5,13), N(7,15)
 */
class MatrixDreamService : DreamService() {

    companion object {
        private const val TAG = "MatrixDreamService"
    }

    // Zeichen-Pool: nur Kleinbuchstaben (ohne b,i,t,c,o,n), Zahlen und Sonderzeichen
    // b,i,t,c,o,n werden separat in ihren zugewiesenen Spalten verwendet
    private val characterPool = "aefghjklmpqrsuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?/~`"

    // Zeichen, die in GROSSBUCHSTABEN und FETT dargestellt werden
    // BCITON (ursprünglich) + C für BITCOIN
    private val boldUppercaseChars = setOf('b', 'c', 'i', 't', 'o', 'n')
    

    // Farbschema
    private val backgroundColor = Color.BLACK
    private val bitcoinColor = Color.parseColor("#FF6600") // Orange für BITCOIN-Buchstaben
    private val normalTextColor = Color.parseColor("#b54b04") // Dunkleres Orange für normale Zeichen

    // Rendering-Komponenten
    private var matrixView: MatrixView? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRendering = false
    private var renderRunnable: Runnable? = null

    // Zeichen-Spalten
    private data class CharacterColumn(
        var x: Float,
        var y: Float,
        var speed: Float,
        var characters: MutableList<Char>,
        var allowedBitcoinChar: Char? = null  // Erlaubter BITCOIN-Buchstabe für diese Spalte (null = keine BITCOIN-Buchstaben)
    )
    

    private val columns = mutableListOf<CharacterColumn>()
    private var columnWidth = 0f

    // Paint-Objekte für verschiedene Zeichen-Typen
    private lateinit var normalPaint: Paint
    private lateinit var boldPaint: Paint

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate called")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow called")
        
        // Vollbild-Modus
        isFullscreen = true
        isInteractive = false
        isScreenBright = true

        // Paint-Objekte initialisieren
        normalPaint = Paint().apply {
            color = normalTextColor
            textSize = 60f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        boldPaint = Paint().apply {
            color = bitcoinColor
            textSize = 60f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // Custom View für Rendering
        matrixView = MatrixView(this)
        setContentView(matrixView)
        
        Log.d(TAG, "ContentView set")
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        Log.d(TAG, "onDreamingStarted called")
        
        // Sicherstellen, dass die View initialisiert wird
        matrixView?.let { view ->
            view.post {
                val w = view.width
                val h = view.height
                Log.d(TAG, "View size: $w x $h")
                if (w > 0 && h > 0 && columns.isEmpty()) {
                    initializeColumns(w, h)
                    Log.d(TAG, "Columns initialized: ${columns.size}")
                }
            }
        }
        
        startRendering()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        Log.d(TAG, "onDreamingStopped called")
        stopRendering()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "onDetachedFromWindow called")
        stopRendering()
    }

    /**
     * Initialisiert die Zeichen-Spalten basierend auf Bildschirmgröße
     */
    private fun initializeColumns(width: Int, height: Int) {
        columns.clear()
        
        if (width == 0 || height == 0) {
            Log.w(TAG, "Cannot initialize columns: width or height is 0")
            return
        }

        // Spaltenbreite mit Abstand zwischen Zeichen (Textgröße 60f + 10f Abstand)
        columnWidth = 70f
        
        // Anzahl der Spalten berechnen
        val numColumns = (width / columnWidth).toInt()
        Log.d(TAG, "Creating $numColumns columns for width $width")
        
        // Spalten erstellen
        // BITCOIN-Muster: 
        // Spalte 1,9 = B; 2,6,10,14 = I; 3,11 = T; 4,12 = C; 5,13 = O; 7,15 = N; 8 = keine BITCOIN-Buchstaben
        val bitcoinPattern = listOf('B', 'I', 'T', 'C', 'O', 'I', 'N', null, 'B', 'I', 'T', 'C', 'O', 'I', 'N')
        
        for (i in 0 until numColumns) {
            val x = i * columnWidth
            // Basis-Geschwindigkeit (wird später basierend auf Y-Position angepasst)
            val baseSpeed = Random.nextFloat() * 1.0f + 1.0f // Basis zwischen 1.0-2.0 Pixel pro Frame
            val startY = Random.nextFloat() * height // Zufällige Startposition
            
            // Bestimme erlaubten BITCOIN-Buchstaben für diese Spalte (wiederholt sich alle 15 Spalten)
            val patternIndex = i % bitcoinPattern.size
            val allowedBitcoinChar = bitcoinPattern[patternIndex]
            
            // Erweitertes Zeichen-Pool für diese Spalte (alle Zeichen + erlaubter BITCOIN-Buchstabe 9x)
            // 9x Vorkommen = 9x so häufig wie ursprünglich
            val columnCharacterPool = if (allowedBitcoinChar != null) {
                val bitcoinChar = allowedBitcoinChar.lowercaseChar()
                characterPool + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar
            } else {
                characterPool
            }
            
            // Initiale Zeichen für diese Spalte generieren (Mischung aus allen Zeichen)
            // Verhindere, dass BITCOIN-Buchstaben direkt aufeinander folgen
            val chars = mutableListOf<Char>()
            var currentY = startY
            var lastChar: Char? = null
            while (currentY < height + 200) {
                var nextChar: Char
                do {
                    nextChar = columnCharacterPool.random()
                    // Wenn der letzte Char ein BITCOIN-Buchstabe war, wähle einen anderen
                } while (allowedBitcoinChar != null && 
                         lastChar == allowedBitcoinChar.lowercaseChar() && 
                         nextChar == allowedBitcoinChar.lowercaseChar())
                chars.add(nextChar)
                lastChar = nextChar
                currentY += columnWidth
            }
            
            columns.add(CharacterColumn(x, startY, baseSpeed, chars, allowedBitcoinChar))
        }
    }

    /**
     * Startet den Rendering-Thread
     */
    private fun startRendering() {
        if (isRendering) {
            Log.w(TAG, "Rendering already started")
            return
        }
        
        Log.d(TAG, "Starting rendering")
        isRendering = true
        
        renderRunnable = object : Runnable {
            override fun run() {
                if (isRendering && matrixView != null) {
                    matrixView?.invalidate()
                    handler.postDelayed(this, 16) // ~60 FPS
                }
            }
        }
        handler.post(renderRunnable!!)
    }

    /**
     * Stoppt den Rendering-Thread
     */
    private fun stopRendering() {
        Log.d(TAG, "Stopping rendering")
        isRendering = false
        renderRunnable?.let { handler.removeCallbacks(it) }
        renderRunnable = null
    }

    /**
     * Custom View für Matrix-Rendering
     */
    private inner class MatrixView(context: android.content.Context) : View(context) {

        private var isInitialized = false

        init {
            setBackgroundColor(backgroundColor)
            isFocusable = true
            isFocusableInTouchMode = false
            Log.d(TAG, "MatrixView created")
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = MeasureSpec.getSize(heightMeasureSpec)
            setMeasuredDimension(width, height)
            Log.d(TAG, "onMeasure: $width x $height")
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            val w = right - left
            val h = bottom - top
            Log.d(TAG, "onLayout: $w x $h, changed=$changed")
            
            if (changed && w > 0 && h > 0 && !isInitialized) {
                initializeColumns(w, h)
                isInitialized = true
            }
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            Log.d(TAG, "onSizeChanged: $w x $h")
            
            if (w > 0 && h > 0 && !isInitialized) {
                initializeColumns(w, h)
                isInitialized = true
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            
            val width = width
            val height = height
            
            // Fallback: Initialisierung in onDraw
            if ((width > 0 && height > 0) && !isInitialized) {
                Log.d(TAG, "Initializing in onDraw: $width x $height")
                initializeColumns(width, height)
                isInitialized = true
            }
            
            // Immer Hintergrund zeichnen
            canvas.drawColor(backgroundColor)
            
            if (width == 0 || height == 0) {
                Log.w(TAG, "onDraw: width or height is 0")
                return
            }
            
            if (columns.isEmpty()) {
                // Test: Zeichne einen einfachen Text, um zu sehen, ob überhaupt gerendert wird
                val testPaint = Paint().apply {
                    color = bitcoinColor
                    textSize = 80f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                val text = "MATRIX"
                val x = width / 2f
                val y = height / 2f
                canvas.drawText(text, x, y, testPaint)
                Log.d(TAG, "Drawing test text: MATRIX at $x, $y")
                return
            }
            
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
            
            // Lokale Kopie für Smart Cast
            val allowedBitcoinChar = column.allowedBitcoinChar
            
            for (char in column.characters) {
                // Zeichen nur rendern, wenn es auf dem Bildschirm sichtbar ist
                if (currentY >= -columnWidth && currentY <= screenHeight + columnWidth) {
                    // Prüfe, ob es ein BITCOIN-Buchstabe ist
                    val isBitcoinChar = allowedBitcoinChar != null && char.lowercaseChar() == allowedBitcoinChar.lowercaseChar()
                    
                    if (isBitcoinChar) {
                        // BITCOIN-Buchstaben: Großbuchstaben und fett in #FF6600
                        canvas.drawText(
                            char.uppercaseChar().toString(),
                            column.x,
                            currentY,
                            boldPaint
                        )
                    } else if (boldUppercaseChars.contains(char.lowercaseChar())) {
                        // Andere BCITON-Buchstaben (sollten nicht vorkommen, aber falls doch): normal in #b54b04
                        canvas.drawText(
                            char.toString(),
                            column.x,
                            currentY,
                            normalPaint
                        )
                    } else {
                        // Alle anderen Zeichen klein und normal in #b54b04
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
            
            // Spalte nach unten bewegen mit variabler Geschwindigkeit
            // Oben schneller, unten schneller (kleinerer Unterschied)
            val normalizedY = (column.y / screenHeight).coerceIn(0f, 1f)
            // Geschwindigkeit: oben 1.2x, unten 2.0x der Basis-Geschwindigkeit
            val dynamicSpeed = column.speed * (1.2f + 0.8f * normalizedY)
            column.y += dynamicSpeed
            
            // Recycling: Wenn Spalte den unteren Rand verlässt, oben neu positionieren
            if (column.y > screenHeight) {
                column.y = -Random.nextInt(200).toFloat()
                
                // Neue Zeichen generieren (sofortiges Auffüllen)
                column.characters.clear()
                var y = column.y
                // Lokale Kopie für Smart Cast
                val allowedBitcoinCharForRecycle = column.allowedBitcoinChar
                // Erweitertes Zeichen-Pool für diese Spalte (BITCOIN-Buchstabe 9x für 9x Häufigkeit)
                val columnCharacterPool = if (allowedBitcoinCharForRecycle != null) {
                    val bitcoinChar = allowedBitcoinCharForRecycle.lowercaseChar()
                    characterPool + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar + bitcoinChar
                } else {
                    characterPool
                }
                // Verhindere, dass BITCOIN-Buchstaben direkt aufeinander folgen
                var lastChar: Char? = null
                while (y < screenHeight + 200) {
                    var nextChar: Char
                    do {
                        nextChar = columnCharacterPool.random()
                        // Wenn der letzte Char ein BITCOIN-Buchstabe war, wähle einen anderen
                    } while (allowedBitcoinCharForRecycle != null && 
                             lastChar == allowedBitcoinCharForRecycle.lowercaseChar() && 
                             nextChar == allowedBitcoinCharForRecycle.lowercaseChar())
                    column.characters.add(nextChar)
                    lastChar = nextChar
                    y += columnWidth
                }
            }
        }
    }
}
