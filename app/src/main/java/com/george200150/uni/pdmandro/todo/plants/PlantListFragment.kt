package com.george200150.uni.pdmandro.todo.plants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.george200150.uni.pdmandro.R
import kotlinx.android.synthetic.main.fragment_item_list.*
import com.george200150.uni.pdmandro.auth.data.AuthRepository
import com.george200150.uni.pdmandro.core.TAG

class PlantListFragment : Fragment() {
    private lateinit var plantListAdapter: PlantListAdapter
    private lateinit var itemsModel: PlantListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn) {
            findNavController().navigate(R.id.fragment_login)
            return;
        }
        setupItemList()
        fab.setOnClickListener {
            Log.v(TAG, "add new plant")
            findNavController().navigate(R.id.fragment_item_edit)
        }
    }

    private fun setupItemList() {
        plantListAdapter = PlantListAdapter(this)
        item_list.adapter = plantListAdapter
        itemsModel = ViewModelProvider(this).get(PlantListViewModel::class.java)
        itemsModel.items.observe(viewLifecycleOwner) { items ->
            Log.v(TAG, "update items")
            plantListAdapter.items = items
        }
        itemsModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }
        itemsModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
        itemsModel.loadItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}