package de.ka.simpres.ui.subjects.detail.idealist.newedit.comments

import androidx.lifecycle.MutableLiveData
import de.ka.simpres.repo.model.Comment
import de.ka.simpres.utils.ViewUtils

class CommentsItemViewModel(val item: Comment) : CommentsBaseItemViewModel() {

    val text = MutableLiveData<String>().apply { value = item.text }
    val textSelection = MutableLiveData<Int>().apply { value = item.text.length }

    val getTextChangedListener = ViewUtils.TextChangeListener {
        text.value = it
        textSelection.value = it.length
    }


    fun save() {
        text.value?.let { item.text = it }
    }


}