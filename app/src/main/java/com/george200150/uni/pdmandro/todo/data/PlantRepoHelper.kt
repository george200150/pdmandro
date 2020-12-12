package com.george200150.uni.pdmandro.todo.data

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.george200150.uni.pdmandro.MyProperties
import com.george200150.uni.pdmandro.core.Result
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.data.remote.PlantApi
import kotlinx.coroutines.launch

object PlantRepoHelper {
    var plantRepository: PlantRepository? = null
    private var plant: Plant? = null
    private var viewLifecycleOwner: LifecycleOwner? = null
    private var plantToBeDeletedId: String? = null

    fun setPlantRepo(plantParam: PlantRepository) {
        this.plantRepository = plantParam
    }

    fun setPlant(plantParam: Plant) {
        this.plant = plantParam
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }

    fun setPlantToBeDeletedId(id: String) {
        plantToBeDeletedId = id
    }

    fun saveNewVersion() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            saveNewVersionHelper()
        }
    }

    private suspend fun saveNewVersionHelper(): Result<Plant> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                Log.d(TAG, "saveNewVersionHelper")
                plant!!.upToDateWithBackend = true
                plant!!.backendUpdateType = ""
                val createdPlant = PlantApi.service.create(plant!!)
                plantRepository!!.plantDao.deleteByName(createdPlant.name)
                plantRepository!!.plantDao.insert(createdPlant)
                MyProperties.instance.snackbarMessage.postValue("The save was registered on the server")
                return Result.Success(createdPlant)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun updateNewVersion() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            updateNewVersionHelper()
        }
    }

    private suspend fun updateNewVersionHelper(): Result<Plant> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                Log.d(TAG, "updateNewVersionHelper")
                plant!!.upToDateWithBackend = true
                plant!!.backendUpdateType = ""
                val updatedPlant = PlantApi.service.update(plant!!._id, plant!!)
                plantRepository!!.plantDao.update(updatedPlant)
                MyProperties.instance.snackbarMessage.postValue("The update was registered on the server")
                return Result.Success(updatedPlant)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun deletePlant(){
        viewLifecycleOwner!!.lifecycleScope.launch {
            deletePlantHelper()
        }
    }

    private suspend fun deletePlantHelper(){
        Log.d(TAG, "deletePlantHelper")
        plantRepository!!.delete(plantToBeDeletedId!!)
        MyProperties.instance.snackbarMessage.postValue("The delete was registered on the server")
    }
}