package it.uninsubia.curiosityapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

        val stringArray = intent!!.getStringArrayExtra("notificationData")!!
        val title = stringArray[0]
        val text = stringArray[1]
        val topic = stringArray[2]

        val requestcode = "$title $text".hashCode()
        Log.e(tag, " \nrequest code : $requestcode \ntitle : $title \ntext : $text \ntopic: $topic")

        //recupero del tempo per schedulare la notifica putextra e getextra non funzionano bene!
        // creazione del broadcast per la risposta
        var actionIntent = Intent(context, PositiveAnswerReceiver::class.java)

        actionIntent.putExtra("test", "per positivo")
        actionIntent.putExtra("notificationData", stringArray)

        val pIntentPositive = PendingIntent.getBroadcast(
            context, requestcode, actionIntent,
            PendingIntent.FLAG_MUTABLE
        )

        actionIntent = Intent(context, NegativeAnswerReceiver::class.java)
        actionIntent.putExtra("test", "per negativo")
        actionIntent.putExtra("notificationData", stringArray)

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

        retrieveCuriosity(context)
    }

    private fun retrieveCuriosity(context: Context){
        val topics = readTopics(context)
        val database = Firebase.database.reference
        val chosenFields = ArrayList<TopicsModel>()
        topics.forEach {
            if(it.checked)
                chosenFields.add(it)
        }

        var rnd = (chosenFields.indices).random()
        val field = chosenFields[rnd]
        Log.i(tag, field.topicName)

        val curiositiesinField =
            database.child("curiosità").child(field.topicName).get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
//                rnd = (0 until it.children.count()).random()
                var count = 0
//                Log.i (TAG , curiosities.toString())
                /*for (children: DataSnapshot in it.children) {
                    if (rnd == count)
                        curiosityData = children.getValue(CuriosityData::class.java)!!
                    Log.i(TAG, children.toString())
                    count++
                }
                Log.e(TAG, curiosityData.toString())*/
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
//        Thread.sleep(500)
//        Log.e(TAG, curiosityData.toString())
//        return curiosityData
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