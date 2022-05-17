package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }

    override fun onStart() {
        super.onStart()

        /*if(auth.currentUser == null) {
            loginUser()
        }*/
    }

    private fun loginUser() {
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
    }
}