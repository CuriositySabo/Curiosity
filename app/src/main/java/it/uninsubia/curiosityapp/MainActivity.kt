package it.uninsubia.curiosityapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.uninsubia.curiosityapp.databinding.ActivityLoginBinding
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var layout : ActivityMainBinding
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        auth = FirebaseAuth.getInstance()

        logoutBtn = layout.logoutBtn
        logoutBtn.setOnClickListener() {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }

}