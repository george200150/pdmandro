package com.george200150.uni.pdmandro.auth.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.george200150.uni.pdmandro.auth.data.TokenHolder
import com.george200150.uni.pdmandro.auth.data.User
import com.george200150.uni.pdmandro.core.Api
import com.george200150.uni.pdmandro.core.Result

object RemoteAuthDataSource {
    interface AuthService {
        @Headers("Content-Type: application/json")
        @POST("/api/auth/login")
        suspend fun login(@Body user: User): TokenHolder
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<TokenHolder> {
        return try {
            Result.Success(authService.login(user))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
