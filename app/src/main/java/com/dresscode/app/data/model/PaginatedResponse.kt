
package com.dresscode.app.data.model

data class PaginatedResponse<T>(
    val total: Long,
    val page: Int,
    val page_size: Int,
    val data: List<T>
)
