package de.ka.simpres.ui.subjects.detail

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.ui.subjects.detail.idealist.IdeaAdapter
import de.ka.simpres.ui.subjects.detail.idealist.newedit.NewEditIdeaFragment
import de.ka.simpres.utils.AndroidSchedulerProvider
import de.ka.simpres.utils.DecorationUtil
import de.ka.simpres.utils.NavigationUtils.BACK
import de.ka.simpres.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator

class SubjectsDetailViewModel(app: Application) : BaseViewModel(app) {

    private var currentSubjectId = "-1"
    private var isLoading = false

    val adapter = MutableLiveData<IdeaAdapter>()
    val refresh = MutableLiveData<Boolean>().apply { value = false }
    val swipeToRefreshListener = SwipeRefreshLayout.OnRefreshListener { refresh() }
    val itemDecoration = DecorationUtil(
        app.resources.getDimensionPixelSize(R.dimen.default_8), app.resources.getDimensionPixelSize(R.dimen.default_8)
    )
    val title = MutableLiveData<String>()

    fun itemAnimator() = SlideInDownAnimator()

    fun layoutManager() = LinearLayoutManager(app.applicationContext)

    fun onBack( )= navigateTo(BACK)

    fun onAddClick() {
        navigateTo(
            R.id.ideaNewEditFragment,
            args = Bundle().apply {
                putString(NewEditIdeaFragment.SUBJECT_ID_KEY, currentSubjectId)
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
                        adapter.value?.let { it.overwriteList(result.list) }
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
    fun setupAdapterAndLoad(owner: LifecycleOwner, subjectId: String) {
        if (currentSubjectId == subjectId) {
            return
        }

        currentSubjectId = subjectId

        // resets all current saved details, should be fairly impossible to get here without a deep link / wrong id
        adapter.value = IdeaAdapter(owner = owner)
        title.postValue("")

        refresh()
    }

    private fun refresh() {
        showLoading()
        repository.getSubject(currentSubjectId)
        repository.getIdeasOf(currentSubjectId)
        hideLoading()
    }

    private fun updateSubject(subject: SubjectItem) {
        title.postValue(subject.title)
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