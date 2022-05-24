package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationAnswerSenderBroadcast : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"

    override fun onReceive(context: Context?, intent: Intent?) {
        //recupero del tempo per schedulare la notifica putextra e getextra non funzionano bene!
        // creazione del broadcast per la risposta
        var actionIntent = Intent(context, PositiveAnswerBroadcastReceiver::class.java)
        val pIntentPositive = PendingIntent.getBroadcast(
            context, 1, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, NegativeAnswerBroadcastReceiver::class.java)
        val pIntentNegative = PendingIntent.getBroadcast(
            context, 2, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        // creazione della notifica
        val notification = NotificationCompat.Builder(context!!, channelid)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lo sapevi?")
            .setContentText("Testo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //sui due bottoni mostrati dalla notifica viene assegnata un azione da eseguire con il
            //con il PendingIntent
            .addAction(R.mipmap.ic_launcher, "Lo sapevo", pIntentPositive)
            .addAction(R.mipmap.ic_launcher, "Non lo sapevo", pIntentNegative)
            .build()

        // il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, notification)
    }


}