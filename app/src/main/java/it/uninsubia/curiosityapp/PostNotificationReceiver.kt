package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.uninsubia.curiosityapp.ui.topics.TopicsModel
import java.io.File
import java.io.IOException
import kotlin.random.Random


class PostNotificationReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"
    private val tag = "PostNotification"

    // inizializzo piccola repository in locale
    private val repository: CuriositiesRepository = CuriositiesRepository()

    override fun onReceive(context: Context?, intent: Intent?) {
        notifyCuriosity(context!!)
    }

    private fun notifyCuriosity(context: Context) {
        // il context non è mai null

        // utilizzo un interfaccia per far si che una volta completate le operazioni con internet si esegua il codice
        // altrimenti il programma proseguirebbe e riporterebbe i dati solo successivamente
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
//                print(response)
                //trasformo la risposta da parte del db in un lista di curiosità
                val curiosityList = convertResponse(response)

                val knownCuriositiesmap = readKnownCuriosities(context).knowncuriosities
                Log.e(tag, knownCuriositiesmap.toString())

                var randomIndex: Int
                var chosenTopic: String
                do {
                    chosenTopic = chooseRandomTopic(context)
                    randomIndex = Random.nextInt(curiosityList.size)
                    val randitemcode =
                        "${curiosityList[randomIndex].title} ${curiosityList[randomIndex].text} ${curiosityList[randomIndex].topic}".hashCode()
                } while (curiosityList[randomIndex].topic != chosenTopic || knownCuriositiesmap.contains(
                        randitemcode
                    )
                )
                // estraggo un valore random contenuto nel range formato da tutti gli indici possibili nella lista
                val chosenCuriosity = curiosityList[randomIndex]
                Log.e(tag, "${chosenCuriosity.title} ${chosenCuriosity.topic}")
                // estraggo la curiosità random con un foreach particolare che per ogni campo disponibile
                // restituiscce posizione e valore
                // creo la notifica e la posto
                notificationCreator(context, chosenCuriosity)
            }
        })
    }

    private fun readTopicsFile(context: Context): List<TopicsModel> {
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
                    Log.i(tag, it)
                }
            }
        }

        response.exception?.let { exception ->
            exception.message?.let {
                Log.e(tag, it)
            }
        }
    }

    private fun convertResponse(response: Response): ArrayList<CuriosityData> {
        // Inizializzo una lista di Curiosity Data verrà usata per memorizzare tutte le curiosità di un topic
        val curiosityList = arrayListOf<CuriosityData>()

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

        return curiosityList

    }

    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        repository.getResponseFromRealtimeDatabaseUsingCallback(callback)
    }

    private fun notificationCreator(context: Context, chosenCuriosity: CuriosityData) {
        val curiosity = arrayListOf(
            chosenCuriosity.title,
            chosenCuriosity.text,
            chosenCuriosity.topic
        )

        val requestcode = "${curiosity[0]} ${curiosity[1]} ${curiosity[2]}".hashCode()

        // creazione del broadcast per la risposta
        var actionIntent = Intent(context, PositiveAnswerReceiver::class.java)

        actionIntent.putExtra("notificationData", curiosity)

        val pIntentPositive = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, NegativeAnswerReceiver::class.java)

        actionIntent.putExtra("notificationData", curiosity)

        val pIntentNegative = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )



        val imageStream = when (curiosity[2]) {
            "Storia" -> context.resources.openRawResource(R.raw.storia)
            "Tecnologia" -> context.resources.openRawResource(R.raw.tecnologia)
            "Sport" -> context.resources.openRawResource(R.raw.sport)
            "Cinema" -> context.resources.openRawResource(R.raw.cinema)
            "Cucina" -> context.resources.openRawResource(R.raw.cucina)
            else -> context.resources.openRawResource(R.raw.cucina)
        }


//        imageStream = context.resources.(R.mipmap.ic_cinema_round)
//        var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.cinemadrawable)

//        Bitmap.cre(R.mipmap.ic_cinema_round)


//        val icon =
//            BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)

        val bitmap = BitmapFactory.decodeStream(imageStream)


        // creazione della notifica
        val notification = NotificationCompat.Builder(context, channelid)
            .setSmallIcon(R.mipmap.ic_mars_foreground)
            .setLargeIcon(bitmap)
            .setContentTitle(curiosity[0])
            .setContentText(curiosity[1])
            .setPriority(NotificationCompat.PRIORITY_MIN)
            //sui due bottoni mostrati dalla notifica viene assegnata un azione da eseguire con il
            //con il PendingIntent
            .addAction(R.drawable.ic_sapevo, "Lo sapevo", pIntentPositive)
            .addAction(R.drawable.ic_non_sapevo, "Non lo sapevo", pIntentNegative)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(curiosity[1])
            )
            .build()


        // il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, notification)
    }

    private fun chooseRandomTopic(context: Context): String {
        // leggo tutti i topics esistenti
        val topics = readTopicsFile(context)

        // in chosenfield  metto i topics checkati dall'utente
        val chosenFields = ArrayList<TopicsModel>()
        topics.forEach {
            if (it.checked)
                chosenFields.add(it)
        }

        // genero un numero random contenuto nel range degli indici della lista
        val rnd = (chosenFields.indices).random()
        // utilizzerò il valore appena generato per scegliere un campo tra le curiosità
        return chosenFields[rnd].topicName
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