
package com.dresscode.app.data.repository

import com.dresscode.app.data.model.LoginRequest
import com.dresscode.app.data.model.RegisterRequest
import com.dresscode.app.data.remote.AuthApiService
import com.dresscode.app.data.remote.RetrofitClient

class AuthRepository {

    private val authService: AuthApiService by lazy {
        RetrofitClient.instance.create(AuthApiService::class.java)
    }

    suspend fun login(loginRequest: LoginRequest) = authService.login(loginRequest)

    suspend fun register(registerRequest: RegisterRequest) = authService.register(registerRequest)
}
