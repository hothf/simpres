package de.ka.simpres.ui.home

import android.view.View
import de.ka.simpres.base.BaseItemViewModel

class HomeItemViewModel(val item: HomeItem, val listener: (HomeItemViewModel, View) -> Unit) : BaseItemViewModel() {
}