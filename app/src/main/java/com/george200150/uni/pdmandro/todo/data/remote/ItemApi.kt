package com.george200150.uni.pdmandro.todo.data.remote

import retrofit2.http.*
import com.george200150.uni.pdmandro.core.Api
import com.george200150.uni.pdmandro.todo.data.Item


object ItemApi {
    interface Service {
        @GET("/api/plant")
        suspend fun find(): List<Item>

        @GET("/api/plant/{id}")
        suspend fun read(@Path("id") itemId: String): Item;

        @Headers("Content-Type: application/json")
        @POST("/api/plant")
        suspend fun create(@Body item: Item): Item

        @Headers("Content-Type: application/json")
        @PUT("/api/plant/{id}")
        suspend fun update(@Path("id") itemId: String, @Body item: Item): Item
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}