package de.ka.simpres.ui.subjects.subjectlist

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.R
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemSubjectBinding
import de.ka.simpres.repo.Repository
import de.ka.simpres.repo.model.SubjectItem
import org.koin.standalone.inject
import kotlin.math.abs

/**
 * Adapter for displaying [SubjectItemViewModel]s.
 */
class SubjectAdapter(owner: LifecycleOwner, list: ArrayList<SubjectItemViewModel> = arrayListOf()) :
    BaseAdapter<SubjectItemViewModel>(owner, list, SubjectAdapterDiffCallback()) {

    private val repository: Repository by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return SubjectViewHolder(ItemSubjectBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        getItems()[position].apply {
            DataBindingUtil.getBinding<ItemSubjectBinding>(holder.itemView)?.let { binding ->
                val sharedTransitionView = binding.item
                ViewCompat.setTransitionName(sharedTransitionView, this.item.id.toString())
                binding.item.setOnClickListener {
                    listener(this, sharedTransitionView)
                }
            }
        }

        super.onBindViewHolder(holder, position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        repository.moveSubject(
            getItems()[fromPosition].item,
            getItems()[toPosition].item,
            fromPosition,
            toPosition
        )
        return super.onItemMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        repository.removeSubject(getItems()[position].item)
        super.onItemDismiss(position)
    }

    /**
     * Overwrites the current list with the given [newItems].
     *
     * @param newItems the new items to append, update or replace
     */
    fun overwriteList(
        newItems: List<SubjectItem>,
        listener: (SubjectItemViewModel, View) -> Unit
    ) {
        val newList: MutableList<SubjectItemViewModel> =
            newItems.map { SubjectItemViewModel(it, listener) }.toMutableList()
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

    override var swipeableView: View? = binding.item
    override var isDraggable = true
    override var isSwipeable = true

    override fun onHolderDrag() {
        binding.item.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorBackgroundPrimary))

        super.onHolderDrag()
    }

    override fun onHolderClear() {
        binding.deleteIcon.alpha = 0.0f
        swipeableView?.alpha = 1.0f

        binding.item.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorBackgroundSecondary))

        super.onHolderClear()
    }

    override fun onHolderSwipe(dX: Float, dY: Float, actionState: Int) {
        swipeableView?.let {
            val change = abs(dX) / it.width
            binding.deleteIcon.alpha = 0.0f + change
            binding.item.alpha = 1.0f - change
        }

        super.onHolderSwipe(dX, dY, actionState)
    }
}