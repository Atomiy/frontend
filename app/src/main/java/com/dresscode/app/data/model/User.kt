
package com.dresscode.app.data.model

data class User(
    val id: Long,
    val username: String,
    val nickname: String,
    val gender: String,
    val avatar: String?,
    val created_at: String?, // Added to match backend response
    val updated_at: String? // Added to match backend response
)
