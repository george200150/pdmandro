package com.george200150.uni.pdmandro.todo.plants

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

class PlantListViewModel : ViewModel() {
    private val mutableItems = MutableLiveData<List<Plant>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Plant>> = mutableItems
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    fun loadItems() {
        viewModelScope.launch {
            Log.v(TAG, "loadItems...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = PlantRepository.loadAll()) {
                is Result.Success -> {
                    Log.d(TAG, "loadItems succeeded");
                    mutableItems.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "loadItems failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}
