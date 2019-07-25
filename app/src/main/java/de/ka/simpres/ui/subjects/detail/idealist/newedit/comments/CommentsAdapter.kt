package de.ka.simpres.ui.subjects.detail.idealist.newedit.comments


import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import de.ka.simpres.databinding.ItemCommentAddBinding
import de.ka.simpres.databinding.ItemCommentBinding
import de.ka.simpres.repo.model.Comment
import de.ka.simpres.repo.model.Comments

class CommentsAdapter(
    owner: LifecycleOwner,
    list: ArrayList<CommentsBaseItemViewModel> = arrayListOf(),
    val open: (Comment) -> Unit,
    sourceItems: Comments?
) : BaseAdapter<CommentsBaseItemViewModel>(owner, list, CommentsAdapterDiffCallback()) {

    init {
        overwriteList(sourceItems?.comments)
    }

    override fun getItemViewType(position: Int): Int {
        if (getItems()[position] is CommentsAddItemViewModel) {
            return 2
        }

        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == 2) {
            CommentsAddIdeaViewHolder(ItemCommentAddBinding.inflate(layoutInflater, parent, false))
        } else {
            CommentsViewHolder(ItemCommentBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val viewModel = getItems()[position]

        if (viewModel is CommentsAddItemViewModel) {
            DataBindingUtil.getBinding<ItemCommentAddBinding>(holder.itemView)?.let { binding ->
                binding.add.setOnClickListener {
                    addComment()
                }
            }
        } else if (viewModel is CommentsItemViewModel) {
            DataBindingUtil.getBinding<ItemCommentBinding>(holder.itemView)?.let { binding ->
                binding.open.setOnClickListener {
                    open(viewModel.item)
                }
                binding.remove.setOnClickListener {
                    removeComment(viewModel.item)
                }
            }
        }

        super.onBindViewHolder(holder, position)
    }

    private fun addComment() {
        val newList = getComments().toMutableList()
        val id = if (newList.isEmpty()) 0L else (newList.last().id.plus(1))
        newList.add((Comment(id, "", false)))
        overwriteList(newList)
    }

    private fun removeComment(comment: Comment) {
        val newList = getComments().toMutableList()
        newList.remove(comment)
        overwriteList(newList)
    }

    /**
     * Retrieves all comments. If you specify that the comments should be saved, the items will call a save function.
     *
     * @param shouldSave set to true to trigger a save method on the items. Defaults to false
     * @return the comments
     */
    fun getComments(shouldSave: Boolean = false): List<Comment> {
        val comments = mutableListOf<Comment>()
        getItems().forEach {
            if (it is CommentsItemViewModel) {
                comments.add(it.item)
                if (shouldSave) {
                    it.save()
                }
            }
        }
        return comments
    }

    /**
     * Overwrites the current list with the given [newItems].
     *
     * @param newItems the new items to append or replace
     */
    fun overwriteList(newItems: List<Comment>?) {
        val newList = mutableListOf<CommentsBaseItemViewModel>()

        if (!newItems.isNullOrEmpty()) {
            newList.addAll(newItems.map { CommentsItemViewModel(it) })
        }

        newList.add(CommentsAddItemViewModel())

        setItems(newList)
    }
}

class CommentsAdapterDiffCallback : DiffUtil.ItemCallback<CommentsBaseItemViewModel>() {

    override fun areItemsTheSame(oldItem: CommentsBaseItemViewModel, newItem: CommentsBaseItemViewModel): Boolean {
        if (oldItem is CommentsItemViewModel && newItem is CommentsItemViewModel) {
            return oldItem.item == newItem.item
        }
        if (oldItem is CommentsAddItemViewModel && newItem is CommentsAddItemViewModel) {
            return true
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: CommentsBaseItemViewModel,
        newItem: CommentsBaseItemViewModel
    ): Boolean {
        return false
    }
}

class CommentsViewHolder(val binding: ItemCommentBinding) : BaseViewHolder<ItemCommentBinding>(binding) {
    override var swipeableView: View? = null
    override var isDraggable = false
    override var isSwipeable = false
}

class CommentsAddIdeaViewHolder(binding: ItemCommentAddBinding) : BaseViewHolder<ItemCommentAddBinding>(binding) {
    override var swipeableView: View? = null
    override var isDraggable = false
    override var isSwipeable = false
}