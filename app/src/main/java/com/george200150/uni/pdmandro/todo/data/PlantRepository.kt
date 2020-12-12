package com.george200150.uni.pdmandro.todo.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.george200150.uni.pdmandro.MyProperties
import com.george200150.uni.pdmandro.auth.data.AuthRepository
import com.george200150.uni.pdmandro.core.Result
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.data.local.DeleteHelper
import com.george200150.uni.pdmandro.todo.data.local.PlantDao
import com.george200150.uni.pdmandro.todo.data.remote.PlantApi

class PlantRepository(val plantDao: PlantDao) {

    val plants = MediatorLiveData<List<Plant>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                val plantApi = PlantApi.service.find()
                plants.value = plantApi
                for (plant in plantApi) {
                    plant.username = AuthRepository.getUsername()
                    plantDao.insert(plant)
                }
                return Result.Success(true)
            } else {
                plants.addSource(plantDao.getAll(AuthRepository.getUsername())) {
                    plants.value = it
                }
                return Result.Success(true)
            }
        } catch (e: Exception) {
            plants.addSource(plantDao.getAll(AuthRepository.getUsername())) {
                plants.value = it
            }
            return Result.Error(e)
        }
    }

    fun getById(itemId: String): LiveData<Plant> {
        return plantDao.getById(itemId)
    }

    suspend fun save(movie: Plant): Result<Plant> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                movie.upToDateWithBackend = true
                movie.backendUpdateType = ""
                val createdMovie = PlantApi.service.create(movie)
                plantDao.insert(createdMovie)
                return Result.Success(createdMovie)
            } else {
                movie.upToDateWithBackend = false
                movie.backendUpdateType = "save"
                MyProperties.instance.snackbarMessage.postValue("The save won't be sent to the server until you have an active internet connection")
                Log.d(TAG, "save: no internet connection (manual check)")
                plantDao.insert(movie)
                return Result.Success(movie)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(movie: Plant): Result<Plant> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                movie.upToDateWithBackend = true
                movie.backendUpdateType = ""
                val updatedMovie = PlantApi.service.update(movie._id, movie)
                plantDao.update(updatedMovie)
                return Result.Success(updatedMovie)
            } else {
                movie.upToDateWithBackend = false
                movie.backendUpdateType = "update"
                MyProperties.instance.snackbarMessage.postValue("The update won't be sent to the server until you have an active internet connection")
                Log.d(TAG, "update: no internet connection (manual check)")
                plantDao.update(movie)
                return Result.Success(movie)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(movieId: String): Result<Boolean> {
        try {
            if (MyProperties.instance.internetActive.value == 1) {
                PlantApi.service.delete(movieId)
                plantDao.delete(movieId)
                return Result.Success(true)
            } else {
                MyProperties.instance.snackbarMessage.postValue("The delete won't be sent to the server until you have an active internet connection")
                Log.d(TAG, "delete: no internet connection (manual check)")
                plantDao.delete(movieId)
                DeleteHelper.addDelete(movieId)
                return Result.Success(true)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}