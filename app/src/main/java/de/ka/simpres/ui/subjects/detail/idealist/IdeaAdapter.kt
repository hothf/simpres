package de.ka.simpres.ui.subjects.detail.idealist


import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemIdeaBinding
import de.ka.simpres.repo.model.IdeaItem

class IdeaAdapter(owner: LifecycleOwner, list: ArrayList<IdeaItemViewModel> = arrayListOf()) :
    BaseAdapter<IdeaItemViewModel>(owner, list,
        IdeaAdapterDiffCallback()
    ) {

    private var dispose: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return BaseViewHolder(ItemIdeaBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        getItems()[position].apply {
            DataBindingUtil.getBinding<ItemIdeaBinding>(holder.itemView)?.let { binding ->
                val sharedTransitionView = binding.itemContainer
                ViewCompat.setTransitionName(sharedTransitionView, this.item.id.toString())
                binding.itemContainer.setOnClickListener {
//                    listener(this, sharedTransitionView)
                }
            }
        }

        super.onBindViewHolder(holder, position)
    }

    /**
     * Marks the list items for disposition. Only useful with lists that will be dynamically changed and if this adapter
     * is reused.
     *
     * When the list is marked for disposition, the next call to [removeAddOrUpdate] will not update items, as they are
     * disposed, leading to removing all items passed as parameter or removing all items.
     * This is useful, if the adapter should be aware of a reset of all items but should not immediately remove all
     * items to allow for a diff on the next [removeAddOrUpdate] call.
     */
    fun markForDisposition() {
        dispose = true
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
        setItems(newItems.map { detail -> IdeaItemViewModel(detail) })
    }

    /**
     * Removes the specified [updatedItems] or inserts them or updates a part of it, depending on the flags given
     * as parameters in this method and applies a [itemClickListener] if possible.
     * Returns eventually added and removed items count summed up. Does not include a item updated count.
     *
     * **Note that if a prior call to [markForDisposition] has been made, this will remove all items of the list or add
     * the items, regardless of update intentions, as the previously added items are all  marked for disposition.
     * The marking itself is removed afterwards.**
     *
     * @param updatedItems the updated items to either remove, add, update or mix
     * @param itemClickListener a item click listener to apply
     * @param remove the flag to indicate that items should be removed
     * @param onlyUpdate a flag to indicate that only updates should be made and no items should be added
     * @param addToTop a flag indicating if adding a new item, it is added to the top of the list
     * @param filter a optional filter for removing unwanted items
     * @return the items removed and added count, updated items are not counted
     */
    fun removeAddOrUpdate(
        updatedItems: List<IdeaItem>,
        remove: Boolean,
        onlyUpdate: Boolean,
        addToTop: Boolean,
        filter: ((IdeaItem) -> Boolean)? = null
    ): Int {
        val items: MutableList<IdeaItemViewModel> = getItems().toMutableList()
        var itemsRemovedAndAddedCount = 0

        if (dispose) {
            items.clear()
            dispose = false
        }

        updatedItems
            .forEach { item ->
                val foundIndex = items.indexOfFirst { it.item.id == item.id }
                if (foundIndex > -1 && items.isNotEmpty()) {
                    if (remove || (filter != null && !filter(item))) {          // remove
                        items.removeAt(foundIndex)
                        itemsRemovedAndAddedCount--
                    } else {                                                    // update
                        items[foundIndex] = IdeaItemViewModel(item)
                    }
                } else if ((!onlyUpdate && filter == null) || (filter != null && filter(item))) {   // add
                    itemsRemovedAndAddedCount++
                    if (addToTop) {
                        items.add(0, IdeaItemViewModel(item))
                    } else {
                        items.add(IdeaItemViewModel(item))
                    }
                }
            }

        setItems(items.toList())

        return itemsRemovedAndAddedCount
    }
}

class IdeaAdapterDiffCallback : DiffUtil.ItemCallback<IdeaItemViewModel>() {

    override fun areItemsTheSame(oldItem: IdeaItemViewModel, newItem: IdeaItemViewModel): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(
        oldItem: IdeaItemViewModel,
        newItem: IdeaItemViewModel
    ): Boolean {
        return oldItem == newItem
    }

}