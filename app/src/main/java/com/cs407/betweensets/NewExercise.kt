package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class NewExercise: AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var setEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var saveButton: Button

    private var noteId: Int = 0
    private lateinit var noteDB: NoteDatabase
    private lateinit var userViewModel: UserViewModel
    private var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_exercise)
        saveButton = findViewById<Button>(R.id.add_exercise_button)
        setEditText = findViewById(R.id.newSets)
        repsEditText = findViewById(R.id.newReps)
        titleEditText = findViewById(R.id.newExercise)
        noteId = intent.getIntExtra("noteId", 0)
        noteDB = NoteDatabase.getDatabase(this)
        userViewModel = (application as ViewModelExtend).userViewModel
        lifecycleScope.launch {
            userViewModel.userState.collect { state ->
                userId = state.id
            }
        }
        if (noteId != 0) {
            //loadNote()
        }

        // Set save button listener
        saveButton.setOnClickListener {
            saveContent()
        }
    }
    //    private fun loadNote() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val note = noteDB.noteDao().getById(noteId)
//            withContext(Dispatchers.Main) {
//                titleEditText.setText(note.noteTitle)
//                setEditText.setText(note.noteSets)
//                repsEditText.setText(note.noteReps)
//            }
//        }
//    }
    private fun saveContent() {
        val title = titleEditText.text.toString()
        val content = setEditText.text.toString()
        val reps = repsEditText.text.toString()
        val intent = Intent(this, home_page_activity::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            val note = Note(
                noteId = if (noteId == 0) 0 else noteId,
                noteTitle = title,
                noteSets = content.toInt(), // Create a basic abstract
                noteReps = reps.toInt(),
                noteDetail = "test",
                notePath = null, // Adjust as needed
                lastEdited = Date()
            )

            noteDB.noteDao().upsertNote(note, userId) // causes error

            withContext(Dispatchers.Main) {
                // Finish activity or give user feedback
                //finish()
                startActivity(intent)
            }
        }
    }

    fun callHome(v: View?) {
        startActivity(Intent(this@NewExercise, home_page_activity::class.java))
    }
}
