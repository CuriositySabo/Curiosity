package it.uninsubia.curiosityapp.ui.topics

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.Exception

class DataSourceList {
    companion object {
        fun createData(context: Context?): List<TopicsModel> {
            fileCheck(context)
            return readFromFile(context)
        }

        private fun readFromFile(context: Context?): List<TopicsModel> {
            var jsonString= ""
            val list: List<TopicsModel>
            val directory = File("${context?.filesDir}/tmp")
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

        private fun fileCheck(context: Context?) {
            val fileName = "topics.json"
            val path = "${context?.filesDir}/tmp/" + fileName
            val file = File(path)
            if (!file.exists())
                createFile(fileName, context)
        }

        private fun createFile(fileName: String, context: Context?) {
            val directory = File(context?.filesDir, "tmp")
            directory.mkdirs()
            val filepath = File(directory, fileName)
            val list = createList()
            try{
                PrintWriter(FileWriter(filepath)).use{
                    val gson = Gson()
                    val jsonString = gson.toJson(list)
                    it.write(jsonString)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

        private fun createList() : ArrayList<TopicsModel> {
            val list : ArrayList<TopicsModel> = ArrayList()
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
                    false
                )
            )
            list.add(
                TopicsModel(
                    "Tecnologia",
                    "https://raw.githubusercontent.com/shishioTsukasa/Immagini/master/topics/tecnologia.jpg",
                    false
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
    }
}

