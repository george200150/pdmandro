package com.george200150.uni.pdmandro.todo.data.remote

import retrofit2.http.*
import com.george200150.uni.pdmandro.core.Api
import com.george200150.uni.pdmandro.todo.data.Plant
import retrofit2.Response

object PlantApi {
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

        @DELETE("/api/plant/{id}")
        suspend fun delete(@Path("id") itemId: String): Response<Unit>
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}