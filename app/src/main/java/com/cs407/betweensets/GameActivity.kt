package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Button to navigate back to the WorkoutInProgress Page
        val workoutInProgressButton: Button = findViewById(R.id.button_workout_in_progress)
        workoutInProgressButton.setOnClickListener {
            val intent = Intent(this, WorkoutInProgressActivity::class.java)
            startActivity(intent)
        }
    }
}