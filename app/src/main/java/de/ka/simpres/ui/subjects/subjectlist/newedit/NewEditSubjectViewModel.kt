package de.ka.simpres.ui.subjects.subjectlist.newedit

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflecopter.multicontactpicker.ContactResult
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.utils.*
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.color.ColorAdapter
import de.ka.simpres.utils.color.ColorItemViewModel
import de.ka.simpres.utils.color.ColorResources
import de.ka.simpres.utils.resources.ResourcesProvider
import org.koin.standalone.inject
import java.util.*

class NewEditSubjectViewModel : BaseViewModel() {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleSelection.value = it.length
        titleError.postValue(null)

        currentSubject?.title = it
    }
    val getContextNotesChangedListener = ViewUtils.TextChangeListener {
        contactNotes.value = it
        contactNotesSelection.value = it.length

        currentSubject?.contactNote = it
    }
    val onPushChanged = CompoundButton.OnCheckedChangeListener { _, changed: Boolean ->
        currentSubject?.pushEnabled = changed
        pushEnabled.value = changed
    }
    private val chooseColor: (ColorItemViewModel) -> Unit = {
        currentSubject?.color = it.colorString
    }
    val getDoneListener = ViewUtils.TextDoneListener { }
    val navTitle = MutableLiveData<String>().apply { value = "" }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String?>().apply { value = null }
    val contactNotes = MutableLiveData<String>().apply { value = null }
    val contactNotesSelection = MutableLiveData<Int>().apply { value = 0 }
    val contactName = MutableLiveData<String>().apply { value = "" }
    val contactUri = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }
    val date = MutableLiveData<String>().apply { value = "" }
    val pushEnabled = MutableLiveData<Boolean>().apply { value = false }
    val adapter = ColorAdapter(chooseColor)

    private val resourcesProvider: ResourcesProvider by inject()
    private val inputValidator: InputValidator by inject()
    private val titleValidator = inputValidator.Validator(
        InputValidator.ValidatorConfig(
            titleError,
            listOf(ValidationRules.NOT_EMPTY, ValidationRules.MIN_4)
        )
    )
    var currentSubject: SubjectItem? = null
        private set

    private var isUpdating = false


    fun onBack(v: View) {
        v.closeAttachedKeyboard()
        navigateTo(BACK)
    }

    fun chooseContact() {
        handle(OpenContactsPicker())
    }

    fun layoutManager() =
        LinearLayoutManager(resourcesProvider.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false)

    fun submit(view: View? = null) {
        if (!titleValidator.isValid(title.value)) return

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

        adapter.markColor(currentSubject?.color)
    }

    /**
     * Sets up an editable subject, received from the given id, if possible.
     */
    fun setupEdit(subjectId: Long) {
        currentSubject = repository.findSubjectById(subjectId)?.copy()

        isUpdating = true

        updateTextViews()

        adapter.markColor(currentSubject?.color)
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

    /**
     * Updates the current contact.
     *
     * @param result the contact to update
     */
    fun updateContact(result: ContactResult) {
        currentSubject?.let {
            it.contactName = result.displayName
            it.contactUri = result.thumbnail?.toString()
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
            contactName.postValue(currentSubject?.contactName)
            contactUri.postValue(currentSubject?.contactUri)
            contactNotes.postValue(currentSubject?.contactNote)
            contactNotesSelection.postValue(currentSubject?.contactNote?.length)
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue(null)
            date.postValue(
                resourcesProvider.getString(R.string.subject_newedit_remind_on, System.currentTimeMillis().toDate())
            )
            pushEnabled.postValue(true)
            contactName.postValue("")
            contactUri.postValue("")
            contactNotesSelection.postValue(0)
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

    class OpenContactsPicker
}