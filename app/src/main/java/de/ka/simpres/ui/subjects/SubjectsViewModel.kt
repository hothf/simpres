package de.ka.simpres.ui.subjects

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.ui.subjects.subjectlist.SubjectAdapter
import de.ka.simpres.ui.subjects.subjectlist.SubjectItemViewModel
import de.ka.simpres.ui.subjects.subjectlist.newedit.NewEditSubjectFragment
import de.ka.simpres.utils.AndroidSchedulerProvider
import de.ka.simpres.utils.DecorationUtil
import de.ka.simpres.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator

/**
 *
 */
class SubjectsViewModel(app: Application) : BaseViewModel(app){

    private var isLoading: Boolean = false

    val adapter = MutableLiveData<SubjectAdapter>()
    val refresh = MutableLiveData<Boolean>().apply { value = false }
    val blankVisibility = MutableLiveData<Int>().apply { value = View.GONE }
    val swipeToRefreshListener = SwipeRefreshLayout.OnRefreshListener { loadSubjects(true) }
    val itemDecoration = DecorationUtil(app.resources.getDimensionPixelSize(R.dimen.default_16), app.resources
        .getDimensionPixelSize(R.dimen.default_8))
    private val itemClickListener = { vm: SubjectItemViewModel, view: View ->
        navigateTo(
            R.id.action_subjectsFragment_to_subjectsDetailFragment,
            false,
            Bundle().apply { putString(SubjectsDetailFragment.SUBJECT_ID_KEY, vm.item.id) },
            null,
            FragmentNavigatorExtras(view to view.transitionName)
        )
    }

    fun layoutManager() = LinearLayoutManager(app.applicationContext)

    fun itemAnimator() = SlideInDownAnimator()

    init {
        startObserving()
    }

    fun onAddClick(){
        navigateTo(
            R.id.subjectNewEditFragment,
            args = Bundle().apply { putBoolean(NewEditSubjectFragment.NEW_KEY, true) },
            animType = AnimType.MODAL
        )
    }

    /**
     * Sets up the view, if not already done.
     *
     * @param owner the lifecycle owner to keep the data in sync with the lifecycle
     */
    fun setupAdapterAndLoad(owner: LifecycleOwner) {
        if (adapter.value == null) {
            adapter.postValue(SubjectAdapter(owner))
            loadSubjects(true)
        }
    }

    private fun startObserving(){
        repository.observableSubjects
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onNext = { result ->
                    adapter.value?.let {
                        val updateOnly = if (result.isFiltered) true else result.update

                        val removedOrAddedCount = it.removeAddOrUpdate(
                            result.list,
                            itemClickListener,
                            result.remove,
                            updateOnly,
                            result.addToTop
                        )
                        if (it.isEmpty) {
                            blankVisibility.postValue(View.VISIBLE)
                        } else {
                            blankVisibility.postValue(View.GONE)
                        }
                    }
                }, onError = ::handleGeneralError
            )
            .addTo(compositeDisposable)
    }

    /**
     * Loads all home items. This will be a paginated process, as long as [reset] is set to false.
     * Calling this with [reset] set to true will immediately cancel all requests and try to fetch from start.
     *
     * @param reset set to true to reset the current state of consensus pagination loading and force a fresh reload
     */
    private fun loadSubjects(reset: Boolean) {
        if (reset) {
            isLoading = false
            compositeDisposable.clear()
            adapter.value?.markForDisposition()
            startObserving()
        }

        if (isLoading) {
            return
        }

        showLoading()
        repository.getSubjects()
        hideLoading()
    }

    private fun hideLoading() {
        refresh.postValue(false)
        isLoading = false
    }

    private fun showLoading() {
        isLoading = true
        refresh.postValue(true)
    }
}
