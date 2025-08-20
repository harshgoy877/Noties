package com.noties.app

import android.app.Application
import com.noties.app.data.database.NotesDatabase
import com.noties.app.data.repository.NotesRepository

/**
 * Application class for Noties.
 * Initializes database and provides dependency injection.
 */
class NotiesApplication : Application() {

    // Database instance
    val database by lazy { NotesDatabase.getDatabase(this) }

    // Repository instance
    val repository by lazy { NotesRepository(database.noteDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: NotiesApplication
            private set
    }
}