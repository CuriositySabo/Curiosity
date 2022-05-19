package it.uninsubia.curiosityapp

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class NotificationService : Service() {
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"
    var Your_X_SECS = 5

    private val channelid = "notifyCuriosity"

    val handler: Handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "=======================onStartCommand========================")
        super.onStartCommand(intent, flags, startId)
        val notification = NotificationCompat.Builder(this, channelid)
            .setContentTitle("Curiosity")
            .setContentText("Curiosity is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
        startTimer()

        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stoptimertask()
        super.onDestroy()
    }

    fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer!!.schedule(timerTask, 5000, (Your_X_SECS * 1000).toLong()) //
        //timer.schedule(timerTask, 5000,1000); //
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post {
                    Log.e(TAG, "============= HO FATTO QUALCOSA ================")
                    notifcationSender()

                }
            }
        }
    }

    fun notifcationSender() {
        val notification = NotificationCompat.Builder(baseContext, channelid) // crea una notifica con le seguenti caratteristiche
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lo sapevi?")
            .setContentText("Testo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(baseContext) // il notification manager permette di postare la notifica
        notificationManager.notify(200, notification)
    }

}