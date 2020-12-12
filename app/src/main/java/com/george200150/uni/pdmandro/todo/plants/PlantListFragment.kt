package com.george200150.uni.pdmandro.todo.plants

import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.george200150.uni.pdmandro.MyProperties
import com.george200150.uni.pdmandro.R
import com.george200150.uni.pdmandro.auth.data.AuthRepository
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.RepoWorker
import com.george200150.uni.pdmandro.todo.data.PlantRepoHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_item_list.*

class PlantListFragment : Fragment() {
    private lateinit var plantListAdapter: PlantListAdapter
    private lateinit var viewModel: PlantListViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PlantRepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
        observeInternetConnection()
    }

    private fun observeInternetConnection(){
        MyProperties.instance.internetActive.observe(
            viewLifecycleOwner, Observer {
                if (it == 1) {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Internet connection active",
                        Snackbar.LENGTH_LONG
                    )
                        .setActionTextColor(Color.RED)
                        .show()

                    updatePlantsOnServer()
                } else {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "No internet connection",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setActionTextColor(Color.RED)
                        .show()
                }
            }
        )
    }

    private fun updatePlantsOnServer() {
        // delete
        val dataParam = Data.Builder().putString("operation", "delete")
        val request = OneTimeWorkRequestBuilder<RepoWorker>()
            .setInputData(dataParam.build())
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)

        // save & update
        val plants = viewModel.plantRepository.plantDao.getAllSimple(AuthRepository.getUsername())
        plants.forEach { plant ->
            if (plant.upToDateWithBackend == null) {
                plant.upToDateWithBackend = true
            }
            if (!plant.upToDateWithBackend!!) {
                if (plant.backendUpdateType == "save") { // save
                    PlantRepoHelper.setPlant(plant)
                    val dataParam = Data.Builder().putString("operation", "save")
                    val request = OneTimeWorkRequestBuilder<RepoWorker>()
                        .setInputData(dataParam.build())
                        .build()
                    WorkManager.getInstance(requireContext()).enqueue(request)
                } else if (plant.backendUpdateType == "update") { // update
                    PlantRepoHelper.setPlant(plant)
                    val dataParam = Data.Builder().putString("operation", "update")
                    val request = OneTimeWorkRequestBuilder<RepoWorker>()
                        .setInputData(dataParam.build())
                        .build()
                    WorkManager.getInstance(requireContext()).enqueue(request)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn(requireContext())) {
            Log.d(TAG, "is not logged in")
            findNavController().navigate(R.id.fragment_login)
            return
        }
        setupPlantList()

        fab.setOnClickListener {
            Log.v(TAG, "add new item")
            findNavController().navigate(R.id.fragment_item_edit)
        }

        log_out_button.setOnClickListener {
            Log.v(TAG, "log out")
            AuthRepository.logout()
            findNavController().navigate(R.id.fragment_login)
        }
    }

    private fun setupPlantList() {
        plantListAdapter = PlantListAdapter(this)
        item_list.adapter = plantListAdapter
        viewModel = ViewModelProvider(this).get(PlantListViewModel::class.java)
        viewModel.plants.observe(viewLifecycleOwner, { plant ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${plant.size}")
            plantListAdapter.plants = plant
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        viewModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.refresh()

        search.doOnTextChanged { _, _, _, _ ->
            viewModel.plants.observe(viewLifecycleOwner, { plant ->
                plantListAdapter.plants = plant
                var watchedFilter = "";
                if (hasFlowers.isChecked) watchedFilter = "true"
                if (noFlowers.isChecked) watchedFilter = "false"
                plantListAdapter.plants =
                    plantListAdapter.searchAndFilter(search.text.toString(), watchedFilter)
                plantListAdapter.notifyDataSetChanged()
            })
        }

        hasFlowers.setOnClickListener {
            viewModel.plants.observe(viewLifecycleOwner, { plant ->
                plantListAdapter.plants = plant
                if (hasFlowers.isChecked) {
                    noFlowers.isChecked = false
                    plantListAdapter.plants =
                        plantListAdapter.searchAndFilter(search.text.toString(), "true")
                    plantListAdapter.notifyDataSetChanged()
                } else {
                    plantListAdapter.plants =
                        plantListAdapter.searchAndFilter(search.text.toString(), "")
                    plantListAdapter.notifyDataSetChanged()
                }
            })
        }

        noFlowers.setOnClickListener {
            viewModel.plants.observe(viewLifecycleOwner, { plant ->
                plantListAdapter.plants = plant
                if (noFlowers.isChecked) {
                    hasFlowers.isChecked = false
                    plantListAdapter.plants =
                        plantListAdapter.searchAndFilter(search.text.toString(), "false")
                    plantListAdapter.notifyDataSetChanged()
                } else {
                    plantListAdapter.plants =
                        plantListAdapter.searchAndFilter(search.text.toString(), "")
                    plantListAdapter.notifyDataSetChanged()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}