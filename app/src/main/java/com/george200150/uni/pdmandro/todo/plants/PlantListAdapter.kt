package com.george200150.uni.pdmandro.todo.plants

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    var plants = emptyList<Plant>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onPlantClick: View.OnClickListener

    init {
        onPlantClick = View.OnClickListener { view ->
            val plant = view.tag as Plant
            fragment.findNavController().navigate(R.id.fragment_item_edit, Bundle().apply {
                putString(PlantEditFragment.ITEM_ID, plant._id)
            })
        }
    }

    fun searchAndFilter(s: String, hasFlowers: String): MutableList<Plant> {
        val filteredList: MutableList<Plant> = ArrayList()
        val s2 = s.toLowerCase().trim()
        for (plant in plants) {
            if (s2.isNotEmpty() && !plant.name.contains(s2))
                continue
            if (hasFlowers.isNotEmpty() && plant.hasFlowers.toString() != hasFlowers)
                continue
            filteredList.add(plant)
        }
        return filteredList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder $position")
        val plant = plants[position]
        holder.itemView.tag = plant
        holder.name.text = plant.name
        holder.bloomDate.date.text = plant.bloomDate
        holder.hasFlowers.text = plant.hasFlowers.toString()
        holder.ivImage.setImageURI(Uri.parse(plant.imageURI))
        holder.itemView.setOnClickListener(onPlantClick)
    }

    override fun getItemCount() = plants.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.name
        val hasFlowers: TextView = view.hasFlowers
        val bloomDate: TextView = view.date
        val ivImage: ImageView = view.ivImage
    }
}
