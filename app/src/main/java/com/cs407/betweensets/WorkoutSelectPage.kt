package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutSelectPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_select_page)

        val buttonContainer: LinearLayout = findViewById(R.id.buttonContainer)

        val workoutDao = NoteDatabase.getDatabase(this).workoutDao()
        lifecycleScope.launch {
            val workouts = withContext(Dispatchers.IO) { workoutDao.getAllWorkouts() }

            for (workout in workouts) {
                val workoutButton = Button(this@WorkoutSelectPage)
                workoutButton.text = workout.workoutName

                // Pass workout details to WorkoutInProgressActivity
                workoutButton.setOnClickListener {
                    val intent = Intent(this@WorkoutSelectPage, WorkoutInProgressActivity::class.java)
                    intent.putExtra("WORKOUT_ID", workout.workoutId)
                    intent.putExtra("WORKOUT_NAME", workout.workoutName)
                    startActivity(intent)
                }

                buttonContainer.addView(workoutButton)
            }
        }

        val newWorkoutButton: Button = findViewById(R.id.newWorkoutButton)
        newWorkoutButton.setOnClickListener {
            val intent = Intent(this, NewWorkout::class.java)
            startActivity(intent)
        }

        val backButton: ImageButton = findViewById(R.id.workoutSelectBack)
        backButton.setOnClickListener { finish() }
    }
}
