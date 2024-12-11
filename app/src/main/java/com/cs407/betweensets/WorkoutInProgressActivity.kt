package com.cs407.betweensets

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class WorkoutInProgressActivity : AppCompatActivity() {

    private var currentSet = 1 // Track the current set
    private val scores = mutableMapOf<Int, Int>() // Store scores for each set

    private lateinit var btnSet1Finish: Button
    private lateinit var btnSet2Finish: Button
    private lateinit var btnSet3Finish: Button

    // Register for activity result from GameActivity
    private val gameActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val score = data?.getIntExtra("finalScore", 0) ?: 0
            val returnedSet = data?.getIntExtra("currentSet", currentSet) ?: currentSet

            // Save the score
            scores[returnedSet] = score

            // Update currentSet to next set
            currentSet = returnedSet + 1

            // Update UI accordingly
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_in_progress)

        // Initialize views
        val tvWorkoutTitle: TextView = findViewById(R.id.tvWorkoutTitle)
        val tvExerciseName: TextView = findViewById(R.id.tvExerciseName)
        btnSet1Finish = findViewById(R.id.btnSet1Finish)
        btnSet2Finish = findViewById(R.id.btnSet2Finish)
        btnSet3Finish = findViewById(R.id.btnSet3Finish)

        // Set initial texts
        tvWorkoutTitle.text = "Push Day"
        tvExerciseName.text = "Bench Press"

        // Initialize buttons
        updateUI()

        // Set Finish button click listeners
        btnSet1Finish.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("currentSet", 1)
            gameActivityLauncher.launch(intent)
        }

        btnSet2Finish.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("currentSet", 2)
            gameActivityLauncher.launch(intent)
        }

        btnSet3Finish.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("currentSet", 3)
            gameActivityLauncher.launch(intent)
        }

        // Handle "Next Exercise" button
        val btnNextExercise: Button = findViewById(R.id.btnNextExercise)
        btnNextExercise.setOnClickListener {
            tvExerciseName.text = "Next Exercise Placeholder"
            // Reset for next exercise
            currentSet = 1
            scores.clear()
            updateUI()
        }
    }

    private fun updateUI() {
        // Update buttons based on currentSet and scores
        when (currentSet) {
            1 -> {
                btnSet1Finish.text = "Finish"
                btnSet1Finish.isEnabled = true
                btnSet2Finish.text = "Finish"
                btnSet2Finish.isEnabled = false
                btnSet3Finish.text = "Finish"
                btnSet3Finish.isEnabled = false
            }
            2 -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.text = "Finish"
                btnSet2Finish.isEnabled = true
                btnSet3Finish.text = "Finish"
                btnSet3Finish.isEnabled = false
            }
            3 -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.text = "${scores[2]} pts"
                btnSet2Finish.isEnabled = false
                btnSet3Finish.text = "Finish"
                btnSet3Finish.isEnabled = true
            }
            else -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.text = "${scores[2]} pts"
                btnSet2Finish.isEnabled = false
                btnSet3Finish.text = "${scores[3]} pts"
                btnSet3Finish.isEnabled = false
                // Optionally, you can enable the "Next Exercise" button here
            }
        }
    }
}
