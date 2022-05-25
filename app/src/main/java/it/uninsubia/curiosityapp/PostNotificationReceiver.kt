package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter


class PostNotificationReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = "Lo sapevi"
        val text = "Testo"
        val topic = "Storia"
        //recupero del tempo per schedulare la notifica putextra e getextra non funzionano bene!
        // creazione del broadcast per la risposta
        var actionIntent = Intent(context, PositiveAnswerReceiver::class.java)
        val pIntentPositive = PendingIntent.getBroadcast(
            context, 1, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, NegativeAnswerReceiver::class.java)
        val pIntentNegative = PendingIntent.getBroadcast(
            context, 2, actionIntent,
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

        val notificationData = NotificationData(title, text, topic)
        writeNotificationToTmp(context, notificationData)

        // il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager
        notificationManager.notify(200, notification)
    }

    private fun writeNotificationToTmp(context: Context, notificationData: NotificationData) {
        var jsonString = ""
        val directory = File("${context.filesDir}/tmp")
        val filepath = File("$directory/lastnotification.json")

        try {
            PrintWriter(FileWriter(filepath)).use {
                val gson = Gson()
                jsonString = gson.toJson(notificationData)
                // scrive la classe in formato json sul file
                it.write(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.e("Answer", jsonString)

    }


}