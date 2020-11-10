package com.george200150.uni.pdmandro.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "hasFlowers") var hasFlowers: Boolean,
    @ColumnInfo(name = "bloomDate") var bloomDate: String,
    @ColumnInfo(name = "location") var location: String,
    @ColumnInfo(name = "photo") var photo: String
) {
    override fun toString(): String = name
}
