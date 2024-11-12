package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the button in the layout
        val buttonToGame = findViewById<Button>(R.id.buttonToGame)

        // Set a click listener on the button
        buttonToGame.setOnClickListener {
            // Create an intent to navigate to GameActivity
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}
