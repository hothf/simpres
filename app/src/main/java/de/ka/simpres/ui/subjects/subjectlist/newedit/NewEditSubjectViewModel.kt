package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.utils.ViewUtils

class NewEditSubjectViewModel(app: Application) : BaseViewModel(app) {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")

        currentSubject?.title = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { submit() }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }

    private var currentSubject: SubjectItem? = null

    fun submit() {

        currentSubject?.let {

            it.id = System.currentTimeMillis().toString()

            repository.saveSubject(it)

            navigateTo(
                navigationTargetId = R.id.action_subjectNewEditFragment_to_subjectsDetailFragment,
                args = Bundle().apply { putString(SubjectsDetailFragment.SUBJECT_ID_KEY, it.id) },
                popupToId = R.id.subjectNewEditFragment
            )
        }



    }

    /**
     *
     */
    fun setupNew() {
        currentSubject = SubjectItem()
//        currentTitle = ""

//        header.postValue(app.getString(R.string.suggestions_newedit_title))
//        saveDrawableRes.postValue(R.drawable.ic_small_add)

        updateTextViews()
    }

    /**
     *
     */
    fun setupEdit(homeItem: SubjectItem) {
        currentSubject = homeItem
//        currentTitle = suggestion.title

//        header.postValue(app.getString(R.string.suggestions_newedit_edit))
//        saveDrawableRes.postValue(R.drawable.ic_small_done)

        updateTextViews()
    }

    private fun updateTextViews() {
        if (currentSubject != null) {
            title.postValue(currentSubject?.title)
            titleSelection.postValue(currentSubject?.title?.length)
            titleError.postValue("")
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
        }

    }

}