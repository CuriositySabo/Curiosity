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


        var actionIntent = Intent(context, IknewReceiver::class.java)
        Log.e("SendNotification", time.toString())
        actionIntent.putExtra("time", time)

        val pIntentKnow = PendingIntent.getBroadcast(
            context, 1, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, IDKnowReceiver::class.java)
        actionIntent.putExtra("time", time)

        val pIntentDKnow = PendingIntent.getBroadcast(
            context, 2, actionIntent,
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
            .addAction(R.mipmap.ic_launcher, "Lo sapevo", pIntentKnow)
            .addAction(R.mipmap.ic_launcher, "Non lo sapevo", pIntentDKnow)
            .build()

        val notificationManager =
            NotificationManagerCompat.from(context) // il notification manager permette di postare la notifica

        notificationManager.notify(200, notification)
    }


}