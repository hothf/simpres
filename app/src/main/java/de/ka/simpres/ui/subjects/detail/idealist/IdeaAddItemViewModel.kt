package de.ka.simpres.ui.subjects.detail.idealist

import android.graphics.Color

class IdeaAddItemViewModel(color: String, val add: () -> Unit) : IdeaBaseItemViewModel() {
    override val id = -999

    fun onAdd() {
        add()
    }

    val color = Color.parseColor(color)
}