package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.content.SharedPreferences
import android.widget.TextView
import android.widget.Toast

class settings_page_activity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var activeUsernameTextView: TextView
    private lateinit var activePasswordTextView: TextView
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)


        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)


        activeUsernameTextView = findViewById(R.id.activeUsername2)
        activePasswordTextView = findViewById(R.id.activePassword)
        userViewModel = (application as ViewModelExtend).userViewModel


        setUserData()
    }

    private fun setUserData() {

        val username = userViewModel.userState.value.name
        val password = userViewModel.userState.value.passwd


            activeUsernameTextView.text = username
            activePasswordTextView.text = password
    }


    fun callHome(v: View?) {
        startActivity(Intent(this@settings_page_activity, home_page_activity::class.java))
    }


    fun callLogout(v: View?) {
        startActivity(Intent(this@settings_page_activity, Welcome::class.java))
    }
}