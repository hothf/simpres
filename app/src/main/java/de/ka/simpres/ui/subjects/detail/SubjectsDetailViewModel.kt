package de.ka.simpres.ui.subjects.detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.idealist.IdeaAdapter
import de.ka.simpres.ui.subjects.detail.idealist.IdeaItemViewModel
import de.ka.simpres.ui.subjects.detail.idealist.newedit.NewEditIdeaFragment
import de.ka.simpres.ui.subjects.subjectlist.newedit.NewEditSubjectFragment
import de.ka.simpres.utils.*
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.color.ColorResources
import de.ka.simpres.utils.resources.ResourcesProvider
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import org.koin.standalone.inject

class SubjectsDetailViewModel : BaseViewModel() {

    private var currentColor = ColorResources.getRandomColorString()
    private var currentSubject: SubjectItem? = null
    private var currentSubjectId = -1L
    private var isLoading = false

    private val resourcesProvider: ResourcesProvider by inject()

    val touchHelper = MutableLiveData<ItemTouchHelper>()
    val adapter = MutableLiveData<IdeaAdapter>()
    val refresh = MutableLiveData<Boolean>().apply { value = false }
    val blankVisibility = MutableLiveData<Int>().apply { value = View.GONE }
    val title = MutableLiveData<String>()
    val sumUnspent =
        MutableLiveData<String>().apply { value = resourcesProvider.getString(R.string.app_general_empty_sign) }
    val doneAmount =
        MutableLiveData<String>().apply { value = resourcesProvider.getString(R.string.app_general_empty_sign) }
    val color = MutableLiveData<Int>().apply { value = Color.parseColor(currentColor) }
    val allAmount =
        MutableLiveData<String>().apply { value = resourcesProvider.getString(R.string.app_general_empty_sign) }
    val remind =
        MutableLiveData<String>().apply { value = resourcesProvider.getString(R.string.app_general_empty_sign) }
    val date = MutableLiveData<String>().apply { value = resourcesProvider.getString(R.string.app_general_empty_sign) }
    val sumSpent =
        MutableLiveData<String>().apply { value = resourcesProvider.getString(R.string.app_general_empty_sign) }
    val contactUri = MutableLiveData<String>().apply { value = "" }

    private val removeListener = { viewModel: IdeaItemViewModel ->
        repository.removeIdea(viewModel.item)
        showSnack(
            resourcesProvider.getString(R.string.idea_delete_undo_title),
            Snacker.SnackType.DEFAULT,
            repository::undoDeleteIdea,
            resourcesProvider.getString(R.string.idea_delete_undo_action)
        )
    }

    private val addListener = {
        navigateTo(
            R.id.ideaNewEditFragment,
            args = Bundle().apply {
                putLong(NewEditIdeaFragment.SUBJECT_ID_KEY, currentSubjectId)
                putBoolean(NewEditIdeaFragment.NEW_KEY, true)
            },
            animType = AnimType.MODAL
        )
    }
    private val ideaClickListener = { ideaItem: IdeaItem ->
        navigateTo(
            R.id.action_subjectsDetailFragment_to_ideaNewEditFragment,
            false,
            Bundle().apply {
                putLong(NewEditIdeaFragment.SUBJECT_ID_KEY, currentSubjectId)
                putSerializable(NewEditIdeaFragment.IDEA_KEY, ideaItem)
            },
            animType = AnimType.MODAL
        )
    }

    fun itemAnimator() = SlideInDownAnimator()

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun onBack() = navigateTo(BACK)

    fun clickContact() {
        currentSubject?.let {
            val firstPart =
                if (!it.contactName.isNullOrBlank()) "${it.contactName}"
                else resourcesProvider.getString(R.string.subject_detail_contactnote)
            val secondPart = if (!it.contactNote.isNullOrBlank()) ": ${it.contactNote}" else ""
            showSnack("$firstPart$secondPart")
        }
    }

    fun onEditSubject() {
        navigateTo(
            R.id.action_subjectsDetailFragment_to_subjectNewEditFragment,
            false,
            Bundle().apply { putLong(NewEditSubjectFragment.SUBJECT_ID_KEY, currentSubjectId) },
            animType = AnimType.MODAL
        )
    }

    init {
        repository.observableIdeas
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onError = ::handleGeneralError,
                onNext = { result ->
                    adapter.value?.let {
                        it.overwriteList(result, currentColor)

                        if (it.isEmpty) {
                            blankVisibility.postValue(View.VISIBLE)
                        } else {
                            blankVisibility.postValue(View.GONE)
                        }
                    }
                }
            )
            .addTo(compositeDisposable)

        repository.observableSubjects
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onError = ::handleGeneralError,
                onNext = {
                    it.list.find { subject -> subject.id == currentSubjectId }?.let { subject ->
                        updateSubject(subject, it.update)
                    }
                }
            )
            .addTo(compositeDisposable)
    }

    /**
     * Sets up the whole view and loads the needed data, but only if the data is not already displayed - in that
     * case the data is simply updated.
     *
     * @param subjectId the id of the subject to display.
     */
    fun setup(subjectId: Long) {
        if (currentSubjectId == subjectId) {
            return
        }

        currentSubject = null
        currentSubjectId = subjectId
        val ideaAdapter = IdeaAdapter(
            subjectId = subjectId,
            listener = ideaClickListener,
            add = addListener,
            remove = removeListener
        )
        adapter.postValue(ideaAdapter)

        title.postValue("")
        sumUnspent.postValue("")
        doneAmount.postValue("")
        date.postValue("")
        allAmount.postValue("")
        sumSpent.postValue("")
        remind.postValue(resourcesProvider.getString(R.string.app_general_empty_item))
        contactUri.postValue("")

        refresh()
    }

    private fun refresh() {
        showLoading()
        repository.findSubjectById(currentSubjectId)?.let { updateSubject(it, false) }
        hideLoading()
    }

    private fun updateSubject(subject: SubjectItem, isUpdate: Boolean = false) {
        currentSubject = subject
        title.postValue(subject.title)

        allAmount.postValue(resourcesProvider.getString(R.string.subject_detail_all, subject.ideasCount))
        doneAmount.postValue(
            resourcesProvider.getString(
                R.string.subject_detail_done_amount,
                subject.ideasDoneCount
            )
        )
        sumUnspent.postValue(
            resourcesProvider.getString(
                R.string.subject_details_sum_unspent,
                subject.sumUnspent.toEuro()
            )
        )
        sumSpent.postValue(
            resourcesProvider.getString(
                R.string.subject_details_sum_spent,
                subject.sumSpent.toEuro()
            )
        )
        if (subject.pushEnabled) {
            date.postValue(subject.date.toDate())
        } else {
            date.postValue(resourcesProvider.getString(R.string.subject_detail_no_remind))
        }
        remind.postValue(resourcesProvider.getString(R.string.subject_detail_date))

        currentColor = subject.color

        color.postValue(Color.parseColor(currentColor))

        contactUri.postValue(subject.contactUri)

        repository.getIdeasOf(currentSubjectId)

        if (isUpdate) {
            showSnack(
                snackType = Snacker.SnackType.SUCCESS,
                message = resourcesProvider.getString(R.string.subject_detail_updated)
            )
        }
    }

    private fun showLoading() {
        isLoading = true
        refresh.postValue(true)
    }

    private fun hideLoading() {
        refresh.postValue(false)
        isLoading = false
    }
}