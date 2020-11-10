package com.george200150.uni.pdmandro.todo.data

import com.george200150.uni.pdmandro.core.Result
import androidx.lifecycle.LiveData
import com.george200150.uni.pdmandro.todo.data.local.PlantDao
import com.george200150.uni.pdmandro.todo.data.remote.PlantApi

class PlantRepository(private val plantDao: PlantDao) {
    val items = plantDao.getAll()

    suspend fun refresh(): Result<Boolean> {
        try {
            val items = PlantApi.service.find()
            for (item in items) {
                plantDao.insert(item)
            }
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun getById(itemId: String): LiveData<Plant> {
        return plantDao.getById(itemId)
    }

    suspend fun save(item: Plant): Result<Plant> {
        try {
            val createdItem = PlantApi.service.create(item)
            plantDao.insert(createdItem)
            return Result.Success(createdItem)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(item: Plant): Result<Plant> {
        try {
            val updatedItem = PlantApi.service.update(item._id, item)
            plantDao.update(updatedItem)
            return Result.Success(updatedItem)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(itemId: String): Result<Boolean> {
        try {

            PlantApi.service.delete(itemId)
            plantDao.delete(id = itemId)
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}