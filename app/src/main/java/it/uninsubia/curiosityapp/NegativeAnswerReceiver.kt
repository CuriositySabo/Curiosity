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
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class NegativeAnswerReceiver : BroadcastReceiver() {
    // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            Log.e("negativo", intent.getStringExtra("test")!!)
        }

        val stringArray = intent!!.getStringArrayExtra("notificationData")!!
        val requestcode = stringArray.hashCode()

        val notificationData = NotificationData(stringArray[0], stringArray[1], stringArray[2])
        writeKnownCuriosities(context!!, notificationData)

        val time = getJsonDataFromSettings(context).time
        Log.e("NegativeAnswer", time.toString())

        Toast.makeText(context, "Notifica partita", Toast.LENGTH_LONG).show()
        val notificationManager =
            NotificationManagerCompat.from(context)
        notificationManager.cancel(200)

        //creazione intent con il broadcast
        val actionIntent = Intent(context, PostNotificationReceiver::class.java)
        actionIntent.putExtra("notificationData", stringArray)

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
            context.getSystemService(ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento
        alarmManager.set(AlarmManager.RTC_WAKEUP, momentTime + time, pendingIntent)
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
        Log.e("Positive Answer", settings.toString())
        return settings
    }


    private fun writeKnownCuriosities(
        context: Context,
        notificationData: NotificationData
    ) {
        val topic = notificationData.topic
        val title = notificationData.title
        val text = notificationData.text

        val directory = File(context.filesDir, "tmp") // path directory tmp
        val filepath = File(directory, "knowncuriosities.json") // path del file

        val fileData = readKnownCuriosities(context)

        //se il file contiene già la mappa col topic la copio e modifico quella
        //altrimenti ne creo una nuova
        val curiositytoadd: HashMap<Long, Boolean> =
            if (fileData.knowncuriosity[topic] != null) {
                fileData.knowncuriosity[topic]!!
            } else {
                hashMapOf()
            }

        //genero il codice della curiosità equivalente a quello del db
        val code = "$title $text".hashCode().toLong()
        //modifico la mappa che contiene le curiosità di un determinato topic aggiungendo una entry
        curiositytoadd[code] = false
        //sovrascrivo la mappa del topic corrispondente con quella nuova o modificata
        fileData.knowncuriosity[topic] = curiositytoadd


        //scrivo le modifiche effettuate sul file
        try {
            PrintWriter(FileWriter(filepath)).use {
                val gson = Gson()
                val jsonString = gson.toJson(fileData)
                // scrive la classe in formato json sul file
                it.write(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readKnownCuriosities(context: Context): KnownCuriosityData {
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
        val dataType = object : TypeToken<KnownCuriosityData>() {}.type
        //trasformo la stringa letta la quale sarà in formato JSON nella classe utilizzata

        return gson.fromJson(jsonString, dataType)
    }
}