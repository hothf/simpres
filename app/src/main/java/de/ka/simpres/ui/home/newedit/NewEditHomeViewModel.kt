package de.ka.simpres.ui.home.newedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.ui.home.HomeItem
import de.ka.simpres.ui.home.detail.HomeDetailFragment
import de.ka.simpres.utils.ViewUtils

class NewEditHomeViewModel(app: Application) : BaseViewModel(app) {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")

        currentHomeItem?.title = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { submit() }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }

    private var currentHomeItem: HomeItem? = null

    fun submit() {

        currentHomeItem?.let {

            it.id = System.currentTimeMillis().toString()

            repository.saveHomeItem(it)

            navigateTo(
                navigationTargetId = R.id.action_homeNewEditFragment_to_homeDetailFragment,
                args = Bundle().apply { putString(HomeDetailFragment.HOME_ID_KEY, it.id) },
                popupToId = R.id.homeNewEditFragment
            )
        }



    }

    /**
     *
     */
    fun setupNew() {
        currentHomeItem = HomeItem()
//        currentTitle = ""

//        header.postValue(app.getString(R.string.suggestions_newedit_title))
//        saveDrawableRes.postValue(R.drawable.ic_small_add)

        updateTextViews()
    }

    /**
     *
     */
    fun setupEdit(homeItem: HomeItem) {
        currentHomeItem = homeItem
//        currentTitle = suggestion.title

//        header.postValue(app.getString(R.string.suggestions_newedit_edit))
//        saveDrawableRes.postValue(R.drawable.ic_small_done)

        updateTextViews()
    }

    private fun updateTextViews() {
        if (currentHomeItem != null) {
            title.postValue(currentHomeItem?.title)
            titleSelection.postValue(currentHomeItem?.title?.length)
            titleError.postValue("")
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
        }

    }

}