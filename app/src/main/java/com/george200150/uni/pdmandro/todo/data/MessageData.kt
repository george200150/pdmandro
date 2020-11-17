package com.george200150.uni.pdmandro.todo.data

data class MessageData(var event: String, var payload: PlantJson) {
    data class PlantJson(var plant: Plant)
}