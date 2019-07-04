package de.ka.simpres.ui.subjects.detail.idealist.newedit

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.ViewUtils
import de.ka.simpres.utils.closeAttachedKeyboard

class NewEditIdeaViewModel : BaseViewModel() {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")

        currentIdea?.title = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { submit() }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }

    private var currentIdea: IdeaItem? = null
    private var currentSubjectId: String? = null

    fun submit(view: View? = null) {

        view?.closeAttachedKeyboard()

        currentIdea?.let { idea ->
            currentSubjectId?.let { id ->
                idea.id = System.currentTimeMillis().toString()
                repository.saveOrUpdateIdea(id, idea)
                navigateTo(
                    navigationTargetId = R.id.action_ideaNewEditFragment_to_subjectsDetailFragment,
                    args = Bundle().apply { putString(SubjectsDetailFragment.SUBJECT_ID_KEY, id) },
                    popupToId = R.id.subjectsDetailFragment
                )
            }

        }


    }

    /**
     *
     */
    fun setupNew(id: String) {
        currentIdea = IdeaItem()
        currentSubjectId = id
//        currentTitle = ""

//        header.postValue(app.getString(R.string.suggestions_newedit_title))
//        saveDrawableRes.postValue(R.drawable.ic_small_add)

        updateTextViews()
    }

    /**
     *
     */
    fun setupEdit(id: String, homeItem: IdeaItem) {
        currentIdea = homeItem
        currentSubjectId = id
//        currentTitle = suggestion.title

//        header.postValue(app.getString(R.string.suggestions_newedit_edit))
//        saveDrawableRes.postValue(R.drawable.ic_small_done)

        updateTextViews()
    }

    private fun updateTextViews() {
        if (currentIdea != null) {
            title.postValue(currentIdea?.title)
            titleSelection.postValue(currentIdea?.title?.length)
            titleError.postValue("")
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
        }

    }

}