package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.uninsubia.curiosityapp.ui.topics.TopicsModel
import java.io.File
import java.io.IOException


class PostNotificationReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"
    private val tag = "PostNotification"
    private lateinit var repository: CuriositiesRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        // il context non è mai null
        val chosenTopic = chooseRandomTopic(context!!)

        // inizializzo piccola repository in locale
        repository = CuriositiesRepository(chosenTopic)

        // Inizializzo una lista di Curiosity Data verrà usata per memorizzare tutte le curiosità di un topic
        var curiosityList = arrayListOf<CuriosityData>()

        // utilizzo un interfaccia per far si che una volta completate le operazioni con internet si esegua il codice
        // altrimenti il programma proseguirebbe e riporterebbe i dati solo successivamente
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                print(response)
                //trasformo la risposta da parte del db in un lista di curiosità
                convertResponse(response, curiosityList)

                // estraggo un valore random contenuto nel range formato da tutti gli indici possibili nella lista
                val rnd = curiosityList.indices.random()

                // inizializzo la variabile per contenere la curiosità random scelta
                var chosenCuriosity = CuriosityData()
                Log.e(tag, rnd.toString())

                // estraggo la curiosità random con un foreach particolare che per ogni campo disponibile
                // restituiscce posizione e valore
                for ((i, curiosity: CuriosityData) in curiosityList.withIndex()) {
                    if (i == rnd) {
                        chosenCuriosity = curiosity
                    }
                }

                // non passabile con l'intent.putExtra perchè serve un tipo supportato
                // perciò lo trasformo in tale
                val curiosity = arrayListOf<String>(
                    chosenCuriosity.title,
                    chosenCuriosity.text,
                    chosenTopic
                )

                // creo la notifica e la posto
                notificationCreator(context, curiosity)
            }
        })

    }

    private fun readTopics(context: Context): List<TopicsModel> {
        var jsonString = ""
        val list: List<TopicsModel>
        val directory = File("${context.filesDir}/tmp")
        val filePath = File("$directory/topics.json")
        try {
            jsonString = filePath.bufferedReader().use {
                it.readText()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val gson = Gson()
        val dataType = object : TypeToken<List<TopicsModel>>() {}.type
        list = gson.fromJson(jsonString, dataType)
        return list
    }

    private fun print(response: Response) {
        response.curiosities?.let { curiosities ->
            curiosities.forEach { curiosity ->
                curiosity.title.let {
                    Log.e(tag, it)
                }
            }
        }

        response.exception?.let { exception ->
            exception.message?.let {
                Log.e(tag, it)
            }
        }
    }

    private fun convertResponse(response: Response, curiosityList: ArrayList<CuriosityData>) {
        response.curiosities?.let { curiosities ->
            curiosities.forEach { curiosity ->
                curiosityList.add(curiosity)
            }
        }

        response.exception?.let { exception ->
            exception.message?.let {
                Log.e(tag, it)
            }
        }

    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        repository.getResponseFromRealtimeDatabaseUsingCallback(callback)
    }

    private fun notificationCreator(context: Context, curiosity: ArrayList<String>) {
        val title = curiosity[0]
        val text = curiosity[1]
        val topic = curiosity[2]

        val requestcode = "$title $text".hashCode()

        // creazione del broadcast per la risposta
        var actionIntent = Intent(context, PositiveAnswerReceiver::class.java)

        actionIntent.putExtra("test", "per positivo")
        actionIntent.putExtra("notificationData", curiosity)

        val pIntentPositive = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, NegativeAnswerReceiver::class.java)
        actionIntent.putExtra("test", "per negativo")
        actionIntent.putExtra("notificationData", curiosity)

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

    private fun chooseRandomTopic(context: Context): String {
        // leggo tutti i topics esistenti
        val topics = readTopics(context)

        // in chosenfield  metto i topics checkati dall'utente
        val chosenFields = ArrayList<TopicsModel>()
        topics.forEach {
            if (it.checked)
                chosenFields.add(it)
        }

        // genero un numero random contenuto nel range degli indici della lista
        var rnd = (chosenFields.indices).random()
        // utilizzerò il valore appena generato per scegliere un campo tra le curiosità
        return chosenFields[rnd].topicName
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