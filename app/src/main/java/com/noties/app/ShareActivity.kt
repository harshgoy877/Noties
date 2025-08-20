package com.noties.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive the SEND intent
        intent?.let {
            if (it.action == Intent.ACTION_SEND && it.type == "text/plain") {
                val sharedText = it.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                // For now, just finish or route to editor
            }
        }
        finish()
    }
}
