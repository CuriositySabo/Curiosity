package it.uninsubia.curiosityapp

import android.content.Intent
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

    private val numberofFields = 10

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

        val timetoNotification = 5
        // TODO: get timetoNotification from slider!
        val
        // TODO: get Curiosity field
        testBtn = layout.testBtn

        testBtn.setOnClickListener {
            val intent = Intent(this, NotificationService::class.java)
            intent.putExtra("timetoNotification", timetoNotification)

            val chosenFields = arrayListOf<String>()
            chosenFields.add("Cinema")
            chosenFields.add("Pizza")
            chosenFields.add("Storia")
            intent.putExtra("chosenFields", chosenFields)

            startService(intent)
        }

        endtestBtn = layout.endtestBtn
        endtestBtn.setOnClickListener {
            stopService(Intent(this, NotificationService::class.java))
        }


    }


}