package de.ka.simpres.ui.subjects

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.ui.subjects.detail.SubjectsDetailFragment
import de.ka.simpres.ui.subjects.subjectlist.SubjectAdapter
import de.ka.simpres.ui.subjects.subjectlist.SubjectItemViewModel
import de.ka.simpres.ui.subjects.subjectlist.newedit.NewEditSubjectFragment
import de.ka.simpres.utils.*
import de.ka.simpres.utils.resources.ResourcesProvider
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import org.koin.standalone.inject

/**
 * The main view model for subjects. Handles the showing, deletion and moving of the list of all available subjects.
 */
class SubjectsViewModel : BaseViewModel() {

    private var isLoading: Boolean = false

    private val resourcesProvider: ResourcesProvider by inject()

    val touchHelper = MutableLiveData<ItemTouchHelper>()
    val blankVisibility = MutableLiveData<Int>().apply { value = View.GONE }
    val adapter = MutableLiveData<SubjectAdapter>()
    val refresh = MutableLiveData<Boolean>().apply { value = false }
    val swipeToRefreshListener = SwipeRefreshLayout.OnRefreshListener { loadSubjects(true) }
    val itemDecoration = DecorationUtil(
        resourcesProvider.getDimensionPixelSize(R.dimen.default_16),
        resourcesProvider.getDimensionPixelSize(R.dimen.default_16),
        COLUMNS_COUNT
    )
    private val itemClickListener = { vm: SubjectItemViewModel, view: View ->
        navigateTo(
            R.id.action_subjectsFragment_to_subjectsDetailFragment,
            false,
            Bundle().apply { putLong(SubjectsDetailFragment.SUBJECT_ID_KEY, vm.item.id) },
            null,
            FragmentNavigatorExtras(view to view.transitionName)
        )
    }
    private val removeListener = { _: SubjectItemViewModel ->
        showSnack(
            resourcesProvider.getString(R.string.subject_delete_undo_title),
            Snacker.SnackType.DEFAULT,
            { repository.undoDeleteSubject() },
            resourcesProvider.getString(R.string.subject_delete_undo_action)
        )
    }

    fun layoutManager() = GridLayoutManager(resourcesProvider.getApplicationContext(), COLUMNS_COUNT)

    fun itemAnimator() = SlideInDownAnimator()

    init {
        startObserving()
    }

    fun onAddClick() {
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
            val subjectAdapter = SubjectAdapter(owner)
            adapter.value = subjectAdapter
            loadSubjects(true)
        }
        adapter.value?.let {
            touchHelper.apply {
                value?.attachToRecyclerView(null)
                value = null
                postValue(ItemTouchHelper(DragAndSwipeItemTouchHelperCallback(it)))
            }
        }
    }

    private fun startObserving() {
        repository.observableSubjects
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onNext = { result ->
                    hideLoading()
                    adapter.value?.let {
                        it.overwriteList(result, itemClickListener, removeListener)

                        if (it.isEmpty) {
                            blankVisibility.postValue(View.VISIBLE)
                        } else {
                            blankVisibility.postValue(View.GONE)
                        }
                    }
                }, onError = { throwable ->
                    hideLoading()
                    handleGeneralError(throwable)
                }
            )
            .addTo(compositeDisposable)
    }

    /**
     * Loads all items.
     * Calling this with [reset] set to true will immediately cancel all requests and try to fetch from start.
     *
     * @param reset set to true to reset the current state and force a fresh reload
     */
    private fun loadSubjects(reset: Boolean) {
        if (reset) {
            isLoading = false
            compositeDisposable.clear()
            startObserving()
        }

        if (isLoading) {
            return
        }

        showLoading()

        repository.getSubjects(reset)
    }

    private fun hideLoading() {
        refresh.postValue(false)
        isLoading = false
    }

    private fun showLoading() {
        isLoading = true
        refresh.postValue(true)
    }

    companion object {
        const val COLUMNS_COUNT = 2
    }
}
