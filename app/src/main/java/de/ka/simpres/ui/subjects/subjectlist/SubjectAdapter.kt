package de.ka.simpres.ui.subjects.subjectlist

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemSubjectBinding
import de.ka.simpres.repo.model.SubjectItem
import kotlin.math.abs
import kotlin.math.min

/**
 * Adapter for displaying [SubjectItemViewModel]s.
 */
class SubjectAdapter(
    owner: LifecycleOwner,
    list: ArrayList<SubjectItemViewModel> = arrayListOf(),
    val click: (SubjectItemViewModel, View) -> Unit,
    val remove: (SubjectItemViewModel) -> Unit,
    val move: (
        fromPosition: Int,
        toPosition: Int,
        viewModel1: SubjectItemViewModel,
        viewModel2: SubjectItemViewModel
    ) -> Unit
) :
    BaseAdapter<SubjectItemViewModel>(owner, list, SubjectAdapterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return SubjectViewHolder(ItemSubjectBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        getItems()[position].apply {
            DataBindingUtil.getBinding<ItemSubjectBinding>(holder.itemView)?.let { binding ->
                val sharedTransitionView = binding.item
                ViewCompat.setTransitionName(sharedTransitionView, this.item.id.toString())
            }
        }

        super.onBindViewHolder(holder, position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        move(fromPosition, toPosition, getItems()[fromPosition], getItems()[toPosition])
        return super.onItemMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        getItems()[position].also(remove)
        super.onItemDismiss(position)
    }

    /**
     * Overwrites the current list with the given [newItems].
     *
     * @param newItems the new items to append, update or replace
     * @param click a click listener
     * @param remove a listener for a request to remove the item
     */
    fun overwriteList(newItems: List<SubjectItem>) {
        val newList: MutableList<SubjectItemViewModel> =
            newItems.map { SubjectItemViewModel(it, click, remove) }.toMutableList()
        setItems(newList)
    }
}

class SubjectAdapterDiffCallback : DiffUtil.ItemCallback<SubjectItemViewModel>() {

    override fun areItemsTheSame(oldItem: SubjectItemViewModel, newItem: SubjectItemViewModel): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(
        oldItem: SubjectItemViewModel,
        newItem: SubjectItemViewModel
    ): Boolean {
        return oldItem.item == newItem.item
    }
}

class SubjectViewHolder<T : ItemSubjectBinding>(private val binding: T) : BaseViewHolder<T>(binding) {

    init {
        binding.deleteIcon.alpha = 0.0f
    }

    override var swipeableView: View? = binding.item
    override var isDraggable = true
    override var isSwipeable = true

    override fun onHolderClear() {
        binding.deleteIcon.alpha = 0.0f
        binding.deleteIcon.scaleX = 0.0f
        binding.deleteIcon.scaleY = 0.0f
        swipeableView?.alpha = 1.0f

        super.onHolderClear()
    }

    override fun onHolderSwipe(dX: Float, dY: Float, actionState: Int) {
        swipeableView?.let {
            val change = abs(dX) / it.width
            binding.deleteIcon.alpha = 0.0f + change
            binding.deleteIcon.scaleX = min(0.0f + change, 1.0f)
            binding.deleteIcon.scaleY = min(0.0f + change, 1.0f)
            binding.item.alpha = 1.0f - change
        }

        super.onHolderSwipe(dX, dY, actionState)
    }
}