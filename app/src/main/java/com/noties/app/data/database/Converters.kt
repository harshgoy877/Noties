package com.noties.app.data.database

import androidx.room.TypeConverter

/**
 * Type converters for Room database.
 * Currently empty but can be extended for complex data types.
 */
class Converters {

    // Example: If we need to store lists or complex objects in the future
    // @TypeConverter
    // fun fromStringList(value: List<String>): String {
    //     return value.joinToString(",")
    // }

    // @TypeConverter
    // fun toStringList(value: String): List<String> {
    //     return value.split(",").filter { it.isNotEmpty() }
    // }
}