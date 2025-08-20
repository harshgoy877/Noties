package com.noties.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noties.app.data.model.Note
import com.noties.app.ui.components.NotesTopBar
import com.noties.app.ui.components.SearchBar
import com.noties.app.ui.components.NoteCard
import com.noties.app.ui.theme.NotiesTheme
import com.noties.app.ui.theme.NotiesShapes
import com.noties.app.ui.viewmodel.NotesViewModel
import com.noties.app.ui.viewmodel.ViewMode
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home screen displaying all notes in grid or list format.
 * Features search, color filtering, and quick actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NotesViewModel,
    onNavigateToNote: (Long) -> Unit,
    onNavigateToNewNote: () -> Unit,
    onNavigateToArchived: () -> Unit
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val selectedColorFilter by viewModel.selectedColorFilter.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showColorFilter by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NotesTopBar(
                title = "Noties",
                notesCount = uiState.notesCount,
                onSettingsClick = { /* TODO: Implement settings */ },
                onArchiveClick = onNavigateToArchived
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewNote,
                shape = NotiesShapes.FloatingActionButton,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onClearSearch = viewModel::clearSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Filter and View Mode Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Color Filter Button
                    FilterChip(
                        onClick = { showColorFilter = !showColorFilter },
                        label = {
                            Text(
                                text = selectedColorFilter?.replaceFirstChar {
                                    it.uppercase()
                                } ?: "All Colors"
                            )
                        },
                        selected = selectedColorFilter != null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Color Filter"
                            )
                        }
                    )

                    // Clear Filters Button (if any filters active)
                    if (selectedColorFilter != null || searchQuery.isNotEmpty()) {
                        FilterChip(
                            onClick = viewModel::clearFilters,
                            label = { Text("Clear") },
                            selected = false,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Filters"
                                )
                            }
                        )
                    }
                }

                // View Mode Toggle
                IconButton(
                    onClick = viewModel::toggleViewMode
                ) {
                    Icon(
                        imageVector = when (viewMode) {
                            ViewMode.GRID -> Icons.Default.ViewList
                            ViewMode.LIST -> Icons.Default.GridView
                        },
                        contentDescription = "Toggle View Mode"
                    )
                }
            }

            // Color Filter Row
            if (showColorFilter) {
                ColorFilterRow(
                    selectedColor = selectedColorFilter,
                    onColorSelected = { color ->
                        viewModel.setColorFilter(color)
                        showColorFilter = false
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Notes Grid/List
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                notes.isEmpty() -> {
                    EmptyNotesState(
                        searchQuery = searchQuery,
                        selectedColorFilter = selectedColorFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    when (viewMode) {
                        ViewMode.GRID -> {
                            NotesGrid(
                                notes = notes,
                                onNoteClick = onNavigateToNote,
                                onArchiveNote = { noteId ->
                                    viewModel.toggleArchiveStatus(noteId, true)
                                },
                                onDeleteNote = { note ->
                                    viewModel.deleteNote(note)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        ViewMode.LIST -> {
                            NotesList(
                                notes = notes,
                                onNoteClick = onNavigateToNote,
                                onArchiveNote = { noteId ->
                                    viewModel.toggleArchiveStatus(noteId, true)
                                },
                                onDeleteNote = { note ->
                                    viewModel.deleteNote(note)
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar or dialog
            viewModel.clearError()
        }
    }
}

@Composable
private fun ColorFilterRow(
    selectedColor: String?,
    onColorSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // "All" option
            FilterChip(
                onClick = { onColorSelected(null) },
                label = { Text("All") },
                selected = selectedColor == null
            )
        }

        items(Note.COLOR_TAGS) { colorTag ->
            FilterChip(
                onClick = { onColorSelected(colorTag) },
                label = {
                    Text(colorTag.replaceFirstChar { it.uppercase() })
                },
                selected = selectedColor == colorTag,
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = com.noties.app.ui.theme.NoteColors.getNoteColor(colorTag),
                                shape = CircleShape
                            )
                    )
                }
            )
        }
    }
}

@Composable
private fun NotesGrid(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onArchiveNote: (Long) -> Unit,
    onDeleteNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp > 600) 3 else 2

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                onArchive = { onArchiveNote(note.id) },
                onDelete = { onDeleteNote(note) }
            )
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onArchiveNote: (Long) -> Unit,
    onDeleteNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                onArchive = { onArchiveNote(note.id) },
                onDelete = { onDeleteNote(note) },
                isListMode = true
            )
        }
    }
}

@Composable
private fun EmptyNotesState(
    searchQuery: String,
    selectedColorFilter: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = when {
                    searchQuery.isNotEmpty() -> Icons.Default.SearchOff
                    selectedColorFilter != null -> Icons.Default.FilterAlt
                    else -> Icons.Default.Note
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "No notes found for \"$searchQuery\""
                    selectedColorFilter != null -> "No $selectedColorFilter notes found"
                    else -> "No notes yet"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = when {
                    searchQuery.isNotEmpty() || selectedColorFilter != null -> "Try adjusting your filters"
                    else -> "Tap + to create your first note"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NotiesTheme {
        // Preview implementation would go here
    }
}