package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WorkoutInProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_in_progress)

        // Hardcoded variables
        val workoutName = "Push Day"
        val exerciseName = "Bench Press"

        // Set workout and exercise names
        val tvWorkoutTitle: TextView = findViewById(R.id.tvWorkoutTitle)
        val tvExerciseName: TextView = findViewById(R.id.tvExerciseName)
        tvWorkoutTitle.text = workoutName
        tvExerciseName.text = exerciseName

        // Handle "Finish" button
        val btnFinish: Button = findViewById(R.id.btnFinish)
        btnFinish.setOnClickListener {
            // Navigate to Game Page
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Handle "Next Exercise" button
        val btnNextExercise: Button = findViewById(R.id.btnNextExercise)
        btnNextExercise.setOnClickListener {
            // For now, just show a placeholder
            tvExerciseName.text = "Next Exercise Placeholder"
        }
    }
}
