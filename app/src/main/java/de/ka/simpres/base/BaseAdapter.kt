package de.ka.simpres.base

import android.graphics.Color
import androidx.databinding.ViewDataBinding

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.BR
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import androidx.recyclerview.widget.ItemTouchHelper
import de.ka.simpres.R
import de.ka.simpres.utils.DragAndSwipeItemTouchHelperCallback
import de.ka.simpres.utils.resources.ResourcesProvider
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


abstract class BaseAdapter<E : BaseItemViewModel>(
    private val owner: LifecycleOwner,
    private val items: ArrayList<E> = arrayListOf(),
    private val diffCallback: DiffUtil.ItemCallback<E>? = null
) : RecyclerView.Adapter<BaseViewHolder<*>>(), KoinComponent {

    private val resourcesProvider: ResourcesProvider by inject()

    private var differ: AsyncListDiffer<E>? = null
    private var itemTouchHelper: ItemTouchHelper? = null

    val layoutInflater: LayoutInflater = LayoutInflater.from(resourcesProvider.getApplicationContext())

    init {
        if (diffCallback != null) {
            @Suppress("LeakingThis")
            differ = AsyncListDiffer(this, diffCallback)
        }
    }

    fun useTouchHelperFor(recyclerView: RecyclerView) {
        itemTouchHelper = ItemTouchHelper(DragAndSwipeItemTouchHelperCallback(this))
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    open var isEmpty: Boolean = items.isEmpty()

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Timber.e("LIST -- move")
        if (diffCallback != null) {
            Collections.swap(differ!!.currentList.toMutableList(), fromPosition, toPosition)
            differ?.submitList(differ!!.currentList)
            notifyItemMoved(fromPosition, toPosition)

        } else {
            Collections.swap(items, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }
        return true
    }

    fun onItemDismiss(position: Int) {
        if (diffCallback != null) {
            differ?.submitList(differ!!.currentList.toMutableList().apply { removeAt(position) })
        } else {
            items.removeAt(position)
            notifyItemRemoved(position)
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
        isEmpty = newItems.isEmpty()
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

        holder.itemView.findViewById<View>(R.id.swipeHandle)?.let {
            it.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper?.startDrag(holder)
                }
                false
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

    fun bind(owner: LifecycleOwner, viewModel: BaseItemViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = owner
        binding.executePendingBindings()
    }

    fun onItemSelected() {
        Timber.e("LIST -- selected")
        itemView.setBackgroundColor(Color.LTGRAY)
    }

    fun onItemClear() {
        Timber.e("LIST -- clear")
        itemView.setBackgroundColor(0)
    }
}



