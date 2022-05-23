package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SendNotificationBroadcast : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"

    override fun onReceive(context: Context?, intent: Intent?) {
        val time = intent!!.getIntExtra("time", 5000)



        val actionIntent = Intent(context, IknewReceiver::class.java)
        Log.e("SendNotification", time.toString())
        actionIntent.putExtra("time", time)

        val pendingIntent = PendingIntent.getBroadcast(
            context, 1, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(
            context!!,
            channelid
        ) // crea una notifica con le seguenti caratteristiche
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lo sapevi?")
            .setContentText("Testo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.mipmap.ic_launcher, "Lo sapevo", pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Non lo sapevo", null)
            .build()

        val notificationManager =
            NotificationManagerCompat.from(context) // il notification manager permette di postare la notifica

        notificationManager.notify(200, notification)
    }


}