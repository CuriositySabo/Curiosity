package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class NegativeAnswerReceiver : BroadcastReceiver() {
    // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("negativo","negativo")

        val time = getJsonDataFromTmp(context!!).time
        Log.e("Notification Answer", time.toString())

        Toast.makeText(context, "Notifica partita", Toast.LENGTH_LONG).show()
        val notificationManager =
            NotificationManagerCompat.from(context)
        notificationManager.cancel(200)

        //creazione intent con il broadcast
        val actionIntent = Intent(context, PostNotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_MUTABLE)

        val momentTime = System.currentTimeMillis()

        //esegui il broadcast dopo i millisecondi passati
        val alarmManager =
            context.getSystemService(ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento
        alarmManager.set(AlarmManager.RTC_WAKEUP, momentTime + time, pendingIntent)
    }

    private fun getJsonDataFromTmp(context: Context): SettingsData {
        val jsonString: String
        val directory = File("${context.filesDir}/tmp")
        val filepath = File("$directory/settings.json")

        try {
            jsonString = filepath.bufferedReader().use {
                it.readText()
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return SettingsData(0)
        }

        val gson = Gson()
        val settingsDatatype = object : TypeToken<SettingsData>(){}.type
        val settings : SettingsData = gson.fromJson(jsonString, settingsDatatype)
        Log.e("Answer", settings.toString())
        return settings
    }
}