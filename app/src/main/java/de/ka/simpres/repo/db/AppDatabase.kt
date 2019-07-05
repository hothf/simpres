package de.ka.simpres.repo.db

import android.app.Application
import de.ka.simpres.repo.model.MyObjectBox
import io.objectbox.BoxStore

/**
 * A object box database.
 */
class AppDatabase(application: Application) {

    private val db: BoxStore by lazy { MyObjectBox.builder().androidContext(application.applicationContext).build() }

    /**
     * Retrieve the object box.
     */
    fun get() = db
}