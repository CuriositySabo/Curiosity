package it.uninsubia.curiosityapp.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import it.uninsubia.curiosityapp.*
import it.uninsubia.curiosityapp.database.CuriositiesRepository
import it.uninsubia.curiosityapp.database.CuriosityData
import it.uninsubia.curiosityapp.database.FirebaseCallback
import it.uninsubia.curiosityapp.database.Response
import kotlin.random.Random

// Riceve il broadcast e posta la notifica
class PostNotificationReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"
    private val tag = "PostNotification"

    // Inizializzo piccola repository in locale
    private val repository: CuriositiesRepository = CuriositiesRepository()

    // Ricevuto il broadcast, ovvero la notifica di un dato evento al sistema, l'applicazione si comporterà nel modo seguente:
    override fun onReceive(context: Context?, intent: Intent?) {
        notifyCuriosity(context!!)
    }

    // Ricevute le informazioni dal db posta la notifica nuova
    private fun notifyCuriosity(context: Context) {
        // il context non è mai null

        // utilizzo un interfaccia per far si che una volta completate le operazioni con internet si esegua il codice
        // altrimenti il programma proseguirebbe e riporterebbe i dati solo successivamente
        getResponseUsingCallback(object : FirebaseCallback {
            override fun onResponse(response: Response) {
                //trasformo la risposta da parte del db in un lista di curiosità
                val curiositiesList = convertResponse(response)

                // scarico dal db la mappa con il numero massimo di curiosità per ogni topic
                val totalcuriositiesMap = Utility.getMapOfTopicsCuriosities(curiositiesList)

                // sceglie una curiosità casuale che non sia stata già ricevuta e che faccia parte dei topic scelti
                val chosenCuriosity =
                    generateCuriosity(context, curiositiesList, totalcuriositiesMap)

                Log.e(tag, "${chosenCuriosity.title} ${chosenCuriosity.topic}")

                Log.e(tag, "POSTO")

                // se chosenCuriosity è inizializzato vuoto faccio una notifica senza bottoni e con un testo statico
                // altrimenti la creo con la curiosità presa dal db
                if (chosenCuriosity == CuriosityData())
                    notificationCreator(context)
                else
                    notificationwithButtonCreator(context, chosenCuriosity)
            }
        })
    }

    // Genera la curiosità casuale da inviare controllando quelle gia ricevute e i topic scelti
    private fun generateCuriosity(
        context: Context,
        curiositiesList: ArrayList<CuriosityData>,
        totalcuriositiesMap: HashMap<String, Int>
    ): CuriosityData {

        // leggo le curiosità già ricevute
        val receivedCuriositiesmap = Utility.readKnownCuriosities(context).knowncuriosities

        Log.e(tag, totalcuriositiesMap.toString())

        // stessa mappa ma con quante curiosità sono già state ricevute la utilizzo per fare il confronto con quella precedente
        val alreadyInReceivedCounter = hashMapOf<String, Int>()
        for (topic in Utility.getTopicsOnDb(curiositiesList)) {
            alreadyInReceivedCounter[topic] =
                if (!receivedCuriositiesmap[topic].isNullOrEmpty()) receivedCuriositiesmap[topic]!!.count()
                else 0
        }
        Log.e(tag, alreadyInReceivedCounter.toString())


        // creo la lista con i topic selezionati dall'utente
        val possibleTopics = Utility.listChosenTopics(context)
        // per ognuno di essi controllo che non siano già state ricevute tutte le curiosità
        // se sono già state ricevute tutte levo il topic dai topic possibili
        totalcuriositiesMap.forEach {
            val key = it.key
            if (it.value == alreadyInReceivedCounter[key]) {
                possibleTopics.remove(key)
            }
        }

        // init variabili per selezionare una curiosità random dalla lista con le curiosità
        var randomIndex: Int
        var chosenTopic: String?
        var randitemcode: Int
        var chosenCuriosity: CuriosityData

        // se vi sono topic che hanno curiosità non già inviate cerco una tra esse
        if (possibleTopics.size == 0) {
            // inizializzo un dato per dopo
            chosenCuriosity = CuriosityData()
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEditor = prefs.edit()
            prefEditor.putBoolean("notification", false)
            prefEditor.apply()
        } else {
            do {
                do {
                    randomIndex = Random.nextInt(curiositiesList.size)
                    chosenCuriosity = curiositiesList[randomIndex]
                    chosenTopic = chosenCuriosity.topic
                    // ripeto la generazione finchè non trovo la curiosià di un topic possibile
                } while (!possibleTopics.contains(chosenTopic))
                // genero il codice della curiosità
                randitemcode =
                    "${chosenCuriosity.title} ${chosenCuriosity.text} ${chosenCuriosity.topic}".hashCode()
                // se vi sono curiosità sul file e tra le curiosità già ricevute non vi è quella generata posso andare avanti
            } while (!receivedCuriositiesmap[chosenTopic].isNullOrEmpty() &&
                receivedCuriositiesmap[chosenTopic]!!.contains(randitemcode)
            )
        }
        return chosenCuriosity
    }

    // Trasforma le curiosità in una lista
    private fun convertResponse(response: Response): ArrayList<CuriosityData> {
        // Inizializzo una lista di Curiosity Data verrà usata per memorizzare tutte le curiosità
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

    // Meccanismo per sapere quando il download dal db ha finito
    private fun getResponseUsingCallback(callback: FirebaseCallback) {
        repository.getResponseFromRealtimeDatabaseUsingCallback(callback)
    }

    // Crea la notifica standard coi bottoni per permettere l'interazione con l'utente e la posta
    private fun notificationwithButtonCreator(context: Context, chosenCuriosity: CuriosityData) {
        val curiosity = arrayListOf(
            chosenCuriosity.title,
            chosenCuriosity.text,
            chosenCuriosity.topic
        )

        val requestcode = "${curiosity[0]} ${curiosity[1]} ${curiosity[2]}".hashCode()

        // Creazione del broadcast lo sapevo per la risposta data dall'utente
        var actionIntent = Intent(context, PositiveAnswerReceiver::class.java)

        // Passo i dati della notifica che visualizzo mi serviranno poi
        actionIntent.putExtra("notificationData", curiosity)

        // Uso un wrapper perchè i bottoni richedono un intent di questo tipo
        val pIntentPositive = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        // Creazione del broadcast lo sapevo per la risposta data dall'utente
        actionIntent = Intent(context, NegativeAnswerReceiver::class.java)

        // Passo i dati della notifica che visualizzo mi serviranno poi
        actionIntent.putExtra("notificationData", curiosity)

        // Uso un wrapper perchè i bottoni richedono un intent di questo tipo
        val pIntentNegative = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        // Scelgo l'immagine del topic corrispondente da visualizzare nella notifica
        val imageStream = when (curiosity[2]) {
            "Storia" -> context.resources.openRawResource(R.raw.storia)
            "Tecnologia" -> context.resources.openRawResource(R.raw.tecnologia)
            "Sport" -> context.resources.openRawResource(R.raw.sport)
            "Cinema" -> context.resources.openRawResource(R.raw.cinema)
            "Cucina" -> context.resources.openRawResource(R.raw.cucina)
            else -> context.resources.openRawResource(R.raw.mars_planet)
        }

        // La faccio diventare una bitmap
        val bitmap = BitmapFactory.decodeStream(imageStream)

        // Creazione della notifica
        val notification = NotificationCompat.Builder(context, channelid)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(bitmap)
            .setContentTitle(curiosity[0])
            .setContentText(curiosity[1])
            .setPriority(NotificationCompat.PRIORITY_MIN)
//          sui due bottoni mostrati dalla notifica viene assegnata un azione da eseguire con il
//          con il PendingIntent
            .addAction(R.drawable.ic_sapevo, "Lo sapevo", pIntentPositive)
            .addAction(R.drawable.ic_non_sapevo, "Non lo sapevo", pIntentNegative)
            .setStyle( // Stile della notifica settato così senò non mostrava la
                NotificationCompat.BigTextStyle()
                    .bigText(curiosity[1])
            )
            .setOngoing(true) // Rende la notifica non cancellabile
            .build()


        // Il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, notification)
    }

    // Crea la notifica per quando sono finite le curiosità. Senza bottoni e intent
    private fun notificationCreator(context: Context) {
        // creazione della notifica
        val notification = NotificationCompat.Builder(context, channelid)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Sai tutto oramai!")
            .setContentText("Sono finite le curiosità!")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Sono finite le curiosità! Scegli un altro topic oppure cancella pure questa notifica!")
            )
            .build()


        // il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, notification)
    }




}