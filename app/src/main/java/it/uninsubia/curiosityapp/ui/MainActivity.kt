package it.uninsubia.curiosityapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import it.uninsubia.curiosityapp.R
import it.uninsubia.curiosityapp.Utility
import it.uninsubia.curiosityapp.databinding.ActivityNavDrawerBinding
import it.uninsubia.curiosityapp.notification.PostNotificationReceiver
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavDrawerBinding

    private val channelid = "notifyCuriosity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavDrawer()

        initOperations()
    }

    // Setta il nav drawer creando quindi il menù laterale nella home page
    private fun setNavDrawer() {
        //action bar
        setSupportActionBar(binding.appBarNavDrawer.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_nav_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_topics,
                R.id.nav_statistics,
                R.id.nav_settings,
                R.id.nav_logout
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_nav_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initOperations() {
        initializeFiles()
        auth = FirebaseAuth.getInstance()
        createNotificationchanel() // creazione del canale di notifica
        //getNotificationStatus()
        registerSettingsListener(this)
    }

    // Legge le sharedpreferences per capire se deve schedulare una notifica
    private fun getNotificationStatus() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        Log.e(tag, prefs.all.toString())
        if (prefs.getBoolean("notification", false)) {
            Utility.notificationLauncher(this)
        }
    }

    // Per creare il canale di notifica
    private fun createNotificationchanel() {
        // Da dopo android 8.0 sono diventati standard i canali di notifica e quindi per poter
        // inviare una notifica la tua app ne deve possedere almeno un
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

    // inizializza i file da utilizzare se non esistono già
    private fun initializeFiles() {
        val directory = File(this.filesDir, "tmp") // crea la directory tmp

        // Controlla se esite la cartella tmp
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Scrive la clsse KnownCuriositiesData vuota sul file se non esiste già
        Utility.writeKnownCuriositiesFile(this)

        // Se topics.json non esiste lo scrive sul file
        if (!File("$directory/topics.json").exists()) {
            val topicsList = Utility.initTopicList()
            Utility.writeTopicsFile(topicsList, this)
        }

    }

    // Aggiungo i listener per le impostazioni
    private fun registerSettingsListener(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val component = ComponentName(context, PostNotificationReceiver::class.java)

        listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            // Toggle che serve per attivare/disattivare la reicezione di notifiche
            if (key.equals("notification")) {
                val flag = prefs.getBoolean("notification", false)
                Log.e("flags", flag.toString())
                if (flag) {
                    // Se lo attivi lancia la prima notifica
                    // Abilita il receiver
                    context.packageManager.setComponentEnabledSetting(
                        component,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                    Utility.notificationLauncher(context)
                } else {
                    // Se lo disattivi cancella la notifica corrente
                    val notificationManager = NotificationManagerCompat.from(context)
                    notificationManager.cancel(200)
                    // Disabilita receiver
                    context.packageManager.setComponentEnabledSetting(
                        component,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
    }
}