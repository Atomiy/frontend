
package com.dresscode.app.data.model

data class CreatePostRequest(
    val title: String,
    val content: String,
    val style: String,
    val season: String,
    val scene: String,
    val images: List<String>,
    val tags: List<String>
)
