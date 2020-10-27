package com.george200150.uni.pdmandro.todo.data

import com.george200150.uni.pdmandro.core.Result
import com.george200150.uni.pdmandro.todo.data.remote.ItemApi

object PlantRepository {
    private var cachedPlants: MutableList<Plant>? = null;

    suspend fun loadAll(): Result<List<Plant>> {
        if (cachedPlants != null) {
            return Result.Success(cachedPlants as List<Plant>);
        }
        try {
            val items = ItemApi.service.find()
            cachedPlants = mutableListOf()
            cachedPlants?.addAll(items)
            return Result.Success(cachedPlants as List<Plant>)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun load(itemId: String): Result<Plant> {
        val item = cachedPlants?.find { it._id == itemId }
        if (item != null) {
            return Result.Success(item)
        }
        try {
            return Result.Success(ItemApi.service.read(itemId))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun save(plant: Plant): Result<Plant> {
        try {
            val createdItem = ItemApi.service.create(plant)
            cachedPlants?.add(createdItem)
            return Result.Success(createdItem)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(plant: Plant): Result<Plant> {
        try {
            val updatedItem = ItemApi.service.update(plant._id, plant)
            val index = cachedPlants?.indexOfFirst { it._id == plant._id }
            if (index != null) {
                cachedPlants?.set(index, updatedItem)
            }
            return Result.Success(updatedItem)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}