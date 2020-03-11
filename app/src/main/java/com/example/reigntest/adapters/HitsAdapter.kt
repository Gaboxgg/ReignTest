package com.example.reigntest.adapters

import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reigntest.R
import com.example.reigntest.data.HitPojo
import kotlinx.android.synthetic.main.item_hit.view.*
import java.text.SimpleDateFormat

import java.util.*

class HitsAdapter (var hits: List<HitPojo>,val listener: (HitPojo) -> Unit) : RecyclerView.Adapter<HitsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = hits[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_hit, parent, false))
    }

    override fun getItemCount(): Int {
        return hits.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(hit: HitPojo) {
            itemView.title.text=if(hit.title!=null) hit.title else hit.story_title
            itemView.dateAndAuthor.text=formatText(hit.created_at,hit.author)
            itemView.setOnClickListener { listener(hit) }
        }

        //method to format the lower text in each row compose by date - author
        private fun formatText(createdAt: String, author: String): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            val strDate: Date = sdf.parse(createdAt)
            val diff: Long = System.currentTimeMillis() - strDate.getTime()
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            var date: String = ""
            if (diff < 1000) {
                date = "1s"
            } else if (diff > 1000 && minutes < 1) {
                date = seconds.toString() + "s"
            } else if (minutes in 1..59) {
                date = minutes.toString() + "m"
            } else if (hours >= 1) {
                var real_minutes:Long = minutes%(hours*60)
                var minu: Char = (real_minutes*100/60).toString().elementAt(0)

                if(minu.equals('0'))
                    date = "$hours"+'h'
                else
                    date = "$hours.$minu"+"h"
            }
            if (hours >= 24){
                date = "yesterday"
        }

            return "$author - $date"
        }
    }

    public fun removeItem(position: Int) {
        val list = hits.toMutableList()
        list.removeAt(position)
        hits = list
        notifyItemRemoved(position)
    }

    fun getData(): List<HitPojo> {
        return hits
    }
}