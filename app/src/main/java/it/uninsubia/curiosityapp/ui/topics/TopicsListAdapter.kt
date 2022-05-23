package it.uninsubia.curiosityapp.ui.topics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import it.uninsubia.curiosityapp.R

class TopicsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var items : List<TopicsModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TopicsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
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

    class TopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val topicsImage: ImageView = itemView.findViewById(R.id.iv_topic_image)
        private val topicsName: TextView = itemView.findViewById(R.id.topic_name)
        fun bind(topic: TopicsModel) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(topic.image)
                .into(topicsImage)
            topicsName.text = topic.topicName
        }
    }
}