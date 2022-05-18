package it.uninsubia.curiosityapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CustomBroadcastReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"

    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = NotificationCompat.Builder(context!!, channelid)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText("You are listening queen_we_are_the_champions...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, notification)
        Log.d("TAG", "========================FUNZIONO========================")
    }


}