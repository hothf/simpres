package de.ka.simpres.ui.home

import android.view.View
import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.utils.toDate

class HomeItemViewModel(val item: HomeItem, val listener: (HomeItemViewModel, View) -> Unit) : BaseItemViewModel() {

    val title = item.title

    val sum = item.sum.toString()

    val date = item.date.toDate()
}