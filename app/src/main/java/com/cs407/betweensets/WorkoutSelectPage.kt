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

        val buttonContainer: Spinner = findViewById(R.id.buttonContainer)

        // Check if there are selected exercises
        selectedExercises?.let {
            // Loop through each exercise and create a button for it
            for (exercise in it) {
                // Create a new button
                val button = Button(this)
                button.text = exercise.noteTitle // Set button text to exercise name

                // You can set a custom layout for the buttons
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                button.layoutParams = layoutParams

                // Handle button click (e.g., navigate to the workout detail page)
                button.setOnClickListener {
                    // Do something with the clicked exercise
                    // For example, log or navigate to another activity
                    Toast.makeText(this, "Selected: ${exercise.noteTitle}", Toast.LENGTH_SHORT)
                        .show()
                    // You can pass more data or go to another activity if needed
                }

                // Add the button to the container
                buttonContainer.addView(button)
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