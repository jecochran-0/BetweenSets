package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WorkoutSelectPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_select_page)

        val newWorkoutButton: Button = findViewById<Button>(R.id.newWorkoutButton)
        newWorkoutButton.setOnClickListener {
            val intent = Intent(this, NewWorkout::class.java)
            startActivity(intent)
        }

        val backButton: ImageButton = findViewById<ImageButton>(R.id.workoutSelectBack)
        backButton.setOnClickListener {
            val intent = Intent(this, home_page_activity::class.java)
            startActivity(intent)
        }


    }
}