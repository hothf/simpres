package de.ka.simpres.ui.subjects.detail.idealist.newedit

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.NavigationUtils
import de.ka.simpres.utils.ViewUtils
import de.ka.simpres.utils.closeAttachedKeyboard
import de.ka.simpres.utils.resources.ResourcesProvider
import org.koin.standalone.inject

class NewEditIdeaViewModel : BaseViewModel() {

    val getTitleTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue(null)
        titleSelection.value = it.length

        currentIdea?.title = it
    }
    val getSumTextChangedListener = ViewUtils.TextChangeListener {
        sum.value = it
        sumError.postValue(null)
        sumSelection.value = it.length

        currentIdea?.sum = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { }
    val sum = MutableLiveData<String>().apply { value = "" }
    val sumError = MutableLiveData<String>().apply { value = "" }
    val sumSelection = MutableLiveData<Int>().apply { value = null }
    val navTitle = MutableLiveData<String>().apply { value = "" }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = null }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }

    private val resourcesProvider: ResourcesProvider by inject()

    private var currentIdea: IdeaItem? = null
    private var currentSubjectId: Long = -1
    private var isUpdating = false

    fun onBack(v: View) {
        v.closeAttachedKeyboard()
        navigateTo(NavigationUtils.BACK)
    }

    fun submit(view: View? = null) {

        view?.closeAttachedKeyboard()

        currentIdea?.let { idea ->
            repository.saveOrUpdateIdea(idea)
            navigateTo(NavigationUtils.BACK)
        }
    }

    /**
     * Sets up a new empty idea.
     */
    fun setupNew(subjectId: Long) {
        currentIdea = IdeaItem(0, subjectId)
        currentSubjectId = subjectId

        isUpdating = false

        updateTextViews()
    }

    /**
     * Sets up an editable idea, taken from the given item.
     */
    fun setupEdit(subjectId: Long, idea: IdeaItem) {
        currentIdea = idea.copy()
        currentSubjectId = subjectId

        isUpdating = true

        updateTextViews()
    }

    private fun updateTextViews() {
        if (isUpdating) {
            navTitle.postValue(resourcesProvider.getString(R.string.idea_newedit_edit))
        } else {
            navTitle.postValue(resourcesProvider.getString(R.string.idea_newedit_add))
        }
        if (currentIdea != null) {
            title.postValue(currentIdea?.title)
            titleSelection.postValue(currentIdea?.title?.length)
            titleError.postValue(null)
            sum.postValue(currentIdea?.sum)
            sumSelection.postValue(currentIdea?.sum?.length)
            sumError.postValue(null)
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue(null)
            sum.value = ("")
            sumSelection.postValue(0)
            sumError.postValue(null)
        }
    }

}