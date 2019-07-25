package de.ka.simpres.ui.subjects.detail.idealist

import android.graphics.Color

class IdeaAddItemViewModel(color: String) : IdeaBaseItemViewModel() {
    override val id = -999

    val color = Color.parseColor(color)
}