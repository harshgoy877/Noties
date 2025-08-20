package com.noties.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a note in the database.
 * Follows the exact schema specified in the requirements.
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val colorTag: String,
    val createdAt: Long,
    val modifiedAt: Long,
    val isArchived: Boolean = false,
    val fontSize: Int = 16,
    val fontStyle: String = "regular"
) {
    companion object {
        // Predefined color tags for note categorization
        val COLOR_TAGS = listOf(
            "blue",
            "green",
            "yellow",
            "orange",
            "red",
            "purple",
            "teal",
            "pink"
        )

        // Font styles
        val FONT_STYLES = listOf(
            "regular",
            "bold",
            "italic",
            "bold_italic"
        )
    }
}