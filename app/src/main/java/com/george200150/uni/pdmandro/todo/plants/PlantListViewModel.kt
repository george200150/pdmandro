package com.george200150.uni.pdmandro.todo.plants

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.george200150.uni.pdmandro.todo.data.Plant
import com.george200150.uni.pdmandro.todo.data.local.PlantDatabase
import com.george200150.uni.pdmandro.todo.data.PlantRepository
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.core.Result
import kotlinx.coroutines.launch

class PlantListViewModel (application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Plant>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    private val plantRepository: PlantRepository
    init {
        val plantDao = PlantDatabase.getDatabase(application,viewModelScope).plantDao()
        plantRepository = PlantRepository(plantDao)
        items = plantRepository.plants
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...")
            mutableLoading.value = true
            mutableException.value = null
            when (val result = plantRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded")
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}
