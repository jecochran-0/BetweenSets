package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
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
    private lateinit var userViewModel: UserViewModel
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_workout)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        exerciseAdapter = ExerciseAdapter()
        recyclerView.adapter = exerciseAdapter


        noteDB = NoteDatabase.getDatabase(this)
        userViewModel = (application as ViewModelExtend).userViewModel


        lifecycleScope.launch {
            userViewModel.userState.collect { state ->
                userId = state.id
                loadExercises()
            }
        }

    }

    private fun loadExercises() {
        lifecycleScope.launch(Dispatchers.IO) {

            val exercises = noteDB.userDao().getUsersWithNoteListsById(userId)

            withContext(Dispatchers.Main) {

                exerciseAdapter.submitList(exercises)
            }
        }
    }

    fun callWorkoutPage(v: View?) {
        val selectedExercises = exerciseAdapter.getSelectedExercises()

        val intent = Intent(this@NewWorkout, WorkoutSelectPage::class.java)

        intent.putParcelableArrayListExtra("SELECTED_EXERCISES", ArrayList(selectedExercises))
        startActivity(intent)
    }
}