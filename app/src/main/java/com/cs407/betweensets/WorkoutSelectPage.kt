package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WorkoutSelectPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_select_page)

        val backButton: ImageView = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener{
            val intent = Intent(this, home_page_activity::class.java)
            startActivity(intent)
        }

    }
}