package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_main)

        val intent = Intent (this,RegisterActivity::class.java )
        startActivity(intent)

    }

    override fun onStart() {
        super.onStart()

        /*if(auth.currentUser == null) {
            loginUser()
        }*/
    }

    private fun loginUser() {
        val intent = Intent (this,RegisterActivity::class.java )
        startActivity(intent)
    }
}