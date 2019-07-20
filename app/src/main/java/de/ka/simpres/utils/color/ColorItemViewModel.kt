package de.ka.simpres.utils.color

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.utils.resources.ResourcesProvider
import org.koin.standalone.inject

class ColorItemViewModel(val colorString: String) : BaseItemViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val color = Color.parseColor(colorString)

    val markedColor =
        MutableLiveData<Int>().apply { value = (resourcesProvider.getColor(R.color.colorBackgroundSecondary)) }

    fun setMarked(marked: Boolean) {
        if (marked) {
            markedColor.postValue(resourcesProvider.getColor(R.color.colorBackgroundPrimaryDark))
        } else {
            markedColor.postValue(resourcesProvider.getColor(R.color.colorBackgroundSecondary))
        }
    }
}