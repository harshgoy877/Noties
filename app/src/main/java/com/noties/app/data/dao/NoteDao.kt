package com.noties.app.data.dao

import androidx.room.*
import com.noties.app.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Note entities.
 * Provides all database operations for notes.
 */
@Dao
interface NoteDao {

    /**
     * Get all notes ordered by modification date (newest first)
     */
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY modifiedAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    /**
     * Get all archived notes
     */
    @Query("SELECT * FROM notes WHERE isArchived = 1 ORDER BY modifiedAt DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    /**
     * Get a specific note by ID
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note?

    /**
     * Search notes by title and content
     */
    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') AND isArchived = 0 ORDER BY modifiedAt DESC")
    fun searchNotes(query: String): Flow<List<Note>>

    /**
     * Get notes by color tag
     */
    @Query("SELECT * FROM notes WHERE colorTag = :colorTag AND isArchived = 0 ORDER BY modifiedAt DESC")
    fun getNotesByColor(colorTag: String): Flow<List<Note>>

    /**
     * Insert a new note
     */
    @Insert
    suspend fun insertNote(note: Note): Long

    /**
     * Update an existing note
     */
    @Update
    suspend fun updateNote(note: Note)

    /**
     * Delete a note
     */
    @Delete
    suspend fun deleteNote(note: Note)

    /**
     * Archive/unarchive a note
     */
    @Query("UPDATE notes SET isArchived = :isArchived, modifiedAt = :modifiedAt WHERE id = :noteId")
    suspend fun updateArchiveStatus(noteId: Long, isArchived: Boolean, modifiedAt: Long)

    /**
     * Update note content and modification time
     */
    @Query("UPDATE notes SET title = :title, content = :content, modifiedAt = :modifiedAt WHERE id = :noteId")
    suspend fun updateNoteContent(noteId: Long, title: String, content: String, modifiedAt: Long)

    /**
     * Update note color tag
     */
    @Query("UPDATE notes SET colorTag = :colorTag, modifiedAt = :modifiedAt WHERE id = :noteId")
    suspend fun updateNoteColor(noteId: Long, colorTag: String, modifiedAt: Long)

    /**
     * Update note font settings
     */
    @Query("UPDATE notes SET fontSize = :fontSize, fontStyle = :fontStyle, modifiedAt = :modifiedAt WHERE id = :noteId")
    suspend fun updateNoteFontSettings(noteId: Long, fontSize: Int, fontStyle: String, modifiedAt: Long)

    /**
     * Get total count of notes (excluding archived)
     */
    @Query("SELECT COUNT(*) FROM notes WHERE isArchived = 0")
    suspend fun getNotesCount(): Int

    /**
     * Get total count of archived notes
     */
    @Query("SELECT COUNT(*) FROM notes WHERE isArchived = 1")
    suspend fun getArchivedNotesCount(): Int

    /**
     * Delete all archived notes (for cleanup)
     */
    @Query("DELETE FROM notes WHERE isArchived = 1")
    suspend fun deleteAllArchivedNotes()
}
