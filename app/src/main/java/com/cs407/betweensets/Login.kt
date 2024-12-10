package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Button to navigate to the Home Page
        val completeLoginButton: Button = findViewById(R.id.completeLoginButton)
        completeLoginButton.setOnClickListener {
            val intent = Intent(this, home_page_activity::class.java)
            startActivity(intent)
        }

        val signupBack: ImageButton = findViewById(R.id.loginBack)
        signupBack.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }
    }
}