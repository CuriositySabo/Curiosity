package it.uninsubia.curiosityapp

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SendNotificationBroadcast : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"

    override fun onReceive(context: Context?, intent: Intent?) {
        val notification: Notification
        val actionintent = Intent(context, iknewReceiver::class.java)
        val actionIntent = PendingIntent.getBroadcast(
            context, 0, actionintent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notification = NotificationCompat.Builder(
            context!!,
            channelid
        ) // crea una notifica con le seguenti caratteristiche
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lo sapevi?")
            .setContentText("Testo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.mipmap.ic_launcher, "Lo sos", actionIntent)
            .build()

        val notificationManager =
            NotificationManagerCompat.from(context) // il notification manager permette di postare la notifica


        notificationManager.notify(200, notification)
    }


}