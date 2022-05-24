package it.uninsubia.curiosityapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var layout: ActivityMainBinding
    private lateinit var gifView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        auth = FirebaseAuth.getInstance()
        gifView = findViewById(R.id.gif)
        Glide.with(this).load(R.raw.rocket_planet).into(gifView)
        Thread.sleep(1500)

        val intent = Intent(this, nav_drawer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}