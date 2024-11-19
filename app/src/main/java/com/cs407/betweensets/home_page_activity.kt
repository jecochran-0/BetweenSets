package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class home_page_activity : AppCompatActivity() {
    private lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_page)

        val newExcerciseButton = findViewById<ImageButton>(R.id.new_excercise_button)
        val workoutButton = findViewById<ImageButton>(R.id.workout_button)
        val settingsButton = findViewById<ImageButton>(R.id.setting_button)
        newExcerciseButton.setOnClickListener {
            startActivity(Intent(this@home_page_activity, settings_page_activity::class.java))
        }
        workoutButton.setOnClickListener{
            startActivity(Intent(this@home_page_activity, settings_page_activity::class.java))
        }
    }
    fun callSettings(v: View?) {
        startActivity(Intent(this@home_page_activity, settings_page_activity::class.java))
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_item -> {
                true
            }

            R.id.logout_item -> {
              // startActivity(Intent(this@home_page_activity, settings_page_activity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        }
}