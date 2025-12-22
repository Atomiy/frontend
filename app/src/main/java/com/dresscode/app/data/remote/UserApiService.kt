
package com.dresscode.app.data.remote

import com.dresscode.app.data.model.ApiResponse
import com.dresscode.app.data.model.User
import com.dresscode.app.data.model.UpdateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApiService {

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<User>>

    @PUT("users/me") // Assuming this is the endpoint for updating user info
    suspend fun updateCurrentUser(@Body request: UpdateUserRequest): Response<ApiResponse<User>>
}
