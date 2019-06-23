package de.ka.simpres

import de.ka.simpres.repo.*
import de.ka.simpres.ui.home.HomeViewModel
import de.ka.simpres.ui.MainViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Declares all modules used for koin dependency injection.
 */

val appModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }

    single { RepositoryImpl() as Repository }
}