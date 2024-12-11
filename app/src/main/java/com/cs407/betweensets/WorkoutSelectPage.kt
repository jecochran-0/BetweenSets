package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WorkoutSelectPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_select_page)

        val selectedExercises: List<NoteSummary>? = intent.getParcelableArrayListExtra("SELECTED_EXERCISES")

        val buttonContainer: LinearLayout? = findViewById(R.id.buttonContainer)

        // Check if there are selected exercises

        selectedExercises?.let {
            for (exercise in it) {
                // Create a new button for each exercise
                val exerciseButton = Button(this)
                exerciseButton.text = exercise.noteTitle

                // Set a listener for the button
                exerciseButton.setOnClickListener {
                    val intent = Intent(this, WorkoutInProgressActivity::class.java)
                    intent.putExtra("EXERCISE_NAME", exercise.noteTitle)
                    startActivity(intent)
                }

                // Add the button to the container
                buttonContainer?.addView(exerciseButton)
            }
        }

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