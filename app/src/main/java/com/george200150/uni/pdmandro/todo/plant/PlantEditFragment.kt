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
import com.george200150.uni.pdmandro.todo.data.Plant
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.time.LocalDate
import java.time.LocalDateTime

class PlantEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "_ID"
    }

    private lateinit var viewModel: PlantEditViewModel
    private var plantId: String? = null
    private var plant: Plant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                plantId = it.getString(ITEM_ID).toString()
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
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save item")
            val i = plant
            if (i != null) {
                i.name = plant_name.text.toString()
                i.hasFlowers = plant_has_flowers.isChecked
                i.location = plant_location.text.toString();
                i.photo = plant_photo.text.toString();
                val day: Int = bloom_date.dayOfMonth
                val month: Int = bloom_date.month + 1
                val year: Int = bloom_date.year
                val date = LocalDate.of(year, month, day)
                i.bloomDate = date.toString()
                viewModel.saveOrUpdateItem(i)

            }
        }
        button_delete.setOnClickListener {
            if (plant != null) {
                viewModel.deleteItem(plant!!._id)
            }

        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlantEditViewModel::class.java)
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
        viewModel.completed.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        }
        val id = plantId
        if (id == null) {
            plant = Plant("", "", "", false, "", "", "")
        } else {
            viewModel.getItemById(id).observe(viewLifecycleOwner) {
                Log.v(TAG, "update items")
                if (it != null) {
                    plant = it
                    plant_name.setText(plant!!.name)
                    plant_location.setText(plant!!.location)
                    plant_has_flowers.isChecked = plant!!.hasFlowers
                    if (plant!!.bloomDate.isNotEmpty()) {
                        val date = LocalDate.parse(plant!!.bloomDate);
                        bloom_date.updateDate(date.year, date.monthValue, date.dayOfMonth)
                    }
                }
            }
        }
    }
}
