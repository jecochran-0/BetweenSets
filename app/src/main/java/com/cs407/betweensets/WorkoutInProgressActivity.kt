package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutInProgressActivity : AppCompatActivity() {

    private var currentExerciseIndex = 0
    private val scores = mutableMapOf<Int, Int>()

    private lateinit var btnSet1Finish: Button
    private lateinit var btnSet2Finish: Button
    private lateinit var btnSet3Finish: Button
    private lateinit var tvWorkoutTitle: TextView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvSet1Weight: TextView
    private lateinit var tvSet2Weight: TextView
    private lateinit var tvSet3Weight: TextView
    private lateinit var tvSet1Reps: TextView
    private lateinit var tvSet2Reps: TextView
    private lateinit var tvSet3Reps: TextView
    private lateinit var btnBack: Button

    private lateinit var noteDatabase: NoteDatabase
    private var workoutExercises: List<Note> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_in_progress)

        // Initialize database
        noteDatabase = NoteDatabase.getDatabase(this)

        // Get workout data from intent
        val workoutId = intent.getIntExtra("WORKOUT_ID", -1)
        val workoutName = intent.getStringExtra("WORKOUT_NAME") ?: "Unknown Workout"

        if (workoutId == -1) {
            finish() // Exit if workoutId is invalid
            return
        }

        // Initialize views
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        btnSet1Finish = findViewById(R.id.btnSet1Finish)
        btnSet2Finish = findViewById(R.id.btnSet2Finish)
        btnSet3Finish = findViewById(R.id.btnSet3Finish)
        tvSet1Weight = findViewById(R.id.tvSet1Weight)
        tvSet2Weight = findViewById(R.id.tvSet2Weight)
        tvSet3Weight = findViewById(R.id.tvSet3Weight)
        tvSet1Reps = findViewById(R.id.tvSet1Reps)
        tvSet2Reps = findViewById(R.id.tvSet2Reps)
        tvSet3Reps = findViewById(R.id.tvSet3Reps)
        btnBack = findViewById(R.id.btnBack)

        // Set workout name
        tvWorkoutTitle.text = workoutName

        // Load workout exercises from the database
        loadWorkoutData(workoutId)

        // Back button functionality
        btnBack.setOnClickListener {
            val intent = Intent(this, WorkoutSelectPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        // Button listeners for set completion
        btnSet1Finish.setOnClickListener { completeSet(1) }
        btnSet2Finish.setOnClickListener { completeSet(2) }
        btnSet3Finish.setOnClickListener { completeSet(3) }

        // Next exercise button
        val btnNextExercise: Button = findViewById(R.id.btnNextExercise)
        btnNextExercise.setOnClickListener {
            moveToNextExercise()
        }
    }

    private fun loadWorkoutData(workoutId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val workout = noteDatabase.workoutDao().getWorkoutById(workoutId)
            val exerciseIds = workout.exercises.split(",").map { it.toInt() }
            workoutExercises = noteDatabase.workoutDao().getNotesByWorkout(exerciseIds)

            withContext(Dispatchers.Main) {
                if (workoutExercises.isNotEmpty()) {
                    updateExerciseUI(workoutExercises[currentExerciseIndex])
                }
            }
        }
    }

    private fun completeSet(setNumber: Int) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("currentSet", setNumber)
        startActivityForResult(intent, setNumber)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val score = data?.getIntExtra("finalScore", 0) ?: 0
            scores[requestCode] = score // Save the score for the completed set

            updateSetUI(requestCode) // Update the UI for the completed set
        }
    }

    private fun moveToNextExercise() {
        if (currentExerciseIndex < workoutExercises.size - 1) {
            currentExerciseIndex++
            scores.clear()
            updateExerciseUI(workoutExercises[currentExerciseIndex])
        } else {
            tvExerciseName.text = "Workout Complete!"
            disableAllButtons()
        }
    }

    private fun updateExerciseUI(exercise: Note) {
        tvExerciseName.text = exercise.noteTitle
        tvSet1Weight.text = "${exercise.noteSets * 10} lbs"
        tvSet2Weight.text = "${exercise.noteSets * 12} lbs"
        tvSet3Weight.text = "${exercise.noteSets * 15} lbs"

        tvSet1Reps.text = "${exercise.noteReps} reps"
        tvSet2Reps.text = "${exercise.noteReps - 2} reps"
        tvSet3Reps.text = "${exercise.noteReps - 4} reps"

        updateUI() // Reset button states
    }

    private fun updateSetUI(setNumber: Int) {
        when (setNumber) {
            1 -> {
                btnSet1Finish.text = "${scores[1]} pts"
                btnSet1Finish.isEnabled = false
                btnSet2Finish.isEnabled = true
            }
            2 -> {
                btnSet2Finish.text = "${scores[2]} pts"
                btnSet2Finish.isEnabled = false
                btnSet3Finish.isEnabled = true
            }
            3 -> {
                btnSet3Finish.text = "${scores[3]} pts"
                btnSet3Finish.isEnabled = false
            }
        }
    }

    private fun updateUI() {
        btnSet1Finish.text = "Finish"
        btnSet1Finish.isEnabled = true
        btnSet2Finish.text = "Finish"
        btnSet2Finish.isEnabled = false
        btnSet3Finish.text = "Finish"
        btnSet3Finish.isEnabled = false
    }

    private fun disableAllButtons() {
        btnSet1Finish.isEnabled = false
        btnSet2Finish.isEnabled = false
        btnSet3Finish.isEnabled = false
    }
}
