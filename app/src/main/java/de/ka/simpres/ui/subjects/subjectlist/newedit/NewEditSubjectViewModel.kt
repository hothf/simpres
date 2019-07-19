package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.utils.NavigationUtils
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.ViewUtils
import de.ka.simpres.utils.closeAttachedKeyboard
import de.ka.simpres.utils.resources.ColorResources
import de.ka.simpres.utils.resources.ResourcesProvider
import de.ka.simpres.utils.toDate
import org.koin.standalone.inject
import java.util.*

class NewEditSubjectViewModel : BaseViewModel() {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")
        titleSelection.value = it.length

        currentSubject?.title = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { }
    val navTitle = MutableLiveData<String>().apply { value = "" }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }
    val date = MutableLiveData<String>().apply { value = "" }
    val pushEnabled = MutableLiveData<Boolean>().apply { value = false }

    val resourcesProvider: ResourcesProvider by inject()

    private var currentSubject: SubjectItem? = null

    private var isUpdating = false

    fun onBack(v: View) {
        v.closeAttachedKeyboard()
        navigateTo(BACK)
    }

    fun submit(view: View? = null) {
        view?.closeAttachedKeyboard()

        currentSubject?.let {
            it.pushEnabled = pushEnabled.value!!
            if (isUpdating) {
                navigateTo(BACK)
                repository.updateSubject(it)
            } else {
                repository.saveSubject(it)
                navigateTo(
                    navigationTargetId = R.id.action_subjectNewEditFragment_to_subjectsDetailFragment,
                    args = Bundle().apply {
                        putLong(SubjectsDetailFragment.SUBJECT_ID_KEY, it.id)
                        putBoolean(SubjectsDetailFragment.SUBJECT_IS_NEW, true)
                    },
                    popupToId = R.id.subjectNewEditFragment
                )
            }
        }
    }

    /**
     * Sets up a new empty subject.
     */
    fun setupNew() {
        currentSubject = SubjectItem(0).apply {
            color = ColorResources.getRandomColorString()
        }

        isUpdating = false

        updateTextViews()
    }

    /**
     * Sets up an editable subject, received from the given id, if possible.
     */
    fun setupEdit(subjectId: Long) {
        currentSubject = repository.findSubjectById(subjectId)?.copy()

        isUpdating = true

        updateTextViews()
    }

    /**
     * Updates the date.
     *
     * @param year the date year
     * @param month the date month
     * @param day the date day
     */
    fun updateDate(year: Int, month: Int, day: Int) {
        currentSubject?.let {
            it.date = Calendar.getInstance().apply {
                time = Date(it.date)
                set(year, month, day)
            }.timeInMillis
        }

        updateTextViews()
    }

    private fun updateTextViews() {
        if (isUpdating) {
            navTitle.postValue(resourcesProvider.getString(R.string.subject_newedit_edit))
        } else {
            navTitle.postValue(resourcesProvider.getString(R.string.subject_newedit_add))
        }
        if (currentSubject != null) {
            title.postValue(currentSubject?.title)
            titleSelection.postValue(currentSubject?.title?.length)
            titleError.postValue("")
            date.postValue("Remind on ${currentSubject?.date?.toDate()}")
            pushEnabled.postValue(currentSubject?.pushEnabled)
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
            date.postValue("Remind on ${System.currentTimeMillis().toDate()}")
            pushEnabled.postValue(true)
        }
    }

    /**
     * Requests to open the date picker.
     *
     * @param view the view requesting the open
     */
    fun onOpenDatePicker(view: View) {
        view.closeAttachedKeyboard()
        currentSubject?.let {
            handle(OpenDatePickerEvent(it.date))
        }
    }

    class OpenDatePickerEvent(val date: Long)

}