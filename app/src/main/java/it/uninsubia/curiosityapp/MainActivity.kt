package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var layout: ActivityMainBinding
    private lateinit var logoutBtn: Button

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

        createNotificationchanel() // creazione del canale di notifica

    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "Notifica partita", Toast.LENGTH_LONG).show()

        val intent = Intent(this, CustomBroadcastReceiver::class.java) //creazione intent con il broadcast
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, FLAG_IMMUTABLE)



        val momentTime = System.currentTimeMillis() // per salvare l'orario in quel dato momento

        var time = 3
        time *= 1000

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento
        alarmManager.set(AlarmManager.RTC_WAKEUP, momentTime + time, pendingIntent) //esegui il broadcast dopo i millisecondi passati

    }

    private fun createNotificationchanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CuriosityChannel"
            val descr = "Channel for Curiosity"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelid, name, importance)
            channel.description = descr

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}