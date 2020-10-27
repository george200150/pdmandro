package com.george200150.uni.pdmandro.todo.plants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.george200150.uni.pdmandro.R
import kotlinx.android.synthetic.main.view_item.view.*
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.data.Plant
import com.george200150.uni.pdmandro.todo.plant.PlantEditFragment

class PlantListAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<PlantListAdapter.ViewHolder>() {

    var items = emptyList<Plant>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onItemClick: View.OnClickListener;

    init {
        onItemClick = View.OnClickListener { view ->
            val item = view.tag as Plant
            fragment.findNavController().navigate(R.id.fragment_item_edit, Bundle().apply {
                putString(PlantEditFragment.ITEM_ID, item._id)
                putBoolean(PlantEditFragment.FLOWERS,item.hasFlowers)
                putString(PlantEditFragment.BLOOM,item.bloomDate)
                putString(PlantEditFragment.LOCATION,item.location)
                putString(PlantEditFragment.PHOTO,item.photo)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder $position")
        val item = items[position]
        holder.itemView.tag = item
        holder.textView.text = item.name
        holder.itemView.setOnClickListener(onItemClick)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.text
    }
}
