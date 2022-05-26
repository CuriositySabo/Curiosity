package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.uninsubia.curiosityapp.ui.topics.TopicsModel
import java.io.File
import java.io.IOException


class PostNotificationReceiver : BroadcastReceiver() {
    private val channelid = "notifyCuriosity"
    private val tag = "PostNotification"

    override fun onReceive(context: Context?, intent: Intent?) {

        val curiosity: ArrayList<String> = retrieveCuriosity(context!!)

        val title =curiosity[0]
        val text = curiosity[1]
        val topic = curiosity[2]

        val requestcode = "$title $text".hashCode()
        Log.e(tag, " \nrequest code : $requestcode \ntitle : $title \ntext : $text \ntopic: $topic")

        //recupero del tempo per schedulare la notifica putextra e getextra non funzionano bene!
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

    private fun retrieveCuriosity(context: Context): ArrayList<String> {
        // leggo tutti i topics esistenti
        val topics = readTopics(context)
        // salvo la reference al db
        val database = Firebase.database.reference

        // in chosenfield  metto i topics checkati dall'utente
        val chosenFields = ArrayList<TopicsModel>()
        topics.forEach {
            if (it.checked)
                chosenFields.add(it)
        }


        // genero un numero random contenuto nel range degli indici della lista
        var rnd = (chosenFields.indices).random()
        // utilizzo il valore appena generato per scegliere un campo tra le curiosità
        val field = chosenFields[rnd]
        Log.e(tag, field.topicName)
        // classe che rappresenta come le informazioni sono rappresentate dal db
        var curiosityData = CuriosityData()
        // entro nel campo curiosità del db,
        // poi nel suo nodo figlio corrispondente e successivamente estrapolo una curiosità
        database.child("curiosità").child(field.topicName).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            // genero un valore random tra i valori possibili
            // (ovvero il numero di curiosità presenti in un determinato topic)
            rnd = (0 until it.children.count()).random()
            Log.e(tag, it.children.count().toString())
            // for each particolare per contare gli indici passati e salvare il valore del children al momento
            for ((count, children: DataSnapshot) in it.children.withIndex()) {
                // quando l'indice che si sta prendendo in considerazione in questo momento è uguale a quello random
                if (rnd == count) {
                    //salvo il valore nella mia classe
                    curiosityData = children.getValue(CuriosityData::class.java)!!
                }
                Log.i(tag, children.toString())
            }
            Log.e(tag, curiosityData.toString())
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it) // se l'operazione fallisce mostro un errore
        }
        Thread.sleep(200)
        Log.e(tag, curiosityData.toString())
        //trasformo curiosity data nell'oggetto che utilizzano le notifiche
        return arrayListOf<String>(curiosityData.title, curiosityData.text, field.topicName)
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


}