package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class IDKnowReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent!!.extras
        val time = bundle!!.getInt("time")

        Log.e("IDknow", time.toString())


        Toast.makeText(context, "Notifica partita", Toast.LENGTH_LONG).show()
        val notificationManager =
            NotificationManagerCompat.from(context!!)
        notificationManager.cancel(200)

        //creazione intent con il broadcast
        val actionIntent = Intent(context, SendNotificationBroadcast::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE)

        val momentTime = System.currentTimeMillis()


        Log.e("IDknow", time.toString())

        //esegui il broadcast dopo i millisecondi passati
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento
        alarmManager.set(AlarmManager.RTC_WAKEUP, momentTime + time, pendingIntent)
    }
}