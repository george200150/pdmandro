package com.george200150.uni.pdmandro.todo.plant

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.george200150.uni.pdmandro.R
import com.george200150.uni.pdmandro.core.TAG
import kotlinx.android.synthetic.main.fragment_item_edit.*

class PlantEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "_ID"
        const val NAME = "flower_name"
        const val FLOWERS = "has_flowers"
        const val BLOOM = "bloom_date"
        const val LOCATION = "location"
        const val PHOTO = "photo"
    }

    private lateinit var viewModel: PlantEditViewModel
    private var plantId: String? = null
    private var plantName: String? = null
    private var hasFlowers: Boolean = false
    private var bloomDate: String? = null
    private var location: String? = null
    private var photo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                plantId = it.getString(ITEM_ID).toString()
            }
            if (it.containsKey(NAME))
            {
                plantName=it.getString(NAME);
            }
            if (it.containsKey(FLOWERS))
            {
                hasFlowers=it.getBoolean(FLOWERS);
            }
            if (it.containsKey(BLOOM))
            {
                bloomDate=it.getString(BLOOM);
            }
            if (it.containsKey(LOCATION))
            {
                location=it.getString(LOCATION);
            }
            if (it.containsKey(PHOTO))
            {
                photo=it.getString(PHOTO);
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v(TAG, "onViewCreated")
        plant_name.setText(plantName)
        plant_has_flowers.isChecked = hasFlowers;
        bloom_date.setText(bloomDate);
        plant_location.setText(location);
        plant_photo.setText(photo);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save plant")
            viewModel.saveOrUpdateItem(
                plant_name.text.toString(),
                plant_has_flowers.isChecked,
                bloom_date.text.toString(),
                plant_location.text.toString(),
                plant_photo.text.toString()
            )
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlantEditViewModel::class.java)
        viewModel.plant.observe(viewLifecycleOwner) { item ->
            Log.v(TAG, "update items")
            plant_name.setText(item.name)
        }
        viewModel.fetching.observe(viewLifecycleOwner) { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        }
        viewModel.fetchingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.completed.observe(viewLifecycleOwner, Observer { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().navigateUp()
            }
        })
        val id = plantId
        if (id != null) {
            viewModel.loadItem(id)
        }
    }
}
