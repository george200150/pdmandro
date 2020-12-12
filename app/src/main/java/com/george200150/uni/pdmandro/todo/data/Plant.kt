package com.george200150.uni.pdmandro.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "hasFlowers") var hasFlowers: Boolean,
    @ColumnInfo(name = "bloomDate") var bloomDate: String,
    @ColumnInfo(name = "upToDateWithBackend") var upToDateWithBackend: Boolean?,
    @ColumnInfo(name = "backendUpdateType") var backendUpdateType: String?,
    @ColumnInfo(name = "imageURI") var imageURI: String?,
    @ColumnInfo(name = "latitude") var latitude: Float?,
    @ColumnInfo(name = "longitude") var longitude: Float?,
) {
    override fun toString(): String = name
}
