package com.noties.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noties.app.data.model.Note
import com.noties.app.data.repository.NotesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Add this if you see an "opt-in" warning on .stateIn/.flatMapLatest:
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NotesViewModel(
    private val repository: NotesRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // View mode (list or grid)
    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    // Filter by color
    private val _selectedColorFilter = MutableStateFlow<String?>(null)
    val selectedColorFilter: StateFlow<String?> = _selectedColorFilter.asStateFlow()

    // Notes flow based on current filters
    val notes: StateFlow<List<Note>> = combine(
        _searchQuery,
        _selectedColorFilter
    ) { query, colorFilter ->
        when {
            query.isNotEmpty() -> repository.searchNotes(query)
            colorFilter != null -> repository.getNotesByColor(colorFilter)
            else -> repository.getAllNotes()
        }
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Archived notes
    val archivedNotes: StateFlow<List<Note>> = repository.getArchivedNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadNotesStats()
    }

    fun createNote(title: String, content: String, colorTag: String = "blue") {
        viewModelScope.launch {
            try {
                repository.createNote(title, content, colorTag)
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to create note: ${e.message}")
                }
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.updateNote(note)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update note: ${e.message}")
                }
            }
        }
    }

    fun updateNoteContent(noteId: Long, title: String, content: String) {
        viewModelScope.launch {
            try {
                repository.updateNoteContent(noteId, title, content)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to save note: ${e.message}")
                }
            }
        }
    }

    fun updateNoteColor(noteId: Long, colorTag: String) {
        viewModelScope.launch {
            try {
                repository.updateNoteColor(noteId, colorTag)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update note color: ${e.message}")
                }
            }
        }
    }

    fun toggleArchiveStatus(noteId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleArchiveStatus(noteId, isArchived)
                loadNotesStats()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to archive note: ${e.message}")
                }
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.deleteNote(note)
                loadNotesStats()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to delete note: ${e.message}")
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun toggleViewMode() {
        _viewMode.value = when (_viewMode.value) {
            ViewMode.LIST -> ViewMode.GRID
            ViewMode.GRID -> ViewMode.LIST
        }
    }

    fun setColorFilter(colorTag: String?) {
        _selectedColorFilter.value = colorTag
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedColorFilter.value = null
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadNotesStats() {
        viewModelScope.launch {
            try {
                val notesCount = repository.getNotesCount()
                val archivedCount = repository.getArchivedNotesCount()
                _uiState.update {
                    it.copy(
                        notesCount = notesCount,
                        archivedCount = archivedCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
        }
    }
}

data class NotesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val notesCount: Int = 0,
    val archivedCount: Int = 0
)

enum class ViewMode {
    LIST, GRID
}
