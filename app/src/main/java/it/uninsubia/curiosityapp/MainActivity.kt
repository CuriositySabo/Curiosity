package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var layout: ActivityMainBinding
    private lateinit var logoutBtn: Button
    private lateinit var testBtn: Button

    private val channelid = "notifyCuriosity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        auth = FirebaseAuth.getInstance()

        createNotificationchanel() // creazione del canale di notifica

        logoutBtn = layout.logoutBtn
        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        testBtn = layout.testBtn
        testBtn.setOnClickListener {
        }

    }


    override fun onStart() {
        super.onStart()
        var time = 1
        time *= 5000 // in realtà ce ne mette 18

        val settingsData = SettingsData(time)


        val directory = File(this.filesDir, "tmp") // crea la directory tmp
        val filepath = File(directory, "settings.json") // crea il file json


        try {
            PrintWriter(FileWriter(filepath)).use {
                val gson = Gson()
                val jsonString = gson.toJson(settingsData)
                // scrive la classe in formato json sul file
                it.write(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        Toast.makeText(this, "Notifica Settata", Toast.LENGTH_LONG).show()

        //creazione intent con il broadcast da inviare
        val intent = Intent(this, NotificationSenderBroadcast::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val momentTime = System.currentTimeMillis() // per salvare l'orario in quel dato momento


        val alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento

        /*
         setto una sveglia, secondo il tempo prestabilito, che viene lanciate anche a se il
         device è in sleep. Quando la sveglia "suona" viene lanciato il pending intent ovvero il
         in broadcast
         */
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            momentTime + time,
            pendingIntent
        )
    }

    // per creare il canale di notifica
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

