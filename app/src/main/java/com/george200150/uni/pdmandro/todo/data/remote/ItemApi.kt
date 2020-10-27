package com.george200150.uni.pdmandro.todo.data.remote

import retrofit2.http.*
import com.george200150.uni.pdmandro.core.Api
import com.george200150.uni.pdmandro.todo.data.Plant


object ItemApi {
    interface Service {
        @GET("/api/plant")
        suspend fun find(): List<Plant>

        @GET("/api/plant/{id}")
        suspend fun read(@Path("id") itemId: String): Plant;

        @Headers("Content-Type: application/json")
        @POST("/api/plant")
        suspend fun create(@Body plant: Plant): Plant

        @Headers("Content-Type: application/json")
        @PUT("/api/plant/{id}")
        suspend fun update(@Path("id") itemId: String, @Body plant: Plant): Plant
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}