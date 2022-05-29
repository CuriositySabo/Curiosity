package it.uninsubia.curiosityapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.uninsubia.curiosityapp.ui.topics.TopicsModel
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class Utility() {

    companion object {
        fun notificationLauncher(context: Context) {
            var time = 1
            time *= 2000 // in realtà ce ne mette di più

            SettingsData(time)

            Toast.makeText(context, "Notifica Settata", Toast.LENGTH_LONG).show()

            //creazione intent con il broadcast da inviare
            val intent = Intent(context, PostNotificationReceiver::class.java)

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    context.hashCode(),
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )

            val momentTime = System.currentTimeMillis() // per salvare l'orario in quel dato momento


            val alarmManager =
                context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager //servizio di sistema per impostare un comportamento in un dato momento

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

        // scrive sempre un nuovo oggetto quindi crea sempre il file
        fun writeSettingsFile(settingsData: SettingsData, context: Context) {

            val directory = File("${context.filesDir}/tmp") // path della directory
            val filepath = File("$directory/settings.json") // path del filepath

            try {
                PrintWriter(FileWriter(filepath)).use {
                    val gson = Gson()
                    val jsonString = gson.toJson(settingsData)
                    // scrive la classe contenente i settings in formato json sul file
                    it.write(jsonString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // scrive un oggetto vuoto solo se il file non esiste già
        fun writeKnownCuriositiesFile(context: Context) {
            val map = KnownCuriositiesData().knowncuriosities
            val topics = initTopicList()
            for (topic in topics) {
                map[topic.topicName] = hashMapOf<Int, Boolean>()
            }
            val directory = File(context.filesDir, "tmp") // path directory tmp
            val filepath = File(directory, "knowncuriosities.json") // path del file

            // creo il file knowncuriosity se non esiste
            if (!filepath.exists()) {
                PrintWriter(FileWriter(filepath)).use {
                    val gson = Gson()
                    // inizializzo con la classe vuota il file
                    val jsonString = gson.toJson(map)
                    it.write(jsonString)
                }
            }
        }

        // scrive sul file partendo da un arraylist
        fun writeKnownCuriositiesFile(
            context: Context,
            notificationData: ArrayList<String>,
            known: Boolean
        ) {

            val title = notificationData[0]
            val text = notificationData[1]
            val topic = notificationData[2]

            val directory = File(context.filesDir, "tmp") // path directory tmp
            val filepath = File(directory, "knowncuriosities.json") // path del file

            val fileData = readKnownCuriosities(context)
            //creo una copia della mappa all'interno del file
            val knownCuriositiesModified = readKnownCuriosities(context).knowncuriosities

            //se il file contiene già la mappa col topic la copio e modifico quella
            //altrimenti ne creo una nuova

            val curiositytoadd: HashMap<Int, Boolean> =
                if (fileData.knowncuriosities[topic] != null) {
                    fileData.knowncuriosities[topic]!!
                } else {
                    hashMapOf()
                }

            //genero il codice della curiosità equivalente a quello del db
            val code = "$title $text $topic".hashCode()

            curiositytoadd[code] = known

            //modifico la mappa che contiene le curiosità di un determinato topic aggiungendo una entry
            knownCuriositiesModified[topic] = curiositytoadd
            //sovrascrivo la mappa del topic corrispondente con quella nuova o modificata
            fileData.knowncuriosities = knownCuriositiesModified

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

        fun readKnownCuriosities(context: Context): KnownCuriositiesData {
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

        // scrive sul file topics partendo da un array list
        fun writeTopicsFile(topicsList: ArrayList<TopicsModel>, context: Context?) {
            val directory = File("${context!!.filesDir}/tmp")
            val filepath = File("$directory/topics.json") // path del file

            try {
                PrintWriter(FileWriter(filepath)).use {
                    val gson = Gson()
                    val jsonString = gson.toJson(topicsList)
                    it.write(jsonString)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        // legge dal file topics
        fun readTopicsFile(context: Context): List<TopicsModel> {
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

        fun initTopicList() : ArrayList<TopicsModel> {
            val list : ArrayList<TopicsModel> = ArrayList()
            list.add(
                TopicsModel(
                    "Cinema",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cinema.jpg",
                    true
                )
            )
            list.add(
                TopicsModel(
                    "Cucina",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cucina.jpg",
                    true
                )
            )
            list.add(
                TopicsModel(
                    "Storia",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/storia2.jpg",
                    true
                )
            )
            list.add(
                TopicsModel(
                    "Tecnologia",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/tecnologia.jpg",
                    true
                )
            )
            list.add(
                TopicsModel(
                    "Sport",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/sport.jpg",
                    true
                )
            )
            return list
        }


        // serve la response per forza!
        fun getTopicCuriosites(topic: String, list: ArrayList<CuriosityData>): Int {
            //get all curiosities from firebase -> return number of curiosities of that topic
            var num = 0
            for (curiosityData in list) {
                if (curiosityData.topic == topic)
                    num++
            }
            return num
        }

        fun getTopicsOnDb(allcuriosities: ArrayList<CuriosityData>): ArrayList<String> {
            val topicsonDb = arrayListOf<String>()
            for (curiosity in allcuriosities) {
                if(!topicsonDb.contains(curiosity.topic))
                    topicsonDb.add(curiosity.topic)
            }

            return  topicsonDb
        }

        fun getMapOfTopicsCuriosities(curiositiesList :ArrayList<CuriosityData> ) : HashMap<String, Int> {
            // mappa che contiene il numero di curiosità totali per topic
            val topicsCuriositiesMap = hashMapOf<String, Int>()
            val topicsList = getTopicsOnDb(curiositiesList)
            for(topic in topicsList) {
                topicsCuriositiesMap[topic] = getTopicCuriosites(topic, curiositiesList)
            }
            return topicsCuriositiesMap
        }
    }

}