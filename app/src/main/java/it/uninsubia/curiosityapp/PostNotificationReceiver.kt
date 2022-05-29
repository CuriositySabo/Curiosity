package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
                //trasformo la risposta da parte del db in un lista di curiosità
                val curiositiesList = convertResponse(response)

                // leggo le curiosità ricevute
                val knownCuriositiesmap = Utility.readKnownCuriosities(context).knowncuriosities

                // scarico dal db la mappa con il numero massimo di curiosità per ogni topic
                val totalcuriositiesMap = Utility.getMapOfTopicsCuriosities(curiositiesList)
                Log.e(tag, totalcuriositiesMap.toString())

                // stessa mappa ma con quante curiosità sono già state ricevute  la utilizzo per fare il confronto con quella precedente
                var alreadyInKnownCounterMap = hashMapOf<String, Int>()
                for (topic in Utility.getTopicsOnDb(curiositiesList)) {
                    alreadyInKnownCounterMap[topic] =
                        if (!knownCuriositiesmap[topic].isNullOrEmpty()) knownCuriositiesmap[topic]!!.count()
                        else 0
                }
                Log.e(tag, alreadyInKnownCounterMap.toString())


                // creo la lista con i topic selezionati dall'utente
                val possibleTopics = listChosenTopics(context)
                // per ognuno di essi controllo che non siano già state ricevute tutte le curiosità
                // se sono già state ricevute tutte levo il topic dai topic possibili
                totalcuriositiesMap.forEach() {
                    val key = it.key
                    if (it.value == alreadyInKnownCounterMap[key]) {
                        possibleTopics.remove(key)
                    }
                }

                // init variabili per selezionare una curiosità random dalla lista con le curiosità
                var randomIndex: Int
                var chosenTopic: String?
                var randitemcode = 0
                var chosenCuriosity: CuriosityData

                // se vi sono topic che hanno curiosità non già inviate cerco una tra esse
                if (possibleTopics.size == 0) {
                    // inizializzo un dato per dopo
                    chosenCuriosity = CuriosityData()
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
                    } while (!knownCuriositiesmap[chosenTopic].isNullOrEmpty() &&
                        knownCuriositiesmap[chosenTopic]!!.contains(randitemcode)
                    )
                }

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

    private fun notificationwithButtonCreator(context: Context, chosenCuriosity: CuriosityData) {
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

    private fun notificationCreator(context: Context) {
        // creazione della notifica
        val notification = NotificationCompat.Builder(context, channelid)
            .setSmallIcon(R.mipmap.ic_mars_foreground)
            .setContentTitle("Sai tutto oramai!")
            .setContentText("Sono finite le curiosità!")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Sono finite le curiosità! Scegli un altro topic oppure Cancella pure questa notifica!")
            )
            .build()


        // il notification manager permette di postare la notifica
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, notification)
    }

    private fun listChosenTopics(context: Context): ArrayList<String> {
        // leggo tutti i topics esistenti
        val topics = Utility.readTopicsFile(context)

        // in chosenfield  metto i topics checkati dall'utente
        val chosenFields = ArrayList<String>()

        // controllo quali sono checkati
        topics.forEach {
            if (it.checked)
                chosenFields.add(it.topicName)
        }

        return chosenFields
    }

    /*private fun print(response: Response) {
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
    }*/
}