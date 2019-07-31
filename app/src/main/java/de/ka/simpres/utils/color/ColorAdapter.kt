package de.ka.simpres.utils.color

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemColorBinding

/**
 * Adapter for displaying [ColorItemViewModel]s.
 */
class ColorAdapter(
    val click: (ColorItemViewModel) -> Unit, list: ArrayList<ColorItemViewModel> = arrayListOf()
) : BaseAdapter<ColorItemViewModel>(list, null) {

    init {
        setItems(ColorResources.indicatorColors.map { ColorItemViewModel(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ColorViewHolder(ItemColorBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        getItems()[position].apply {
            DataBindingUtil.getBinding<ItemColorBinding>(holder.itemView)?.let { binding ->
                binding.item.setOnClickListener {
                    markColor(this.colorString)
                    click(this)
                }
            }
        }

        super.onBindViewHolder(holder, position)
    }

    fun markColor(color: String?) {
        if (color == null) {
            return
        }
        getItems().forEach {
            it.setMarked(it.colorString == color)
        }
    }
}


class ColorViewHolder(private val binding: ItemColorBinding) : BaseViewHolder<ItemColorBinding>(binding) {
    override var swipeableView: View? = null
    override var isDraggable = false
    override var isSwipeable = false
}