package it.uninsubia.curiosityapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
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
import it.uninsubia.curiosityapp.databinding.ActivityNavDrawerBinding
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
                R.id.nav_logout),
            drawerLayout)
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

    // inizializza i file da utilizzare se non esistono già
    private fun initializeFiles() {
        val directory = File(this.filesDir, "tmp") // crea la directory tmp
        if (!directory.exists()) {
            directory.mkdirs()
        }

        Utility.writeKnownCuriositiesFile(this)

        if (!File("$directory/topics.json").exists()) {
            val topicsList = Utility.initTopicList()
            Utility.writeTopicsFile(topicsList, this)
        }

    }

    // aggiungo i listener per le impostazioni
    private fun registerSettingsListener(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            // Implementation
            if (key.equals("notification")) {
                val flag = prefs.getBoolean("notification", false)
                Log.e("flags", flag.toString())
                if (flag)
                    Utility.notificationLauncher(context)
                else {
                    val notificationManager = NotificationManagerCompat.from(context)
                    notificationManager.cancel(200)
                }
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
    }
}