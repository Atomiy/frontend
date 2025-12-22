
package com.dresscode.app.data.remote

import com.dresscode.app.data.model.ApiResponse
import com.dresscode.app.data.model.LoginRequest
import com.dresscode.app.data.model.LoginResponse
import com.dresscode.app.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Unit>>
}
