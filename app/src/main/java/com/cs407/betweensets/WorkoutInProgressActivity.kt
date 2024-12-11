package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class WorkoutInProgressActivity : AppCompatActivity() {

    private var currentSet = 1
    private val scores = mutableMapOf<Int, Int>()

    private lateinit var btnSet1Finish: Button
    private lateinit var btnSet2Finish: Button
    private lateinit var btnSet3Finish: Button
    private lateinit var tvWorkoutTitle: TextView
    private lateinit var tvExerciseName: TextView

    private val gameActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val score = data?.getIntExtra("finalScore", 0) ?: 0
            val returnedSet = data?.getIntExtra("currentSet", currentSet) ?: currentSet

            scores[returnedSet] = score
            currentSet = returnedSet + 1
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_in_progress)

        // Get workout data from intent
        val workoutName = intent.getStringExtra("WORKOUT_NAME") ?: "Unknown Workout"

        // Initialize views
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        btnSet1Finish = findViewById(R.id.btnSet1Finish)
        btnSet2Finish = findViewById(R.id.btnSet2Finish)
        btnSet3Finish = findViewById(R.id.btnSet3Finish)

        // Set initial data
        tvWorkoutTitle.text = workoutName
        tvExerciseName.text = "Exercise 1"

        updateUI()

        // Set button listeners
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

        val btnNextExercise: Button = findViewById(R.id.btnNextExercise)
        btnNextExercise.setOnClickListener {
            tvExerciseName.text = "Next Exercise"
            currentSet = 1
            scores.clear()
            updateUI()
        }
    }

    private fun updateUI() {
        when (currentSet) {
            1 -> {
                btnSet1Finish.text = "Finish"
                btnSet1Finish.isEnabled = true
                btnSet2Finish.isEnabled = false
                btnSet3Finish.isEnabled = false
            }
            2 -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.text = "Finish"
                btnSet2Finish.isEnabled = true
                btnSet3Finish.isEnabled = false
            }
            3 -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet2Finish.text = "${scores[2]} pts"
                btnSet3Finish.text = "Finish"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.isEnabled = false
                btnSet3Finish.isEnabled = true
            }
            else -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet2Finish.text = "${scores[2]} pts"
                btnSet3Finish.text = "${scores[3]} pts"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.isEnabled = false
                btnSet3Finish.isEnabled = false
            }
        }
    }
}
