package com.george200150.uni.pdmandro.todo.plant

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.george200150.uni.pdmandro.core.Result
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.data.Plant
import com.george200150.uni.pdmandro.todo.data.PlantRepository
import com.george200150.uni.pdmandro.todo.data.local.PlantDatabase


class PlantEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val plantRepository: PlantRepository

    init {
        val plantDao = PlantDatabase.getDatabase(application, viewModelScope).plantDao()
        plantRepository = PlantRepository(plantDao)
    }

    fun getPlantById(itemId: String): LiveData<Plant> {
        Log.v(TAG, "getPlantById...")
        return plantRepository.getById(itemId)
    }


    fun saveOrUpdatePlant(plant: Plant) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateItem...")
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Plant>
            if (plant._id.isNotEmpty()) {
                result = plantRepository.update(plant)
            } else {
                result = plantRepository.save(plant)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdatePlant succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdatePlant failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun deletePlant(itemId: String)
    {
        viewModelScope.launch {
//            mutableFetching.value = true
//            mutableException.value = null
            val result: Result<Boolean> = plantRepository.delete(itemId)
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "delete succeeded");
                    //mutableItem.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "delete failed", result.exception);
                    mutableException.value = result.exception
                }
            }
//            mutableCompleted.value = true
//            mutableFetching.value = false
        }
    }
}
