package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Utility {
    companion object {
         fun notificationLauncher(context : Context) {
            var time = 1
            time *= 2000 // in realtà ce ne mette di più

            SettingsData(time)

            Toast.makeText(context, "Notifica Settata", Toast.LENGTH_LONG).show()

            //creazione intent con il broadcast da inviare
            val intent = Intent(context, PostNotificationReceiver::class.java)


            val pendingIntent =
                PendingIntent.getBroadcast(context, context.hashCode(), intent, PendingIntent.FLAG_MUTABLE)

            val momentTime = System.currentTimeMillis() // per salvare l'orario in quel dato momento


            val alarmManager =
                context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento

            /*
             setto una sveglia, secondo il tempo prestabilito, che viene lanciate anche a se il
             device è in sleep. Quando la sveglia "suona" viene lanciato il pending intent ovvero il
             in broadcast
             */
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                momentTime + time,
                pendingIntent
            )
        }
    }
}