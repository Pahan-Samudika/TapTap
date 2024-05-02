package com.example.tappinggame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tapToPlay = findViewById<Button>(R.id.start)
        val highestScore = findViewById<TextView>(R.id.highestScore)

        // Retrieve high score from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("HIGH_SCORE", 0)
        highestScore.text = "Highest Score: $highScore"

        tapToPlay.setOnClickListener {
            val intent = Intent(this@Home, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
