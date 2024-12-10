package com.cs407.betweensets

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewExercise(private val injectedUserViewModel: UserViewModel? = null) : AppCompatActivity() {
    private lateinit var newExercise: EditText
    private lateinit var newSets: EditText
    private lateinit var newReps: EditText
    private lateinit var userViewModel: UserViewModel

    private var noteId: Int=0
    private var userId: Int=0
    private lateinit var noteDB: NoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_exercise)

        }
    fun callHome(v: View?) {
        startActivity(Intent(this@NewExercise, home_page_activity::class.java))
    }
    }
