package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class NewWorkout : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var noteDB: NoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_workout)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        exerciseAdapter = ExerciseAdapter()
        recyclerView.adapter = exerciseAdapter

        noteDB = NoteDatabase.getDatabase(this)

        lifecycleScope.launch {
            loadExercises()
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.addWorkoutButton).setOnClickListener {
            saveWorkout()
        }
    }

    private fun loadExercises() {
        lifecycleScope.launch(Dispatchers.IO) {
            val notes = noteDB.userDao().getAllNotes() // Fetch notes

            // Map Notes to NoteSummary
            val exercises = notes.map { note ->
                NoteSummary(
                    noteId = note.noteId,
                    noteTitle = note.noteTitle,
                    noteSets = note.noteSets,
                    noteReps = note.noteReps,
                    lastEdited = note.lastEdited
                )
            }

            // Update adapter on the main thread
            withContext(Dispatchers.Main) {
                exerciseAdapter.submitList(exercises)
            }
        }
    }


    private fun saveWorkout() {
        val selectedExercises = exerciseAdapter.getSelectedExercises()
        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "No exercises selected", Toast.LENGTH_SHORT).show()
            return
        }

        val workoutName = findViewById<EditText>(R.id.enterNewWorkout).text.toString().trim()
        if (workoutName.isEmpty()) {
            Toast.makeText(this, "Workout name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val exerciseIds = selectedExercises.joinToString(",") { it.noteId.toString() }

        lifecycleScope.launch {
            val workout = Workout(workoutName = workoutName, exercises = exerciseIds)
            withContext(Dispatchers.IO) {
                noteDB.workoutDao().insertWorkout(workout)
            }
            Toast.makeText(this@NewWorkout, "Workout saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
