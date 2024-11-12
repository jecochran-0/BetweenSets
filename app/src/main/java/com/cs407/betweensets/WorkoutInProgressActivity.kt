package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WorkoutInProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_in_progress)

        // Button to navigate to the Game Page
        val gameButton: Button = findViewById(R.id.button_game)
        gameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Button to navigate to the Workout Page (handled by a team member)
       // val workoutButton: Button = findViewById(R.id.button_workout)
        //workoutButton.setOnClickListener {
          //  val intent = Intent(this, WorkoutActivity::class.java)
            //startActivity(intent)
        //}
    }
}