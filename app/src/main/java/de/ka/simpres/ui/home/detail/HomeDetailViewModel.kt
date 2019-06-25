package de.ka.simpres.ui.home.detail

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.base.events.AnimType
import de.ka.simpres.ui.home.HomeItem
import de.ka.simpres.ui.home.detail.newedit.NewEditHomeDetailFragment
import de.ka.simpres.ui.home.newedit.NewEditHomeFragment
import de.ka.simpres.utils.AndroidSchedulerProvider
import de.ka.simpres.utils.DecorationUtil
import de.ka.simpres.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator

class HomeDetailViewModel(app: Application) : BaseViewModel(app) {

    private var currentItem: HomeItem? = null
    private var currentId = "-1"
    private var isLoading = false

    val adapter = MutableLiveData<HomeDetailAdapter>()
    val refresh = MutableLiveData<Boolean>().apply { value = false }
    val swipeToRefreshListener = SwipeRefreshLayout.OnRefreshListener { refreshDetails() }
    val itemDecoration = DecorationUtil(
        app.resources.getDimensionPixelSize(R.dimen.default_8), app.resources.getDimensionPixelSize(R.dimen.default_8)
    )

    fun itemAnimator() = SlideInDownAnimator()

    fun layoutManager() = LinearLayoutManager(app.applicationContext)

    fun onAddClick(){
        navigateTo(
            R.id.homeDetailNewEditFragment,
            args = Bundle().apply { putBoolean(NewEditHomeDetailFragment.NEW_KEY, true) },
            animType = AnimType.MODAL
        )
    }

    init {
        repository.observableHomeDetailItems
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onError = ::handleGeneralError,
                onNext = { result ->
                    if (result.invalidate) {
                        refreshDetails()
                    } else {
                        adapter.value?.let { it.overwriteList(result.list) }
                    }
                }
            )
            .addTo(compositeDisposable)

        repository.observableHomeItems
            .with(AndroidSchedulerProvider())
            .subscribeBy(
                onError = ::handleGeneralError,
                onNext = { it.list.find { home -> home.id == currentId }?.let(::updateItem) }
            )
            .addTo(compositeDisposable)

    }

    /**
     * Sets up the whole view and loads the needed data, but only if the data is not already displayed - in that
     * case the data is simply updated.
     *
     * @param owner the lifecycle owner, needed for keeping new data in sync with the lifecycle owner
     * @param id the id of the consensus to display.
     */
    fun setupAdapterAndLoad(owner: LifecycleOwner, id: String) {
        if (currentId == id) {
            currentItem?.let { updateItem(it) }
            return
        }

        currentId = id

        adapter.value = HomeDetailAdapter(owner = owner)

        // resets all current saved details, should be fairly impossible to get here without a deep link / wrong id
        currentItem = null

        refreshDetails()
    }

    fun refreshDetails() {
//        repository.consensusManager.getConsensusDetail(currentId)
//            .with(AndroidSchedulerProvider())
//            .subscribeRepoCompletion { onDetailsLoaded(it, fromLock = false) }
//            .start(compositeDisposable) {
//                showLoading()
//                showLockLoading()
//            }

        showLoading()
        repository.getHomeDetailItems()
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

    private fun updateItem(it: HomeItem) {
        val alreadyShowing = currentItem?.id == it.id

        currentItem = it

    }


}