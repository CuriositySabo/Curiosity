package it.uninsubia.curiosityapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class iknewReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        Toast.makeText(context, "Notifica partita", Toast.LENGTH_LONG).show()
        // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
        val notification = NotificationCompat.Builder(
            context!!,
            "notifyCuriosity"
        ) // crea una notifica con le seguenti caratteristiche
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lo sapevi?")
            .setContentText("Testo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager =
            NotificationManagerCompat.from(context) // il notification manager permette di postare la notifica
        notificationManager.notify(200, notification)
    }
}