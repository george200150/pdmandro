package com.george200150.uni.pdmandro.todo.data.local;

import androidx.lifecycle.LiveData
import androidx.room.*
import com.george200150.uni.pdmandro.todo.data.Plant

@Dao
interface PlantDao {

    @Query("SELECT * FROM plants WHERE username=:username ORDER BY name ASC")
    fun getAll(username: String): LiveData<List<Plant>>

    @Query("SELECT * from plants WHERE username=:username ORDER BY name ASC")
    fun getAllSimple(username: String): List<Plant>

    @Query("SELECT * FROM plants WHERE _id=:id")
    fun getById(id: String): LiveData<Plant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: Plant)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(plant: Plant)

    @Query("DELETE FROM plants WHERE _id=:id")
    suspend fun delete(id: String)

    @Query("DELETE FROM plants")
    suspend fun deleteAll()

    @Query("DELETE FROM plants WHERE name=:name")
    suspend fun deleteByName(name: String)
}