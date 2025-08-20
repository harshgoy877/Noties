package com.noties.app.data.repository

import com.noties.app.data.dao.NoteDao
import com.noties.app.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Note operations.
 */
class NotesRepository(
    private val noteDao: NoteDao
) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes()

    suspend fun getNoteById(noteId: Long): Note? =
        noteDao.getNoteById(noteId)

    fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotes(query)

    fun getNotesByColor(colorTag: String): Flow<List<Note>> =
        noteDao.getNotesByColor(colorTag)

    suspend fun createNote(
        title: String,
        content: String,
        colorTag: String
    ): Long {
        val now = System.currentTimeMillis()
        return noteDao.insertNote(
            Note(
                title = title,
                content = content,
                colorTag = colorTag,
                createdAt = now,
                modifiedAt = now
            )
        )
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.copy(modifiedAt = System.currentTimeMillis()))
    }

    suspend fun updateNoteContent(
        noteId: Long,
        title: String,
        content: String
    ) {
        noteDao.updateNoteContent(
            noteId,
            title,
            content,
            System.currentTimeMillis()
        )
    }

    suspend fun updateNoteColor(
        noteId: Long,
        colorTag: String
    ) {
        noteDao.updateNoteColor(
            noteId,
            colorTag,
            System.currentTimeMillis()
        )
    }

    suspend fun updateNoteFontSettings(
        noteId: Long,
        fontSize: Int,
        fontStyle: String
    ) {
        noteDao.updateNoteFontSettings(
            noteId,
            fontSize,
            fontStyle,
            System.currentTimeMillis()
        )
    }

    suspend fun toggleArchiveStatus(
        noteId: Long,
        isArchived: Boolean
    ) {
        noteDao.updateArchiveStatus(
            noteId,
            isArchived,
            System.currentTimeMillis()
        )
    }

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun getNotesCount(): Int = noteDao.getNotesCount()

    suspend fun getArchivedNotesCount(): Int =
        noteDao.getArchivedNotesCount()

    suspend fun deleteAllArchivedNotes() =
        noteDao.deleteAllArchivedNotes()
}
