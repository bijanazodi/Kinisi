package com.kinisi.trailtracker.ui.newactivity

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kinisi.trailtracker.R
import com.kinisi.trailtracker.models.SearchModel


class RecyclerAdapter(
    private val dataModel: ArrayList<SearchModel>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var itemType: TextView
        var itemDesc: TextView

        init {
            itemTitle = itemView.findViewById(R.id.title)
            itemType = itemView.findViewById(R.id.type)
            itemDesc = itemView.findViewById(R.id.description)


            itemView.setOnClickListener {
                val context = itemView.context
                val gmmIntentUri = Uri.parse("geo:0,0?q=${itemTitle.text}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(context, mapIntent, null)

            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.frame_trailview, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return dataModel.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as ViewHolder
        viewHolder.itemTitle.text = dataModel[position].title
        viewHolder.itemType.text = ""
        // viewHolder.itemType.text = dataModel[position].dist.toString()
        viewHolder.itemDesc.text = dataModel[position].type

    }

}