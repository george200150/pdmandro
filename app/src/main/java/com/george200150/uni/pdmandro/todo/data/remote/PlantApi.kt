package com.george200150.uni.pdmandro.todo.data.remote

import android.util.Log
import retrofit2.http.*
import com.george200150.uni.pdmandro.core.Api
import com.george200150.uni.pdmandro.core.Constants
import com.george200150.uni.pdmandro.todo.data.Plant
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import retrofit2.Response

object PlantApi {

    private const val WSURL = "ws://192.168.1.5:3000" // "ws://192.168.0.150:3000"

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

    object RemoteDataSource {
        val eventChannel = Channel<String>()

        init {
            val request = Request.Builder().url(WSURL).build()
            OkHttpClient().newWebSocket(request, MyWebSocketListener()).request()
        }

        private class MyWebSocketListener : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "onOpen")
                val token = Constants.instance()?.fetchValueString("token")
                val json =
                    "{\"type\":\"authorization\",\"payload\":{\"token\":\"$token\"}}"
                    //"{\"type\":\"authorization\",\"payload\":{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImIiLCJfaWQiOiJ2STFHTWt6QnphNWk4Mnp6IiwiaWF0IjoxNjA1NjI5MDU4LCJleHAiOjE2MDU4NDUwNTh9.GepIJPYh_qR-5nRNIULd--7cT5tdfhJhmzSQKTApVzA\"}}"
                Log.d("json", json)
                webSocket.send(json)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "onMessage$text")
                runBlocking { eventChannel.send(text) }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("WebSocket", "onMessage bytes")
                output("Receiving bytes : " + bytes.hex())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "onFailure", t)
                t.printStackTrace()
            }

            private fun output(txt: String) {
                Log.d("WebSocket", txt)
            }
        }
    }

}