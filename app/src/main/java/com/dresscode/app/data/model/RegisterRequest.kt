
package com.dresscode.app.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val gender: String,
    val avatar: String? = null
)
