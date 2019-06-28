package de.ka.simpres

import de.ka.simpres.repo.*
import de.ka.simpres.ui.subjects.SubjectsViewModel
import de.ka.simpres.ui.MainViewModel
import de.ka.simpres.ui.subjects.detail.SubjectsDetailViewModel
import de.ka.simpres.ui.subjects.detail.idealist.newedit.NewEditIdeaViewModel
import de.ka.simpres.ui.subjects.subjectlist.newedit.NewEditSubjectViewModel
import de.ka.simpres.utils.resources.ResourcesProvider
import de.ka.simpres.utils.resources.ResourcesProviderImpl
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Declares all modules used for koin dependency injection.
 */

val appModule = module {
    viewModel { MainViewModel() }
    viewModel { SubjectsViewModel() }
    viewModel { SubjectsDetailViewModel() }
    viewModel { NewEditSubjectViewModel() }
    viewModel { NewEditIdeaViewModel() }

    single { ResourcesProviderImpl(get()) as ResourcesProvider }
    single { RepositoryImpl() as Repository }
}