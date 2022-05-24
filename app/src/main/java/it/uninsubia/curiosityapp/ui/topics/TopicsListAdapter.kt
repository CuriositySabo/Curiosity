package it.uninsubia.curiosityapp.ui.topics

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.uninsubia.curiosityapp.R
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.Exception

class TopicsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var items : List<TopicsModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TopicsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false), parent.context
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder)
        {
            is TopicsViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(topicList: List<TopicsModel>)
    {
        items = topicList
    }

    class TopicsViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView)
    {
        private val topicsImage: ImageView = itemView.findViewById(R.id.iv_topic_image)
        private val topicsName: TextView = itemView.findViewById(R.id.topic_name)
        private val topicsCheck: CheckBox = itemView.findViewById(R.id.selected)

        fun bind(topic: TopicsModel) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(topic.image)
                .into(topicsImage)
            topicsName.text = topic.topicName
            topicsCheck.isChecked = topic.checked
            topicsImage.setOnClickListener {
                onClick(topicsImage, context)
            }
            topicsName.setOnClickListener {
                onClick(topicsName, context)
            }
            topicsCheck.setOnClickListener {
                onClick(topicsCheck, context)
            }
        }

        private fun onClick(v: View, context: Context) {
            if(v !is CheckBox)
            {
                topicsCheck.isChecked = !topicsCheck.isChecked
            }
            //recupero la lista
            var jsonString= ""
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
            //accedo alla lista
            var indexElement = 0
            when(topicsName.text)
            {
                 "Cinema" -> {
                    indexElement = 0
                }
                "Cucina" -> {
                    indexElement = 1
                }
                "Storia" -> {
                    indexElement = 2
                }
                "Tecnologia" -> {
                    indexElement = 3
                }"Sport" -> {
                indexElement = 4
            }

            }
            list[indexElement].checked = topicsCheck.isChecked
            //scrivo la lista su file
            try{
                PrintWriter(FileWriter(filePath)).use{
                    jsonString = gson.toJson(list)
                    it.write(jsonString)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}