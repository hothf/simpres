package de.ka.simpres.ui.subjects.detail.idealist.newedit

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.NavigationUtils
import de.ka.simpres.utils.ViewUtils
import de.ka.simpres.utils.closeAttachedKeyboard

class NewEditIdeaViewModel : BaseViewModel() {

    val getTitleTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")

        currentIdea?.title = it
    }
    val getSumTextChangedListener = ViewUtils.TextChangeListener {
        sum.value = it
        sumError.postValue("")

        currentIdea?.sum = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { submit() }
    val sum = MutableLiveData<String>().apply { value = "" }
    val sumError = MutableLiveData<String>().apply { value = "" }
    val sumSelection = MutableLiveData<Int>().apply { value = 0 }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }

    private var currentIdea: IdeaItem? = null
    private var currentSubjectId: String? = null

    fun onBack() = navigateTo(NavigationUtils.BACK)

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
     * Sets up a new empty idea.
     */
    fun setupNew(subjectId: String) {
        currentIdea = IdeaItem()
        currentSubjectId = subjectId

        updateTextViews()
    }

    /**
     * Sets up an editable idea, taken from the given item.
     */
    fun setupEdit(subjectId: String, idea: IdeaItem) {
        currentIdea = idea
        currentSubjectId = subjectId

        updateTextViews()
    }

    private fun updateTextViews() {
        if (currentIdea != null) {
            title.postValue(currentIdea?.title)
            titleSelection.postValue(currentIdea?.title?.length)
            titleError.postValue("")
            sum.postValue(currentIdea?.sum)
            sumSelection.postValue(currentIdea?.sum?.length)
            sumError.postValue("")
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
            sum.postValue("")
            sumSelection.postValue(0)
            sumError.postValue("")
        }
    }

}