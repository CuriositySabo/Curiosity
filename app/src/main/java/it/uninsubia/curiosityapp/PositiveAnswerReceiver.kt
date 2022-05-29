package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager

class PositiveAnswerReceiver : BroadcastReceiver() {
    private val tag = "Positive answer"

    // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
    override fun onReceive(context: Context?, intent: Intent?) {
        rescheduleNotification(context!!, intent!!)
    }

    private fun rescheduleNotification(context: Context, intent: Intent) {
        val notificationData = intent.getStringArrayListExtra("notificationData")!!
        Log.e(tag, notificationData.toString())


        Utility.writeKnownCuriositiesFile(context, notificationData, true)

//        val time = getJsonDataFromSettings(context).time
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val time = prefs.getString("frequency", "30")!!.toInt()
        Log.e(tag, time.toString())

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(200)


        Toast.makeText(context, "Notifica Settata", Toast.LENGTH_LONG).show()

        val actionIntent = Intent(context, PostNotificationReceiver::class.java)
        actionIntent.putExtra("notificationData", notificationData)

        val requestcode = notificationData.hashCode()

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestcode,
                actionIntent,
                PendingIntent.FLAG_MUTABLE
            )

        val momentTime = System.currentTimeMillis()

        //esegui il broadcast dopo i millisecondi passati
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento
        alarmManager.set(AlarmManager.RTC_WAKEUP, momentTime + time, pendingIntent)
        Toast.makeText(context, "Notifica Settata", Toast.LENGTH_LONG).show()

    }



}