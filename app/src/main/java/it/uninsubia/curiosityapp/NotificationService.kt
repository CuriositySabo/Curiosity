package it.uninsubia.curiosityapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class NotificationService : Service() {
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var TAG = "Timers"

    private val channelidForeground = "notifyCuriosityForeground"
    private val channelid = "notifyCuriosity"

    private lateinit var channel: NotificationChannel
    private lateinit var serviceChannel: NotificationChannel

    private lateinit var intent: Intent

    val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var database: DatabaseReference


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)

        this.intent = intent!!

        val notification = NotificationCompat.Builder(baseContext, channelidForeground)
            .setContentTitle("Curiosity")
            .setContentText("Curiosity is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setChannelId(channelidForeground)
            .build()
        startForeground(1, notification)


        val timetoNotification = intent!!.getIntExtra("timetoNotification", 60)
        startTimer(timetoNotification)

        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        database = Firebase.database.reference
        createNotificationChannel() // creazione dei canali di notifica per il foreground
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    private fun startTimer(timetoNotification: Int) {

        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer!!.schedule(timerTask, 5000, (timetoNotification * 1000).toLong())

    }

    private fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask() {
        var i = 0

        timerTask = object : TimerTask() {
            override fun run() { //what the timer does when is running
                handler.post {

                    retrieveCuriosity()
                    i += 1
                    notifcationSender(i.toString())
                }
            }
        }
    }

    private fun retrieveCuriosity() {
        val chosenFields = intent.getStringArrayListExtra("chosenFields")
        val rnd = (0 until chosenFields!!.size).random()
        val field = chosenFields[rnd]

        val curiositiesinField =
            database.child("curiosità").child(field!!).get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                val curiosities = it.children
                for(children in curiosities) {
                    Log.i(TAG, children.toString())
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        if (curiositiesinField != null) {

        }

    }


    private fun notifcationSender(testo: String) {
        val notification = NotificationCompat.Builder(
            baseContext,
            channelid
        ) // crea una notifica con le seguenti caratteristiche
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lo sapevi?")
            .setContentText(testo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setChannelId(channelid)
            .build()

        // il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(baseContext)
        notificationManager.notify(200, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceChannel = NotificationChannel(
                channelidForeground,
                "Stato Curiosity Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.description = "Channel for Curiosity service"
            serviceChannel.lightColor = Color.RED

            channel = NotificationChannel(
                channelid,
                "Curiosità",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Channel for Curiosity"
            channel.lightColor = Color.GREEN

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}