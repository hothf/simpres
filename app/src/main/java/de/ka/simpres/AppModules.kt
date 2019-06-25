package de.ka.simpres

import de.ka.simpres.repo.*
import de.ka.simpres.ui.home.HomeViewModel
import de.ka.simpres.ui.MainViewModel
import de.ka.simpres.ui.home.detail.HomeDetailFragment
import de.ka.simpres.ui.home.detail.HomeDetailViewModel
import de.ka.simpres.ui.home.detail.newedit.NewEditHomeDetailViewModel
import de.ka.simpres.ui.home.newedit.NewEditHomeViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Declares all modules used for koin dependency injection.
 */

val appModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { HomeDetailViewModel(get()) }
    viewModel { NewEditHomeViewModel(get()) }
    viewModel { NewEditHomeDetailViewModel(get()) }

    single { RepositoryImpl() as Repository }
}