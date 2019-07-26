package de.ka.simpres.ui.subjects.detail.idealist.newedit.comments

import android.view.View


class CommentsAddItemViewModel(private val add: () -> Unit) : CommentsBaseItemViewModel() {

    fun addNew(view: View){
        view.clearFocus()
        add()
    }

}