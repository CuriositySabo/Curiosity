package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

class NegativeAnswerReceiver : BroadcastReceiver() {
    private val tag = "Negative answer"

    // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
    override fun onReceive(context: Context?, intent: Intent?) {
        rescheduleNotification(context!!, intent!!)

    }

    private fun getJsonDataFromSettings(context: Context): SettingsData {
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
        val settingsDatatype = object : TypeToken<SettingsData>() {}.type
        val settings: SettingsData = gson.fromJson(jsonString, settingsDatatype)
        Log.e(tag, settings.toString())
        return settings
    }




    private fun rescheduleNotification(context: Context, intent: Intent) {
        val notificationData = intent.getStringArrayListExtra("notificationData")!!
        Log.e(tag, notificationData.toString())


        Utility.writeKnownCuriositiesFile(context, notificationData, false)

        val time = getJsonDataFromSettings(context).time
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


    private fun readKnownCuriosities(context: Context): KnownCuriositiesData {
        var jsonString = ""
        val directory = File("${context.filesDir}/tmp") // path della directory
        val filepath = File("$directory/knowncuriosities.json") // path del filepath

        // leggi tutto il testo presente sul file
        try {
            jsonString = filepath.bufferedReader().use {
                it.readText()
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val gson = Gson()
        //salvo il tipo dell'oggetto scritto sul file
        val dataType = object : TypeToken<KnownCuriositiesData>() {}.type
        //trasformo la stringa letta la quale sarà in formato JSON nella classe utilizzata

        return gson.fromJson(jsonString, dataType)
    }
}