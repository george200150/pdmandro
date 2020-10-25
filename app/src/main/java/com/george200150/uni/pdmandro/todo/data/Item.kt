package com.george200150.uni.pdmandro.todo.data

data class Item(
    val id: String,
    var text: String
) {
    override fun toString(): String = text
}
