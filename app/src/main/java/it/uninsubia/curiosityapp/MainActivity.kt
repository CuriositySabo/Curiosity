package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var editTextTitle : EditText
    private lateinit var editTextText : EditText
    private lateinit var editTextTopic : EditText

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


        editTextTitle = layout.editTextTitle
        editTextText = layout.editTextText
        editTextTopic = layout.editTextTopic



        testBtn = layout.testBtn
        testBtn.setOnClickListener {
            val notificationData = NotificationData(editTextTitle.text.toString(), editTextText.text.toString(), editTextTopic.text.toString())
            doStuff(notificationData)
        }

    }

    private fun doStuff(notificationData: NotificationData) {
        var time = 1
        time *= 2000 // in realtà ce ne mette di più

        val requestcode = notificationData.hashCode()

        val settingsData = SettingsData(time)
        createFile(settingsData)

        Toast.makeText(this, "Notifica Settata", Toast.LENGTH_LONG).show()

        //creazione intent con il broadcast da inviare
        val intent = Intent(this, PostNotificationReceiver::class.java)
        val stringArray = arrayOf(notificationData.title, notificationData.text, notificationData.topic)
        intent.putExtra("notificationData", stringArray)
        val pendingIntent =
            PendingIntent.getBroadcast(this, requestcode, intent, PendingIntent.FLAG_MUTABLE)

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

    private fun createFile(settingsData: SettingsData) {
        val directory = File(this.filesDir, "tmp") // crea la directory tmp
        if (!directory.exists()){
            directory.mkdirs()
        }

        var filepath = File(directory, "settings.json")

        // crea il file setting se non esiste
        try {
            PrintWriter(FileWriter(filepath)).use {
                val gson = Gson()
                val jsonString = gson.toJson(settingsData)
                // scrive la classe contenente i settings in formato json sul file
                it.write(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        filepath = File(directory, "knowncuriosities.json")
        // creo il file knowncuriosity se non esiste
        if (!filepath.exists()) {
            PrintWriter(FileWriter(filepath)).use {
                val gson = Gson()
                // inizializzo con la classe vuota il file
                val jsonString = gson.toJson(KnownCuriosityData())
                it.write(jsonString)
            }
        }
    }
}

