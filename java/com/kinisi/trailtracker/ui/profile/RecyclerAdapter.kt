package com.kinisi.trailtracker.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kinisi.trailtracker.R
import com.kinisi.trailtracker.ui.profile.RecyclerAdapter

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var itemType: TextView
        var itemDesc: TextView

        init {
            itemTitle = itemView.findViewById(R.id.title)
            itemType = itemView.findViewById(R.id.type)
            itemDesc = itemView.findViewById(R.id.description)
/*
            itemView.setOnClickListener {
                var position: Int = getAdapterPosition()
                val context = itemView.context
                val intent = Intent(context, DetailPertanyaan::class.java).apply {
                    putExtra("NUMBER", position)
                    putExtra("CODE", itemKode.text)
                    putExtra("CATEGORY", itemKategori.text)
                    putExtra("CONTENT", itemIsi.text)
                }
                context.startActivity(intent)
            }*/
        }
    }

    private val title = arrayOf(
        "Rocky Mountain",
        "Coulon Park", "Margrette's Way",
        "Red Rock Trail", "Andes Hike",
        "Golden Fountain Park", "Alki Beach"
    )

    private val type = arrayOf(
        "National Park",
        "Park", "Trail",
        "Trail", "Trail",
        "Park", "Beach"
    )

    private val description = arrayOf(
        "item 1",
        "item 2", "item 3",
        "item 4", "item 5",
        "item 6", "item 7"
    )


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.frame_trailview, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = title[i]
        viewHolder.itemType.text = type[i]
        viewHolder.itemDesc.text = description[i]

    }

    override fun getItemCount(): Int {
        return title.size
    }
}