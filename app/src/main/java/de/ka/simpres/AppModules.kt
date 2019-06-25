package de.ka.simpres

import de.ka.simpres.repo.*
import de.ka.simpres.ui.subjects.SubjectsViewModel
import de.ka.simpres.ui.MainViewModel
import de.ka.simpres.ui.subjects.detail.SubjectsDetailViewModel
import de.ka.simpres.ui.subjects.detail.idealist.newedit.NewEditIdeaViewModel
import de.ka.simpres.ui.subjects.subjectlist.newedit.NewEditSubjectViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Declares all modules used for koin dependency injection.
 */

val appModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { SubjectsViewModel(get()) }
    viewModel { SubjectsDetailViewModel(get()) }
    viewModel { NewEditSubjectViewModel(get()) }
    viewModel { NewEditIdeaViewModel(get()) }

    single { RepositoryImpl() as Repository }
}