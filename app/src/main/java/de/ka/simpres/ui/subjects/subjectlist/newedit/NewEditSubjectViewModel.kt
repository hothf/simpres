package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.ViewUtils
import de.ka.simpres.utils.closeAttachedKeyboard
import de.ka.simpres.utils.toDate
import java.util.*

class NewEditSubjectViewModel : BaseViewModel() {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")

        currentSubject?.title = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { submit() }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }
    val date = MutableLiveData<String>().apply { value = "" }

    private var currentSubject: SubjectItem? = null

    private var isUpdating = false

    fun onBack() = navigateTo(BACK)

    fun submit(view: View? = null) {
        view?.closeAttachedKeyboard()

        currentSubject?.let {
            repository.saveOrUpdateSubject(it)

            if (isUpdating) {
                navigateTo(BACK)
            } else {
                navigateTo(
                    navigationTargetId = R.id.action_subjectNewEditFragment_to_subjectsDetailFragment,
                    args = Bundle().apply { putLong(SubjectsDetailFragment.SUBJECT_ID_KEY, it.id) },
                    popupToId = R.id.subjectNewEditFragment
                )
            }
        }
    }

    /**
     * Sets up a new empty subject.
     */
    fun setupNew() {
        currentSubject = SubjectItem(0)

        isUpdating = false

        updateTextViews()
    }

    /**
     * Sets up an editable subject, received from the given id, if possible.
     */
    fun setupEdit(subjectId: Long) {
        currentSubject = repository.findSubjectById(subjectId)

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
        if (currentSubject != null) {
            title.postValue(currentSubject?.title)
            titleSelection.postValue(currentSubject?.title?.length)
            titleError.postValue("")
            date.postValue(currentSubject?.date?.toDate())
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
            date.postValue(System.currentTimeMillis().toDate())
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