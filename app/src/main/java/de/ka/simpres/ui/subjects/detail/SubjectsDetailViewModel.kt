package de.ka.simpres.ui.subjects.detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.idealist.IdeaAdapter
import de.ka.simpres.ui.subjects.detail.idealist.newedit.NewEditIdeaFragment
import de.ka.simpres.ui.subjects.subjectlist.newedit.NewEditSubjectFragment
import de.ka.simpres.utils.AndroidSchedulerProvider
import de.ka.simpres.utils.DecorationUtil
import de.ka.simpres.utils.DragAndSwipeItemTouchHelperCallback
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.resources.ResourcesProvider
import de.ka.simpres.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import org.koin.standalone.inject

class SubjectsDetailViewModel : BaseViewModel() {

    private var currentSubjectId = -1L
    private var isLoading = false

    private val resourcesProvider: ResourcesProvider by inject()

    val touchHelper = MutableLiveData<ItemTouchHelper>()
    val adapter = MutableLiveData<IdeaAdapter>()
    val refresh = MutableLiveData<Boolean>().apply { value = false }
    val swipeToRefreshListener = SwipeRefreshLayout.OnRefreshListener { refresh() }
    val itemDecoration = DecorationUtil(
        resourcesProvider.getDimensionPixelSize(R.dimen.default_16), resourcesProvider.getDimensionPixelSize(
            R.dimen
                .default_16
        )
    )
    val blankVisibility = MutableLiveData<Int>().apply { value = View.GONE }
    val title = MutableLiveData<String>()
    val sum = MutableLiveData<String>().apply { value = "" }

    fun itemAnimator() = SlideInDownAnimator()

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun onBack() = navigateTo(BACK)

    fun onEdit(){
        navigateTo(
            R.id.action_subjectsDetailFragment_to_subjectNewEditFragment,
            false,
            Bundle().apply { putLong(NewEditSubjectFragment.SUBJECT_ID_KEY, currentSubjectId) },
            animType = AnimType.MODAL
        )
    }

    fun onAddClick() {
        navigateTo(
            R.id.ideaNewEditFragment,
            args = Bundle().apply {
                putLong(NewEditIdeaFragment.SUBJECT_ID_KEY, currentSubjectId)
                putBoolean(NewEditIdeaFragment.NEW_KEY, true)
            },
            animType = AnimType.MODAL
        )
    }

    init {
        repository.observableIdeas
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onError = ::handleGeneralError,
                onNext = { result ->
                    if (result.invalidate) {
                        refresh()
                    } else {
                        adapter.value?.let {
                            it.overwriteList(result.list)

                            if (it.isEmpty) {
                                blankVisibility.postValue(View.VISIBLE)
                            } else {
                                blankVisibility.postValue(View.GONE)
                            }

                        }
                    }
                }
            )
            .addTo(compositeDisposable)

        repository.observableSubjects
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onError = ::handleGeneralError,
                onNext = { it.list.find { subject -> subject.id == currentSubjectId }?.let(::updateSubject) }
            )
            .addTo(compositeDisposable)
    }

    /**
     * Sets up the whole view and loads the needed data, but only if the data is not already displayed - in that
     * case the data is simply updated.
     *
     * @param owner the lifecycle owner, needed for keeping new data in sync with the lifecycle owner
     * @param subjectId the id of the subject to display.
     */
    fun setupAdapterAndLoad(owner: LifecycleOwner, subjectId: Long) {
        if (currentSubjectId == subjectId) {
            return
        }

        currentSubjectId = subjectId

        // resets all current saved details, should be fairly impossible to get here without a deep link / wrong id
        adapter.value = IdeaAdapter(owner = owner, subjectId = subjectId, listener = ::onIdeaClick)
        adapter.value?.apply {
            touchHelper.value = ItemTouchHelper(DragAndSwipeItemTouchHelperCallback(this))
        }
        title.postValue("")
        sum.postValue("0")

        refresh()
    }

    private fun onIdeaClick(ideaItem: IdeaItem) {
        navigateTo(
            R.id.action_subjectsDetailFragment_to_ideaNewEditFragment,
            false,
            Bundle().apply { putSerializable(NewEditIdeaFragment.IDEA_KEY, ideaItem) },
            animType = AnimType.MODAL
        )
    }

    private fun refresh() {
        showLoading()
        repository.getSubject(currentSubjectId)
        repository.getIdeasOf(currentSubjectId)
        hideLoading()
    }

    private fun updateSubject(subject: SubjectItem) {
        title.postValue(subject.title)
        sum.postValue(subject.sum)
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