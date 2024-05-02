package com.example.tappinggame

import DotView
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var dotContainer: FrameLayout
    private lateinit var movesCount: TextView
    private lateinit var paused: TextView
    private lateinit var back: Button

    private var score = 0
    private var highScore = 0
    private var exHighScore = 0
    private var totalWrongMoves = 10
    private var wrongMovesCount = 0
    private var dotAppearanceInterval = 1000L // Initial interval in milliseconds
    private var difficultyIncreaseRate = 0.9999 // Controls how fast difficulty increases
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: SharedPreferences

    private val tappedDots = mutableListOf<DotView>()
    private val untappedDots = mutableListOf<DotView>()
    private var isGamePaused = false // Flag to track game state
    private var isCountDown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        highScore = sharedPreferences.getInt("HIGH_SCORE", 0)
        exHighScore = sharedPreferences.getInt("HIGH_SCORE", 0)

        scoreTextView = findViewById(R.id.scoreTextView)
        dotContainer = findViewById(R.id.dotContainer)
        movesCount = findViewById(R.id.movesCount)
        paused = findViewById(R.id.paused)

        back = findViewById<Button>(R.id.goback)

        back.setOnClickListener {
            pauseGame()
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Restart Game")
                setMessage("Are you sure you want to exit the game?")
                setPositiveButton("Yes") { _, _ ->
                    resetGame()
                    val intent = Intent(this@MainActivity, Home::class.java)
                    startActivity(intent)
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                    resumeGame()
                    startCountdown()
                }
            }
            alertDialogBuilder.create().show()
        }

        paused.visibility = View.GONE
        movesCount.text = totalWrongMoves.toString()


        dotContainer.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y
                checkTap(x, y)
            }
            false
        }

        val pauseButton = findViewById<Button>(R.id.pause)
        pauseButton.setOnClickListener {
            togglePauseResume()
        }


        val restartButton = findViewById<Button>(R.id.restart)
        restartButton.setOnClickListener {
            restartGame()
        }

        startCountdown()
    }

    private fun togglePauseResume() {
        if (!isGamePaused) {
            pauseGame()
            paused.visibility = View.VISIBLE
        } else {
            resumeGame()
            paused.visibility = View.GONE
            startCountdown()
        }
    }

    private fun startCountdown() {
        isCountDown = true
        val countdownDuration = 3000L
        val countdownTextView = findViewById<TextView>(R.id.countdownTextView)
        countdownTextView.text = "3"
        countdownTextView.visibility = View.VISIBLE

        val countdownHandler = Handler(Looper.getMainLooper())
        val countdownRunnable = object : Runnable {
            var countdownValue = 3
            override fun run() {
                countdownValue--
                if (countdownValue >= 1) {
                    countdownTextView.text = countdownValue.toString()
                    countdownHandler.postDelayed(this, 1000)
                } else {
                    countdownTextView.text = "Go!"
                    countdownHandler.postDelayed({
                        countdownTextView.visibility = View.GONE
                        startGeneratingDots()
                    }, 1000)
                }
            }
        }

        countdownHandler.postDelayed(countdownRunnable, countdownDuration)
    }

    private fun restartGame() {
        pauseGame()
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Restart Game")
            setMessage("Are you sure you want to restart the game?")
            setPositiveButton("Yes") { _, _ ->
                resetGame()
                resumeGame()
                startCountdown()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                resumeGame()
                startCountdown()
            }
        }
        alertDialogBuilder.create().show()
    }

    private fun resetGame() {
        score = 0
        updateScore()
        dotAppearanceInterval = 2000L
        wrongMovesCount = 0
        updateWrongMovesCount()
        dotContainer.removeAllViews()
        untappedDots.clear()
        tappedDots.clear()
        handler.removeCallbacksAndMessages(null)
    }

    private fun pauseGame() {
        isGamePaused = true
        dotContainer.removeAllViews()
        handler.removeCallbacksAndMessages(null)
        val pauseButton = findViewById<Button>(R.id.pause)
        pauseButton.text = getString(R.string.resume)
        pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_play_arrow_24, 0, 0, 0)
    }

    private fun resumeGame() {
        isGamePaused = false
        val pauseButton = findViewById<Button>(R.id.pause)
        pauseButton.text = getString(R.string.pause)
        pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_pause_24, 0, 0, 0)
    }

    private fun startGeneratingDots() {
        val runnable = object : Runnable {
            override fun run() {
                if (!checkGameOver()) {
                    generateDot()
                    dotAppearanceInterval = (dotAppearanceInterval * difficultyIncreaseRate).toLong()
                    handler.postDelayed(this, dotAppearanceInterval)
                } else {
                    handler.removeCallbacks(this)
                    if (score > exHighScore) {
                        val intent = Intent(this@MainActivity, NewHighScore::class.java)
                        intent.putExtra("NEW_HIGH_SCORE", score)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity, GameOver::class.java)
                        intent.putExtra("SCORE", score)
                        startActivity(intent)
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private fun generateDot() {
        if (!isGamePaused) {
            val dotSizeRange = 80..200
            val dotSize = dotSizeRange.random()

            val dot = DotView(this@MainActivity)
            dot.setSize(dotSize)

            val dotX = Random.nextInt(dotContainer.width - dotSize)
            val dotY = Random.nextInt(dotContainer.height - dotSize)

            dot.setPosition(dotX.toFloat(), dotY.toFloat())
            dot.setColor(Color.RED, 128)

            dotContainer.addView(dot)
            untappedDots.add(dot)

            handler.postDelayed({
                if (dot.isTapped()) {
                    dotContainer.removeView(dot)
                } else {
                    if (!isGamePaused) {
                        dot.setColor(Color.GRAY, 80)
                        dot.setDisabled(true)
                        wrongMovesCount++
                        updateScore()
                        updateWrongMovesCount()
                    }
                }
            }, dotAppearanceInterval)
        }
    }

    private fun checkTap(x: Float, y: Float) {
        for (dot in untappedDots) {
            if (!dot.isDisabled() && dot.isPointInside(x, y)) {
                handleTap(dot)
                break
            }
        }
    }

    private fun handleTap(dot: DotView) {
        dot.setTapped(true)
        dot.setColor(Color.GREEN, 50)
        score++
        updateScore()
        updateHighScore()
        untappedDots.remove(dot)
        tappedDots.add(dot)
    }


    private fun updateScore() {
        scoreTextView.text = "$score"
    }

    private fun checkGameOver(): Boolean {
        val untouchedDotCount = countUntappedDots()
        return untouchedDotCount >= totalWrongMoves
    }

    private fun countUntappedDots(): Int {
        return untappedDots.size
    }

    private fun updateHighScore() {
        if (score > highScore) {
            highScore = score
            val editor = sharedPreferences.edit()
            editor.putInt("HIGH_SCORE", highScore)
            editor.apply()
        }
    }

    private fun updateWrongMovesCount() {
        movesCount.text = (totalWrongMoves - wrongMovesCount).toString()
    }
}
