package com.cs407.betweensets

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class settings_page_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings_page)

    }
  //  override fun onCreateOptionsMenu(menu: Menu): Boolean {
  //      menuInflater.inflate(R.menu.settings_menu, menu)
  //      return true
  //  }
    fun callHome(v: View?) {
        startActivity(Intent(this@settings_page_activity, home_page_activity::class.java))
    }
    fun callLogout(v: View?) {
       // startActivity(Intent(this@settings_page_activity, Login::class.java))
    }
}