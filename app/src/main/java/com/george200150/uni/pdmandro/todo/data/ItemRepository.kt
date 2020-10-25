package com.george200150.uni.pdmandro.todo.data

import com.george200150.uni.pdmandro.core.Result
import com.george200150.uni.pdmandro.todo.data.remote.ItemApi

object ItemRepository {
    private var cachedItems: MutableList<Item>? = null;

    suspend fun loadAll(): Result<List<Item>> {
        if (cachedItems != null) {
            return Result.Success(cachedItems as List<Item>);
        }
        try {
            val items = ItemApi.service.find()
            cachedItems = mutableListOf()
            cachedItems?.addAll(items)
            return Result.Success(cachedItems as List<Item>)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun load(itemId: String): Result<Item> {
        val item = cachedItems?.find { it._id == itemId }
        if (item != null) {
            return Result.Success(item)
        }
        try {
            return Result.Success(ItemApi.service.read(itemId))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun save(item: Item): Result<Item> {
        try {
            val createdItem = ItemApi.service.create(item)
            cachedItems?.add(createdItem)
            return Result.Success(createdItem)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(item: Item): Result<Item> {
        try {
            val updatedItem = ItemApi.service.update(item._id, item)
            val index = cachedItems?.indexOfFirst { it._id == item._id }
            if (index != null) {
                cachedItems?.set(index, updatedItem)
            }
            return Result.Success(updatedItem)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}