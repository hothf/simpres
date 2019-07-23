package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.ViewUtils
import de.ka.simpres.utils.closeAttachedKeyboard
import de.ka.simpres.utils.color.ColorAdapter
import de.ka.simpres.utils.color.ColorItemViewModel
import de.ka.simpres.utils.color.ColorResources
import de.ka.simpres.utils.resources.ResourcesProvider
import de.ka.simpres.utils.toDate
import org.koin.standalone.inject
import java.util.*

class NewEditSubjectViewModel : BaseViewModel() {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue(null)
        titleSelection.value = it.length

        currentSubject?.title = it
    }
    val onPushChanged = CompoundButton.OnCheckedChangeListener { _, changed: Boolean ->
        currentSubject?.pushEnabled = changed
        pushEnabled.value = changed
    }
    val getDoneListener = ViewUtils.TextDoneListener { }
    val navTitle = MutableLiveData<String>().apply { value = "" }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String?>().apply { value = null }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }
    val date = MutableLiveData<String>().apply { value = "" }
    val pushEnabled = MutableLiveData<Boolean>().apply { value = false }
    val adapter = MutableLiveData<ColorAdapter>()

    private val resourcesProvider: ResourcesProvider by inject()

    private var currentSubject: SubjectItem? = null
    private var isUpdating = false

    private val chooseColor: (ColorItemViewModel) -> Unit = {
        currentSubject?.color = it.colorString
    }

    fun onBack(v: View) {
        v.closeAttachedKeyboard()
        navigateTo(BACK)
    }

    fun setupAdapterAndLoad(owner: LifecycleOwner) {
        if (adapter.value == null) {
            adapter.value = ColorAdapter(chooseColor, owner)
        }
        adapter.value?.let {
            it.owner = owner
            it.markColor(currentSubject?.color)
        }
    }

    fun layoutManager() =
        LinearLayoutManager(resourcesProvider.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false)

    fun submit(view: View? = null) {
        view?.closeAttachedKeyboard()

        currentSubject?.let {
            if (isUpdating) {
                navigateTo(BACK)
                repository.updateSubject(it)
            } else {
                repository.saveSubject(it)
                navigateTo(
                    animType = AnimType.MODAL,
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
                time = Date(System.currentTimeMillis())
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
            titleError.postValue(null)
            date.postValue(
                resourcesProvider.getString(R.string.subject_newedit_remind_on, currentSubject?.date?.toDate())
            )
            pushEnabled.postValue(currentSubject?.pushEnabled)
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue(null)
            date.postValue(
                resourcesProvider.getString(R.string.subject_newedit_remind_on, System.currentTimeMillis().toDate())
            )
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