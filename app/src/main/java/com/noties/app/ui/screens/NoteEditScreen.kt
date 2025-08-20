package com.noties.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noties.app.data.model.Note
import com.noties.app.ui.components.SimpleTopBar
import com.noties.app.ui.theme.NoteColors
import com.noties.app.ui.theme.NotiesTheme
import com.noties.app.ui.viewmodel.NotesViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for creating and editing notes.
 * Features auto-save, color selection, and rich formatting options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var note by remember { mutableStateOf<Note?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("blue") }
    var fontSize by remember { mutableIntStateOf(16) }
    var fontStyle by remember { mutableStateOf("regular") }

    var showColorPicker by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isNewNote = noteId == 0L

    // Load existing note
    LaunchedEffect(noteId) {
        if (!isNewNote) {
            // In a real app, you'd load the note here
            // For now, we'll use a placeholder
            note = Note(
                id = noteId,
                title = title,
                content = content,
                colorTag = selectedColor,
                createdAt = System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis(),
                fontSize = fontSize,
                fontStyle = fontStyle
            )
        }
    }

    // Auto-save functionality
    LaunchedEffect(title, content, selectedColor, fontSize, fontStyle) {
        if (!isNewNote && note != null) {
            delay(2000) // Auto-save after 2 seconds of inactivity
            viewModel.updateNoteContent(noteId, title, content)
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = if (isNewNote) "New Note" else "Edit Note",
                onBackClick = {
                    if (isNewNote && (title.isNotBlank() || content.isNotBlank())) {
                        // Save new note before going back
                        viewModel.createNote(title.ifBlank { "Untitled" }, content, selectedColor)
                    }
                    onNavigateBack()
                },
                actions = {
                    // Color picker toggle
                    IconButton(onClick = { showColorPicker = !showColorPicker }) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = NoteColors.getNoteColor(selectedColor),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        )
                    }

                    // Font size
                    IconButton(onClick = { showFontSizeDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Font Size"
                        )
                    }

                    // Share note
                    if (!isNewNote && (title.isNotBlank() || content.isNotBlank())) {
                        IconButton(
                            onClick = {
                                // Implement share functionality
                                val shareText = buildString {
                                    if (title.isNotBlank()) {
                                        append(title)
                                        append("\n\n")
                                    }
                                    append(content)
                                }
                                // You would implement Android's share intent here
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Note"
                            )
                        }
                    }

                    // Delete note
                    if (!isNewNote) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Note",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Color picker row
            if (showColorPicker) {
                ColorPickerRow(
                    selectedColor = selectedColor,
                    onColorSelected = { color ->
                        selectedColor = color
                        if (!isNewNote) {
                            viewModel.updateNoteColor(noteId, color)
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Note metadata (for existing notes)
            if (!isNewNote && note != null) {
                NoteMetadata(
                    note = note!!,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Note editing area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Note title...",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = (fontSize * 1.25).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontSize = (fontSize * 1.25).sp,
                        fontWeight = when (fontStyle) {
                            "bold", "bold_italic" -> FontWeight.Bold
                            else -> FontWeight.Normal
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NoteColors.getNoteColor(selectedColor).copy(alpha = 0.1f),
                        unfocusedContainerColor = NoteColors.getNoteColor(selectedColor).copy(alpha = 0.05f),
                        focusedBorderColor = NoteColors.getNoteColor(selectedColor, isDark = true),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = false,
                    maxLines = 3
                )

                // Content field
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 200.dp),
                    placeholder = {
                        Text(
                            text = "Start writing your note...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = fontSize.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSize.sp,
                        fontWeight = when (fontStyle) {
                            "bold", "bold_italic" -> FontWeight.Bold
                            else -> FontWeight.Normal
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NoteColors.getNoteColor(selectedColor).copy(alpha = 0.1f),
                        unfocusedContainerColor = NoteColors.getNoteColor(selectedColor).copy(alpha = 0.05f),
                        focusedBorderColor = NoteColors.getNoteColor(selectedColor, isDark = true),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = false
                )
            }

            // Font formatting options
            FontFormattingBar(
                currentFontStyle = fontStyle,
                onFontStyleChanged = { style ->
                    fontStyle = style
                    if (!isNewNote) {
                        viewModel.updateNoteFontSettings(noteId, fontSize, style)
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    // Font size dialog
    if (showFontSizeDialog) {
        FontSizeDialog(
            currentSize = fontSize,
            onSizeSelected = { size ->
                fontSize = size
                if (!isNewNote) {
                    viewModel.updateNoteFontSettings(noteId, size, fontStyle)
                }
                showFontSizeDialog = false
            },
            onDismiss = { showFontSizeDialog = false }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        note?.let { viewModel.deleteNote(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Auto-save indicator
    if (uiState.isLoading) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = NoteColors.getNoteColor(selectedColor, isDark = true)
        )
    }
}

@Composable
private fun ColorPickerRow(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(Note.COLOR_TAGS) { colorTag ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = NoteColors.getNoteColor(colorTag),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorTag) }
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selectedColor == colorTag) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = NoteColors.getNoteColor(colorTag, isDark = true),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteMetadata(
    note: Note,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Created: ${SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(note.createdAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Modified: ${SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(note.modifiedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FontFormattingBar(
    currentFontStyle: String,
    onFontStyleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Note.FONT_STYLES.forEach { style ->
            FilterChip(
                onClick = { onFontStyleChanged(style) },
                label = {
                    Text(
                        text = style.replace("_", " ").replaceFirstChar { it.uppercase() },
                        fontWeight = when (style) {
                            "bold", "bold_italic" -> FontWeight.Bold
                            else -> FontWeight.Normal
                        }
                    )
                },
                selected = currentFontStyle == style
            )
        }
    }
}

@Composable
private fun FontSizeDialog(
    currentSize: Int,
    onSizeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val fontSizes = listOf(12, 14, 16, 18, 20, 24, 28)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Font Size") },
        text = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fontSizes) { size ->
                    FilterChip(
                        onClick = { onSizeSelected(size) },
                        label = { Text("${size}sp") },
                        selected = currentSize == size
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteEditScreenPreview() {
    NotiesTheme {
        // Preview implementation would go here
    }
}