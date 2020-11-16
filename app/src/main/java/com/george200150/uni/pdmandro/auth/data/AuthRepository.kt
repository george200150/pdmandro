package com.george200150.uni.pdmandro.auth.data

import com.george200150.uni.pdmandro.auth.data.remote.RemoteAuthDataSource
import com.george200150.uni.pdmandro.core.Api
import com.george200150.uni.pdmandro.core.Constants
import com.george200150.uni.pdmandro.core.Result

object AuthRepository {
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        Constants.instance()?.deleteValueString("token")
        Api.tokenInterceptor.token = null
    }

    suspend fun login(username: String, password: String): Result<TokenHolder> {
        val user = User(username, password)
        val result = RemoteAuthDataSource.login(user)
        if (result is Result.Success<TokenHolder>) {
            setLoggedInUser(user, result.data)
            Constants.instance()?.storeValueString("token",result.data.token)
            Constants.instance()?.storeValueString("_id", result.data._id) // added ownership data to local storage
        }
        return result
    }

    private fun setLoggedInUser(user: User, tokenHolder: TokenHolder) {
        AuthRepository.user = user
        Api.tokenInterceptor.token = tokenHolder.token
    }
}