package com.noties.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noties.app.ui.navigation.NotiesNavigation
import com.noties.app.ui.theme.NotiesTheme
import com.noties.app.ui.viewmodel.NotesViewModel

/**
 * Main Activity for the Noties app.
 * Sets up the Compose UI with Material 3 theming and edge-to-edge experience.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotiesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val notesViewModel: NotesViewModel = viewModel {
                        NotesViewModel((application as NotiesApplication).repository)
                    }

                    NotiesNavigation(notesViewModel = notesViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    NotiesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Preview content here
        }
    }
}