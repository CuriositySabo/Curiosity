package it.uninsubia.curiosityapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var layout: ActivityMainBinding
    private lateinit var logoutBtn: Button
    private lateinit var testBtn: Button
    private lateinit var endtestBtn: Button

    private val channelid = "notifyCuriosity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        auth = FirebaseAuth.getInstance()

        logoutBtn = layout.logoutBtn
        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        testBtn = layout.testBtn
        testBtn.setOnClickListener {
            startService(Intent(this, NotificationService::class.java))
        }

        endtestBtn = layout.endtestBtn
        endtestBtn.setOnClickListener {
            stopService(Intent(this, NotificationService::class.java))
        }


        createNotificationchanel() // creazione del canale di notifica

    }

    private fun createNotificationchanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CuriosityChannel"
            val descr = "Channel for Curiosity"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelid, name, importance)
            channel.description = descr

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}