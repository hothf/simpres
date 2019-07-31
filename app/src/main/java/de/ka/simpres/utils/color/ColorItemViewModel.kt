package de.ka.simpres.utils.color

import android.graphics.Color
import androidx.databinding.ObservableInt
import de.ka.simpres.R
import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.utils.resources.ResourcesProvider
import org.koin.standalone.inject

class ColorItemViewModel(val colorString: String) : BaseItemViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val color = Color.parseColor(colorString)

    val markedColor =
        ObservableInt().apply { set(resourcesProvider.getColor(R.color.colorBackgroundPrimary)) }

    fun setMarked(marked: Boolean) {
        if (marked) {
            markedColor.set(resourcesProvider.getColor(R.color.colorTextDefault))
        } else {
            markedColor.set(resourcesProvider.getColor(R.color.colorBackgroundPrimary))
        }
    }
}