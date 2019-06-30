package de.ka.simpres.base

import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import de.ka.simpres.BR
import de.ka.simpres.R
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import de.ka.simpres.utils.DragAndSwipeItemTouchHelperCallback
import de.ka.simpres.utils.resources.ResourcesProvider
import java.util.*
import kotlin.collections.ArrayList


abstract class BaseAdapter<E : BaseItemViewModel>(
    private val owner: LifecycleOwner,
    private val items: ArrayList<E> = arrayListOf(),
    private val diffCallback: DiffUtil.ItemCallback<E>? = null
) : RecyclerView.Adapter<BaseViewHolder<*>>(), KoinComponent {

    private val resourcesProvider: ResourcesProvider by inject()

    private var differ: AsyncListDiffer<E>? = null

    var isEmpty: Boolean = items.isEmpty()

    val layoutInflater: LayoutInflater = LayoutInflater.from(resourcesProvider.getApplicationContext())

    init {
        if (diffCallback != null) {
            @Suppress("LeakingThis")
            differ = AsyncListDiffer(this, diffCallback)
        }
    }

    fun useTouchHelperFor(recyclerView: RecyclerView) {
        ItemTouchHelper(DragAndSwipeItemTouchHelperCallback(this)).attachToRecyclerView(recyclerView)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (diffCallback != null) {
            Collections.swap(differ!!.currentList.toMutableList(), fromPosition, toPosition)
            differ?.submitList(differ!!.currentList)
            notifyItemMoved(fromPosition, toPosition)
        }
        return true
    }

    open fun onItemDismiss(position: Int) {
        if (diffCallback != null) {
            return
        } else {
            items.removeAt(position)
            notifyItemRemoved(position)
            isEmpty = items.isEmpty()
        }
    }

    fun getItems(): List<E> {
        return if (differ != null) {
            differ!!.currentList
        } else {
            items
        }
    }

    open fun clear() {
        if (differ != null) {
            differ?.submitList(listOf())
        } else {
            items.clear()
            notifyDataSetChanged()
        }
        isEmpty = true
    }

    open fun addItem(index: Int = 0, item: E) {
        if (diffCallback != null) {
            differ?.submitList(differ!!.currentList.toMutableList().apply { add(index, item) })
        } else {
            items.add(item)

            notifyDataSetChanged()
        }
        isEmpty = false
    }

    open fun setItems(newItems: List<E>) {
        if (diffCallback != null) {
            differ?.submitList(newItems)
        } else {
            items.clear()

            items.addAll(newItems)

            notifyDataSetChanged()
        }
        isEmpty = newItems.isEmpty()
    }

    open fun addItems(newItems: List<E>) {
        if (diffCallback != null) {
            val items = ArrayList(differ!!.currentList)
            items.addAll(newItems)

            differ?.submitList(items)
        } else {
            items.addAll(newItems)
            notifyDataSetChanged()
        }
        isEmpty = false
    }

    override fun getItemCount(): Int {
        if (diffCallback != null) {
            return differ!!.currentList.size
        }
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (diffCallback != null) {
            holder.bind(owner, differ!!.currentList[holder.adapterPosition])
        } else {
            holder.bind(owner, items[holder.adapterPosition])
        }

        if (holder.adapterPosition in 0 until itemCount) {
            if (diffCallback != null) {
                differ!!.currentList[holder.adapterPosition].onAttached()
            } else {
                items[holder.adapterPosition].onAttached()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (diffCallback != null) {
            differ!!.currentList[position].type
        } else {
            items[position].type
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        if (holder.adapterPosition in 0 until itemCount) {
            if (diffCallback != null) {
                differ!!.currentList[holder.adapterPosition].onCleared()
            } else {
                items[holder.adapterPosition].onCleared()
            }
        }
        super.onViewRecycled(holder)
    }
}

/**
 * These viewModels are not created through the android viewmodel framework but still may be used
 * with [MutableLiveData<T>].
 */
abstract class BaseItemViewModel(val type: Int = 0) : KoinComponent {

    var compositeDisposable: CompositeDisposable? = null

    fun onAttached() {
        compositeDisposable = CompositeDisposable()
    }

    fun onCleared() {
        compositeDisposable?.clear()
    }
}

class BaseViewHolder<T : ViewDataBinding>(private val binding: T) : RecyclerView.ViewHolder(binding.root) {

    private var cachedBackgroundImage: Drawable? = null

    fun bind(owner: LifecycleOwner, viewModel: BaseItemViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = owner
        binding.executePendingBindings()
    }

    fun onItemSelected() {
        cachedBackgroundImage = itemView.background

        itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_selected)
    }

    fun onItemClear() {
        itemView.background = cachedBackgroundImage
    }
}