package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class PostNotificationReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"
    private val tag = "PostNotification"

    override fun onReceive(context: Context?, intent: Intent?) {

        val stringArray = intent!!.getStringArrayExtra("notificationData")!!
        val title = stringArray[0]
        val text = stringArray[1]
        val topic = stringArray[2]

        val requestcode = "$title $text".hashCode()
        Log.e(tag, " \nrequest code : $requestcode \ntitle : $title \ntext : $text \ntopic: $topic")

        //recupero del tempo per schedulare la notifica putextra e getextra non funzionano bene!
        // creazione del broadcast per la risposta
        var actionIntent = Intent(context, PositiveAnswerReceiver::class.java)

        actionIntent.putExtra("test", "per positivo")
        actionIntent.putExtra("notificationData", stringArray)

        val pIntentPositive = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, NegativeAnswerReceiver::class.java)
        actionIntent.putExtra("test", "per negativo")
        actionIntent.putExtra("notificationData", stringArray)

        val pIntentNegative = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        // creazione della notifica
        val notification = NotificationCompat.Builder(context!!, channelid)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
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