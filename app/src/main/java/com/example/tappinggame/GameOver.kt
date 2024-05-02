package com.example.tappinggame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GameOver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val scoreTextView = findViewById<TextView>(R.id.yourScore)
        val highScoreTextView = findViewById<TextView>(R.id.highScore)

        val homeBtn = findViewById<Button>(R.id.homebtn)
        val restartBtn = findViewById<Button>(R.id.restartbtn)

        homeBtn.setOnClickListener {
            val intent = Intent(this@GameOver, Home::class.java)
            startActivity(intent)
        }

        restartBtn.setOnClickListener {
            val intent = Intent(this@GameOver, MainActivity::class.java)
            startActivity(intent)
        }

        // Get the score passed from MainActivity
        val score = intent.getIntExtra("SCORE", 0)

        scoreTextView.text = "Your Score: $score"

        // Retrieve high score from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("HIGH_SCORE", 0)
        highScoreTextView.text = "Highest Score: $highScore"
    }
}
