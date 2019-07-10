package de.ka.simpres.ui.subjects.detail.idealist


import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemIdeaAddBinding
import de.ka.simpres.databinding.ItemIdeaBinding
import de.ka.simpres.repo.Repository
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.ui.subjects.detail.idealist.IdeaBaseItemViewModel.Companion.ADD_ID
import org.koin.standalone.inject

class IdeaAdapter(
    owner: LifecycleOwner,
    list: ArrayList<IdeaBaseItemViewModel> = arrayListOf(),
    val listener: (IdeaItem) -> Unit,
    val subjectId: Long
) :
    BaseAdapter<IdeaBaseItemViewModel>(
        owner, list,
        IdeaAdapterDiffCallback()
    ) {

    private val repository: Repository by inject()

    override fun getItemViewType(position: Int): Int {
        if (getItems()[position].id == ADD_ID) {
            return 2
        }

        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        if (viewType == 2) {
            return BaseViewHolder(ItemIdeaAddBinding.inflate(layoutInflater, parent, false), false)
        }

        return BaseViewHolder(ItemIdeaBinding.inflate(layoutInflater, parent, false), true)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val viewModel = getItems()[position] as? IdeaItemViewModel

        viewModel?.let {
            DataBindingUtil.getBinding<ItemIdeaBinding>(holder.itemView)?.let { binding ->
                binding.check.setOnCheckedChangeListener { _, checked ->
                    viewModel.item.done = checked
                    repository.saveOrUpdateIdea(subjectId, viewModel.item)
                }
                binding.item.setOnClickListener {
                    listener(viewModel.item)
                }
            }
        }

        super.onBindViewHolder(holder, position)
    }

    override fun onItemDismiss(position: Int) {
        val viewModel = getItems()[position] as? IdeaItemViewModel

        viewModel?.let {
            repository.removeIdea(subjectId, viewModel.item)
        }

        super.onItemDismiss(position)
    }

    /**
     * Overwrites the current list with the given [newItems] and applies a [itemClickListener] to them.
     *
     * @param newItems the new items to append or replace
     * @param itemClickListener a click listener for individual items
     */
    fun overwriteList(
        newItems: List<IdeaItem>
    ) {
        val newList: MutableList<IdeaBaseItemViewModel> =
            newItems.map { detail -> IdeaItemViewModel(detail) }.toMutableList()
        newList.add(0, IdeaAddItemViewModel())
        setItems(newList)
    }
}

class IdeaAdapterDiffCallback : DiffUtil.ItemCallback<IdeaBaseItemViewModel>() {

    override fun areItemsTheSame(oldItem: IdeaBaseItemViewModel, newItem: IdeaBaseItemViewModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: IdeaBaseItemViewModel,
        newItem: IdeaBaseItemViewModel
    ): Boolean {
        return oldItem == newItem
    }

}