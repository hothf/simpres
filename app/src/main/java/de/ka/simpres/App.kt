package de.ka.simpres

import android.app.Application
import org.koin.android.ext.android.startKoin
import timber.log.Timber

/**
 * App creation point. Please keep as simple as possible - keep an eye on memory leaks: please
 * do not access the app context through a singleton here;
 *
 * Application context and resources can be used by injecting a [de.ka.simpres.utils.resources.ResourcesProvider].
 *
 **/
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // start injecting with koin
        startKoin(this, listOf(appModule))

        // debug logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}