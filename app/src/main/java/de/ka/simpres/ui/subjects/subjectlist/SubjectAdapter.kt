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
import de.ka.simpres.repo.Repository
import de.ka.simpres.repo.model.SubjectItem
import org.koin.standalone.inject

/**
 * Adapter for displaying [SubjectItemViewModel]s.
 */
class SubjectAdapter(owner: LifecycleOwner, list: ArrayList<SubjectItemViewModel> = arrayListOf()) :
    BaseAdapter<SubjectItemViewModel>(owner, list, SubjectAdapterDiffCallback()) {

    val repository: Repository by inject()

    private var dispose: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return BaseViewHolder(ItemSubjectBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        getItems()[position].apply {
            DataBindingUtil.getBinding<ItemSubjectBinding>(holder.itemView)?.let { binding ->
                val sharedTransitionView = binding.item
                ViewCompat.setTransitionName(sharedTransitionView, this.item.id)
                binding.item.setOnClickListener {
                    listener(this, sharedTransitionView)
                }
            }
        }

        super.onBindViewHolder(holder, position)
    }

    override fun onItemDismiss(position: Int) {
        repository.removeSubject(getItems()[position].item.id)
        super.onItemDismiss(position)
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
        newItems: List<SubjectItem>,
        itemClickListener: (SubjectItemViewModel, View) -> Unit
    ) {
        setItems(newItems.map { consensus ->
            SubjectItemViewModel(
                consensus,
                itemClickListener
            )
        })
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
        updatedItems: List<SubjectItem>,
        itemClickListener: (SubjectItemViewModel, View) -> Unit,
        remove: Boolean,
        onlyUpdate: Boolean,
        addToTop: Boolean,
        filter: ((SubjectItem) -> Boolean)? = null
    ): Int {
        val items: MutableList<SubjectItemViewModel> = getItems().toMutableList()
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
                        items[foundIndex] = SubjectItemViewModel(item, itemClickListener)
                    }
                } else if ((!onlyUpdate && filter == null) || (filter != null && filter(item))) {   // add
                    itemsRemovedAndAddedCount++
                    item.sum = itemsRemovedAndAddedCount-1.toDouble()
                    if (addToTop) {
                        items.add(0, SubjectItemViewModel(item, itemClickListener))
                    } else {
                        items.add(SubjectItemViewModel(item, itemClickListener))
                    }
                }
            }

        setItems(items.toList())

        return itemsRemovedAndAddedCount
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
        return oldItem == newItem
    }

}