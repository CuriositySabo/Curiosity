package it.uninsubia.curiosityapp.ui.topics

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.Exception

class DataSourceList {
    companion object {
        fun createData(context: Context?): ArrayList<TopicsModel> {
            val list = ArrayList<TopicsModel>()
            list.add(
                TopicsModel(
                    "Cinema",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cinema.jpg",
                    false
                )
            )
            list.add(
                TopicsModel(
                    "Cucina",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cucina.jpg",
                    false
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
                    false
                )
            )
            return list
        }

        private fun fileCheck(context: Context?) :Boolean
        {
            val fileName = "topics.json"
            val file = File(fileName)
            if (file.exists())
                Log.e("file","il file esiste")
            else
            {
                Log.e("file","il file ==== NON ===== esiste")
                createFile(fileName, context)
            }
            return file.exists()
        }

        private fun createFile(fileName: String, context: Context?)
        {
            val path = context?.filesDir
            val directory = File(path, "tmp")
            directory.mkdirs()
            File(directory,fileName)
            //scrivo le informazioni di default
            try{
                PrintWriter(FileWriter(directory)).use{
                    val gson = Gson()
                    var topic = TopicsModel(
                        "Cinema",
                        "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cinema.jpg",
                        false
                    )
                    var jsonString = gson.toJson(topic)
                    it.write(jsonString)
                    topic = TopicsModel(
                        "Cucina",
                        "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/cucina.jpg",
                        false
                    )
                    jsonString = gson.toJson(topic)
                    it.append(jsonString)
                    topic = TopicsModel(
                        "Storia",
                        "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/storia2.jpg",
                        true
                    )
                    jsonString = gson.toJson(topic)
                    it.append(jsonString)
                    topic = TopicsModel(
                        "Tecnologia",
                        "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/tecnologia.jpg",
                        true
                    )
                    jsonString = gson.toJson(topic)
                    it.append(jsonString)
                    topic = TopicsModel(
                        "Sport",
                        "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/sport.jpg",
                        false
                    )
                    jsonString = gson.toJson(topic)
                    it.append(jsonString)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}