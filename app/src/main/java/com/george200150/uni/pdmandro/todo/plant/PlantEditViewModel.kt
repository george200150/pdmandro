package com.george200150.uni.pdmandro.todo.plant

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.george200150.uni.pdmandro.core.Result
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.data.Plant
import com.george200150.uni.pdmandro.todo.data.PlantRepository

class PlantEditViewModel : ViewModel() {
    private val mutableItem = MutableLiveData<Plant>().apply { value = Plant("", "", hasFlowers = false, bloomDate = "", location = "", photo = "") }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val plant: LiveData<Plant> = mutableItem
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            Log.v(TAG, "loadItem...")
            mutableFetching.value = true
            mutableException.value = null
            when (val result = PlantRepository.load(itemId)) {
                is Result.Success -> {
                    Log.d(TAG, "loadItem succeeded")
                    mutableItem.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "loadItem failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableFetching.value = false
        }
    }

    fun saveOrUpdateItem(name: String, hasFlowers: Boolean, bloomDate: String, location: String, photo: String) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateItem...")
            val item = mutableItem.value ?: return@launch
            item.name = name // might generate empty IDs
            item.hasFlowers=hasFlowers
            item.bloomDate=bloomDate
            item.location=location
            item.photo=photo

            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Plant>
            if (item._id.isNotEmpty()) {
                result = PlantRepository.update(item)
            } else {
                result = PlantRepository.save(item)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateItem succeeded")
                    mutableItem.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdateItem failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}
