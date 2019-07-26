package de.ka.simpres.ui.subjects.detail.idealist.newedit.comments

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.repo.model.Comment
import de.ka.simpres.utils.ViewUtils

class CommentsItemViewModel(
    val item: Comment,
    private val open: (Comment) -> Unit
) : CommentsBaseItemViewModel() {

    val text = MutableLiveData<String>().apply { value = item.text }
    val textSelection = MutableLiveData<Int>().apply { value = item.text.length }
    val openVisibility = MutableLiveData<Int>().apply { value = determineOpenVisibility(!item.text.isBlank()) }

    fun open() {
        open(item)
    }

    val getTextChangedListener = ViewUtils.TextChangeListener {
        text.value = it
        textSelection.value = it.length

        openVisibility.postValue(determineOpenVisibility(!it.isBlank()))

        text.value?.let { value -> item.text = value }
        openVisibility.value?.let { value -> item.isLink = value == View.VISIBLE }
    }

    private fun determineOpenVisibility(isLink: Boolean): Int {
        if (isLink) {
            return View.VISIBLE
        }
        return View.INVISIBLE
    }
}