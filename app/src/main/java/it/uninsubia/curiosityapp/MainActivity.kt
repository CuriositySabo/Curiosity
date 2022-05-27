package it.uninsubia.curiosityapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
    private val channelid = "notifyCuriosity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        createFile(SettingsData(10))
        auth = FirebaseAuth.getInstance()

        val intent = Intent(this, nav_drawer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        createNotificationchanel() // creazione del canale di notifica
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
                val jsonString = gson.toJson(KnownCuriositiesData())
                it.write(jsonString)
            }
        }
    }

}