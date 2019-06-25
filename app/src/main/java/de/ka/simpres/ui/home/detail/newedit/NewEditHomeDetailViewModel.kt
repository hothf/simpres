package de.ka.simpres.ui.home.detail.newedit

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseViewModel
import de.ka.simpres.ui.home.HomeItem
import de.ka.simpres.ui.home.detail.HomeDetailFragment
import de.ka.simpres.ui.home.detail.HomeDetailItem
import de.ka.simpres.utils.ViewUtils

class NewEditHomeDetailViewModel(app: Application) : BaseViewModel(app) {

    val getTextChangedListener = ViewUtils.TextChangeListener {
        title.value = it
        titleError.postValue("")

        currentHomeDetailItem?.title = it
    }
    val getDoneListener = ViewUtils.TextDoneListener { submit() }
    val title = MutableLiveData<String>().apply { value = "" }
    val titleError = MutableLiveData<String>().apply { value = "" }
    val titleSelection = MutableLiveData<Int>().apply { value = 0 }

    private var currentHomeDetailItem: HomeDetailItem? = null

    fun submit() {

        currentHomeDetailItem?.let {


            repository.saveHomeDetailItem(it)
        }


        val id = System.currentTimeMillis()
        navigateTo(
            navigationTargetId = R.id.action_homeDetailNewEditFragment_to_homeDetailFragment,
            args = Bundle().apply { putString(HomeDetailFragment.HOME_ID_KEY, id.toString()) },
            popupToId = R.id.homeDetailFragment
        )
    }

    /**
     *
     */
    fun setupNew() {
        currentHomeDetailItem = HomeDetailItem()
//        currentTitle = ""

//        header.postValue(app.getString(R.string.suggestions_newedit_title))
//        saveDrawableRes.postValue(R.drawable.ic_small_add)

        updateTextViews()
    }

    /**
     *
     */
    fun setupEdit(homeItem: HomeDetailItem) {
        currentHomeDetailItem = homeItem
//        currentTitle = suggestion.title

//        header.postValue(app.getString(R.string.suggestions_newedit_edit))
//        saveDrawableRes.postValue(R.drawable.ic_small_done)

        updateTextViews()
    }

    private fun updateTextViews() {
        if (currentHomeDetailItem != null) {
            title.postValue(currentHomeDetailItem?.title)
            titleSelection.postValue(currentHomeDetailItem?.title?.length)
            titleError.postValue("")
        } else {
            title.postValue("")
            titleSelection.postValue(0)
            titleError.postValue("")
        }

    }

}