package com.george200150.uni.pdmandro.todo.data

data class Item(
    val _id: String,
    var name: String,
    var hasFlowers: Boolean,
    var bloomDate: String,
    var location: String,
    var photo: String
) {
    override fun toString(): String = name
}
