
package com.dresscode.app.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dresscode.app.data.local.Converters

@Entity(tableName = "posts")
@TypeConverters(Converters::class)
data class Post(
    @PrimaryKey
    val id: Long,
    val title: String,
    val content: String,
    val style: String,
    val season: String,
    val scene: String,
    val images: List<Image>,
    val tags: List<Tag>,
    @Embedded(prefix = "author_")
    val author: User,
    var isFavorited: Boolean = false
)
