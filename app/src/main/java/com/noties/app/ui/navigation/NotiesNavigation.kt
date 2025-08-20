package com.noties.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noties.app.ui.screens.HomeScreen
import com.noties.app.ui.screens.NoteEditScreen
import com.noties.app.ui.screens.ArchivedNotesScreen
import com.noties.app.ui.viewmodel.NotesViewModel

/**
 * Navigation component for the Noties app.
 * Handles navigation between different screens.
 */
@Composable
fun NotiesNavigation(
    navController: NavHostController = rememberNavController(),
    notesViewModel: NotesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = notesViewModel,
                onNavigateToNote = { noteId ->
                    navController.navigate("note_edit/$noteId")
                },
                onNavigateToNewNote = {
                    navController.navigate("note_edit/0")
                },
                onNavigateToArchived = {
                    navController.navigate("archived")
                }
            )
        }

        composable("note_edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: 0L
            NoteEditScreen(
                noteId = noteId,
                viewModel = notesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("archived") {
            ArchivedNotesScreen(
                viewModel = notesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNote = { noteId ->
                    navController.navigate("note_edit/$noteId")
                }
            )
        }
    }
}