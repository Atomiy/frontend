
package com.dresscode.app.data.repository

import com.dresscode.app.data.model.UpdateUserRequest
import com.dresscode.app.data.remote.RetrofitClient
import com.dresscode.app.data.remote.UserApiService

class UserRepository {

    private val userService: UserApiService by lazy {
        RetrofitClient.instance.create(UserApiService::class.java)
    }

    suspend fun getCurrentUser() = userService.getCurrentUser()

    suspend fun updateUser(updateRequest: UpdateUserRequest) = userService.updateCurrentUser(updateRequest)
}
