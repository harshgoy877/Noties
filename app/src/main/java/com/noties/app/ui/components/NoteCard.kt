package com.noties.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noties.app.data.model.Note
import com.noties.app.ui.theme.NoteColors
import com.noties.app.ui.theme.NotiesShapes
import com.noties.app.ui.theme.NotiesTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card component for displaying individual notes.
 * Supports both grid and list view modes with quick actions.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    isListMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showActionsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isListMode) {
                    Modifier.height(80.dp)
                } else {
                    Modifier.height(140.dp)
                }
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showActionsDialog = true }
            ),
        shape = NotiesShapes.NoteCard,
        colors = CardDefaults.cardColors(
            containerColor = NoteColors.getNoteColor(
                note.colorTag,
                isDark = false // You might want to get this from theme
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with color indicator and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = NoteColors.getNoteColor(note.colorTag, isDark = true),
                            shape = CircleShape
                        )
                )

                // Timestamp
                Text(
                    text = formatTimestamp(note.modifiedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Note content
            if (isListMode) {
                // List mode: horizontal layout
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (note.title.isNotBlank()) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (note.content.isNotBlank()) {
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Quick actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onArchive,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = "Archive",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            } else {
                // Grid mode: vertical layout
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (note.title.isNotBlank()) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (note.content.isNotBlank()) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (note.title.isBlank()) 4 else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // Actions Dialog
    if (showActionsDialog) {
        AlertDialog(
            onDismissRequest = { showActionsDialog = false },
            title = { Text("Note Actions") },
            text = {
                Text("Choose an action for this note.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onArchive()
                    showActionsDialog = false
                }) {
                    Text("Archive")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            onDelete()
                            showActionsDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }

                    TextButton(onClick = { showActionsDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

/**
 * Formats timestamp for display on note cards
 */
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Now" // Less than 1 minute
        diff < 3_600_000 -> "${diff / 60_000}m" // Less than 1 hour
        diff < 86_400_000 -> "${diff / 3_600_000}h" // Less than 1 day
        diff < 604_800_000 -> "${diff / 86_400_000}d" // Less than 1 week
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteCardPreview() {
    NotiesTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            NoteCard(
                note = Note(
                    id = 1,
                    title = "Sample Note",
                    content = "This is a sample note with some content to demonstrate the card layout.",
                    colorTag = "blue",
                    createdAt = System.currentTimeMillis() - 3600000,
                    modifiedAt = System.currentTimeMillis() - 1800000
                ),
                onClick = { },
                onArchive = { },
                onDelete = { }
            )

            NoteCard(
                note = Note(
                    id = 2,
                    title = "List Mode Note",
                    content = "This demonstrates the list mode layout.",
                    colorTag = "green",
                    createdAt = System.currentTimeMillis() - 7200000,
                    modifiedAt = System.currentTimeMillis() - 3600000
                ),
                onClick = { },
                onArchive = { },
                onDelete = { },
                isListMode = true
            )
        }
    }
}