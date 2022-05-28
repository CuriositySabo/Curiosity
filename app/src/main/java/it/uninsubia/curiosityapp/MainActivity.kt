package it.uninsubia.curiosityapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val tag = "MainActivity"

    private lateinit var layout: ActivityMainBinding
    private val channelid = "notifyCuriosity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        initializeFiles(SettingsData(10))
        auth = FirebaseAuth.getInstance()

        val intent = Intent(this, nav_drawer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        createNotificationchanel() // creazione del canale di notifica

        getNotificationStatus()

        registerSettingsListener(this)

    }

    // legge le sharedpreferences per capire se deve schedulare una notifica
    private fun getNotificationStatus() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        Log.e(tag, prefs.all.toString())
        if (prefs.getBoolean("notification", false)) {
            Utility.notificationLauncher(this)
        }
    }

    // per creare il canale di notifica
    private fun createNotificationchanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CuriosityChannel"
            val descr = "Mostra le curiosità nella barra delle notifiche!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelid, name, importance)
            channel.description = descr

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeFiles(settingsData: SettingsData) {
        val directory = File(this.filesDir, "tmp") // crea la directory tmp
        if (!directory.exists()) {
            directory.mkdirs()
        }

        Utility.writeSettingsFile(settingsData, this)

        Utility.writeKnownCuriositiesFile(this)

        val topicsList = Utility.initTopicList()
        Utility.writeTopicsFile(topicsList, this)
    }

    private fun registerSettingsListener(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key.equals("notification")) {
                    val booleano = sharedPreferences.getBoolean("notification", false)
                    Log.e("flags", booleano.toString())
                    if (booleano)
                        Utility.notificationLauncher(context)
                }

                if (key.equals("frequency")) {
                    val frequency = sharedPreferences.getString("frequency", "30")!!.toInt()
                    Utility.writeSettingsFile(SettingsData(frequency), context)
                }
            }
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

}