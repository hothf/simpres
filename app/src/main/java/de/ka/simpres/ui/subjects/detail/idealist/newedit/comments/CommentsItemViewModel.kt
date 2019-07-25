package de.ka.simpres.ui.subjects.detail.idealist.newedit.comments

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.repo.model.Comment
import de.ka.simpres.utils.ViewUtils

class CommentsItemViewModel(val item: Comment) : CommentsBaseItemViewModel() {

    val text = MutableLiveData<String>().apply { value = item.text }
    val textSelection = MutableLiveData<Int>().apply { value = item.text.length }
    val openVisibility = MutableLiveData<Int>().apply { value = determineOpenVisibility(!item.text.isBlank()) }

    val getTextChangedListener = ViewUtils.TextChangeListener {
        text.value = it
        textSelection.value = it.length

        openVisibility.postValue(determineOpenVisibility(!it.isBlank()))
    }

    private fun determineOpenVisibility(isLink: Boolean): Int {
        if (isLink) {
            return View.VISIBLE
        }
        return View.INVISIBLE
    }


    fun save() {
        text.value?.let { item.text = it }
        openVisibility.value?.let { item.isLink = it == View.VISIBLE }
    }


}