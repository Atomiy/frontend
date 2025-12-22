
package com.dresscode.app.data.local

import androidx.room.TypeConverter
import com.dresscode.app.data.model.Image
import com.dresscode.app.data.model.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // Converters for List<Image>
    @TypeConverter
    fun fromImageList(images: List<Image>?): String? {
        return gson.toJson(images)
    }

    @TypeConverter
    fun toImageList(json: String?): List<Image>? {
        val type = object : TypeToken<List<Image>>() {}.type
        return gson.fromJson(json, type)
    }

    // Converters for List<Tag>
    @TypeConverter
    fun fromTagList(tags: List<Tag>?): String? {
        return gson.toJson(tags)
    }

    @TypeConverter
    fun toTagList(json: String?): List<Tag>? {
        val type = object : TypeToken<List<Tag>>() {}.type
        return gson.fromJson(json, type)
    }
}
