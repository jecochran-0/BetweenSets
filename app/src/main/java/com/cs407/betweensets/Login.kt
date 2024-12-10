package com.cs407.betweensets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class Login(private val injectedUserViewModel: UserViewModel? = null)
    : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var userViewModel: UserViewModel

    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var noteDB: NoteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameEditText = findViewById(R.id.enterEmailAddress)
        passwordEditText = findViewById(R.id.enterPassword)
        userViewModel = if (injectedUserViewModel != null) {
            injectedUserViewModel
        } else {
            ViewModelProvider(this)[UserViewModel::class.java]
        }
        noteDB = NoteDatabase.getDatabase(this)

        // Initialize SharedPreferences
        userPasswdKV = getSharedPreferences(
            getString(R.string.userPasswdKV), Context.MODE_PRIVATE
        )
        startListeners()
    }

    private fun startListeners() {

        // Button to navigate to the Home Page
        val completeLoginButton: Button = findViewById(R.id.completeLoginButton)
        completeLoginButton.setOnClickListener {
            val intent = Intent(this, home_page_activity::class.java)
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            //startActivity(intent)
            if (username.isNotBlank() && password.isNotBlank()) {
                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        getUserPasswd(username, password)
                    }
                    if (success) {
                        val userId = withContext(Dispatchers.IO) {
                            val user = noteDB.userDao().getByName(username)
                            user.userId
                        }
                        userViewModel.setUser(UserState(userId, username, password))

                        // Navigate to the next Activity (e.g., NoteListActivity)
                        //navigateToNoteListActivity()
                        startActivity(intent)
                    } else {
                        //errorTextView.visibility = View.VISIBLE
                    }
                }
            } else {
                //errorTextView.visibility = View.VISIBLE
            }

        }


        val signupBack: ImageButton = findViewById(R.id.loginBack)
        signupBack.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }
    }
private suspend fun getUserPasswd(name: String, passwdPlain: String): Boolean {
    val passwd = hash(passwdPlain)
    if (userPasswdKV.contains(name)) {
        val passwdInKV = userPasswdKV.getString(name, null)
        if (passwd == passwdInKV){
            return true
        }else {
            return false
        }

    } else {
        return false
//        withContext(Dispatchers.IO) {
//            noteDB.userDao().insert(User(userName = name))
//        }
//        with(userPasswdKV.edit()) {
//            putString(name, passwd)
//            apply()
//        }
    }
    return true
}
    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}
