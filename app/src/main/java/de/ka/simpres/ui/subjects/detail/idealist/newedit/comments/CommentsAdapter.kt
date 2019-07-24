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

class CommentsAdapter(
    owner: LifecycleOwner,
    list: ArrayList<CommentsBaseItemViewModel> = arrayListOf(),
    val open: (Comment) -> Unit,
    sourceItems: List<Comment>?
) : BaseAdapter<CommentsBaseItemViewModel>(owner, list, CommentsAdapterDiffCallback()) {

    init {
        overwriteList(sourceItems)
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
        newList.add((Comment("", false)))
        overwriteList(newList)
    }

    private fun removeComment(comment: Comment) {
        val newList = getComments().toMutableList()
        newList.remove(comment)
        overwriteList(newList)
    }

    fun getComments(): List<Comment> {
        val comments = mutableListOf<Comment>()
        getItems().forEach {
            if (it is CommentsItemViewModel) {
                comments.add(it.item)
            }
        }
        return comments
    }

    override fun addItem(item: CommentsBaseItemViewModel) {
        super.addItem(item)
    }

    override fun removeItem(item: CommentsBaseItemViewModel) {
        super.removeItem(item)
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