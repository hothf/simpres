package de.ka.simpres.ui.subjects.detail.idealist


import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemIdeaAddBinding
import de.ka.simpres.databinding.ItemIdeaBinding
import de.ka.simpres.repo.model.IdeaItem
import kotlin.math.abs
import kotlin.math.min

class IdeaAdapter(
    owner: LifecycleOwner,
    list: ArrayList<IdeaBaseItemViewModel> = arrayListOf(),
    val listener: (IdeaItem) -> Unit,
    val add: () -> Unit,
    val subjectId: Long,
    val remove: (IdeaItemViewModel) -> Unit
) : BaseAdapter<IdeaBaseItemViewModel>(owner, list, IdeaAdapterDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        if (getItems()[position] is IdeaAddItemViewModel) {
            return 2
        }

        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == 2) {
            AddIdeaViewHolder(ItemIdeaAddBinding.inflate(layoutInflater, parent, false))
        } else {
            IdeaViewHolder(ItemIdeaBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onItemDismiss(position: Int) {
        val viewModel = getItems()[position] as? IdeaItemViewModel

        viewModel?.let {
            remove(it)
        }

        super.onItemDismiss(position)
    }

    /**
     * Overwrites the current list with the given [newItems].
     *
     * @param newItems the new items to append or replace
     */
    fun overwriteList(
        newItems: List<IdeaItem>,
        color: String
    ) {
        var indexOfFirstDone = -1
        val newList: MutableList<IdeaBaseItemViewModel> =
            newItems.mapIndexed { index, detail ->
                if (detail.done && indexOfFirstDone == -1) {
                    indexOfFirstDone = index
                }
                IdeaItemViewModel(detail, color, listener)
            }.toMutableList()

        //newList.add(0,IdeaAddItemViewModel()) // used if it should be always on top!

        if (indexOfFirstDone != -1) {
            newList.add(indexOfFirstDone, IdeaAddItemViewModel(color, add))
        } else {
            newList.add(IdeaAddItemViewModel(color, add))
        }
        setItems(newList)
    }
}

class IdeaAdapterDiffCallback : DiffUtil.ItemCallback<IdeaBaseItemViewModel>() {

    override fun areItemsTheSame(oldItem: IdeaBaseItemViewModel, newItem: IdeaBaseItemViewModel): Boolean {
        if (oldItem.id == newItem.id) return true
        return false
    }

    override fun areContentsTheSame(
        oldItem: IdeaBaseItemViewModel,
        newItem: IdeaBaseItemViewModel
    ): Boolean {
        return false
    }
}

class IdeaViewHolder<T : ItemIdeaBinding>(val binding: T) : BaseViewHolder<T>(binding) {
    override var swipeableView: View? = binding.swipeAble
    override var isDraggable = false
    override var isSwipeable = true

    override fun onHolderClear() {
        binding.deleteIconLeft.alpha = 0.0f
        binding.deleteIconLeft.scaleX = 0.0f
        binding.deleteIconLeft.scaleY = 0.0f
        binding.deleteIconRight.alpha = 0.0f
        binding.deleteIconRight.scaleX = 0.0f
        binding.deleteIconRight.scaleY = 0.0f
        super.onHolderClear()
    }

    override fun onHolderSwipe(dX: Float, dY: Float, actionState: Int) {
        swipeableView?.let {
            val change = abs(dX) / it.width
            if (dX > 0) {
                binding.deleteIconRight.alpha = 0.0f
                binding.deleteIconLeft.alpha = 0.5f + change
                binding.deleteIconLeft.scaleX = min(0.5f + change, 1.0f)
                binding.deleteIconLeft.scaleY = min(0.5f + change, 1.0f)
            } else {
                binding.deleteIconLeft.alpha = 0.0f
                binding.deleteIconRight.alpha = 0.5f + change
                binding.deleteIconRight.scaleX = min(0.5f + change, 1.0f)
                binding.deleteIconRight.scaleY = min(0.5f + change, 1.0f)
            }
        }

        super.onHolderSwipe(dX, dY, actionState)
    }
}

class AddIdeaViewHolder<T : ViewDataBinding>(binding: T) : BaseViewHolder<T>(binding) {
    override var swipeableView: View? = null
    override var isDraggable = false
    override var isSwipeable = false
}