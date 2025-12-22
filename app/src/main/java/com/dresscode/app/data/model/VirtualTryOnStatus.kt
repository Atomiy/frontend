
package com.dresscode.app.data.model

data class VirtualTryOnStatus(
    val id: Long,
    val task_id: String,
    val status: String, // PENDING, SUCCEED, FAILED
    val result_url: String?
)
