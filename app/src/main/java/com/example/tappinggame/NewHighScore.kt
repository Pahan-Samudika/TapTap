package com.example.tappinggame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class NewHighScore : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_high_score)


        val congratsTextView = findViewById<TextView>(R.id.congrats)
        val newHighTextView = findViewById<TextView>(R.id.newHighScore)
        val highScoreTextView = findViewById<TextView>(R.id.highScore)

        val homeBtn = findViewById<Button>(R.id.homebtn)
        val restartBtn = findViewById<Button>(R.id.restartbtn)

        homeBtn.setOnClickListener {
            val intent = Intent(this@NewHighScore, Home::class.java)
            startActivity(intent)
        }

        restartBtn.setOnClickListener {
            val intent = Intent(this@NewHighScore, MainActivity::class.java)
            startActivity(intent)
        }

        val score = intent.getIntExtra("NEW_HIGH_SCORE", 0)
        newHighTextView.text = "New High Score : $score"

        // Retrieve high score from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("HIGH_SCORE", 0)
        highScoreTextView.text = "Highest Score : $highScore"
    }
}