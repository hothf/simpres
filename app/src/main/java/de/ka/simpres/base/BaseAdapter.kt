package de.ka.simpres.base

import androidx.databinding.ViewDataBinding

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import androidx.recyclerview.widget.*
import de.ka.simpres.BR
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import de.ka.simpres.utils.resources.ResourcesProvider
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseAdapter<E : BaseItemViewModel>(
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

    @CallSuper
    open fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (diffCallback != null) {
            val list = differ!!.currentList.toMutableList()
            Collections.swap(list, fromPosition, toPosition)
            setItems(list)
        } else {
            Collections.swap(items, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }
        return true
    }

    @CallSuper
    open fun onItemDismiss(position: Int) {
        if (diffCallback != null) {
            // let subclasses handle it
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

    open fun addItem(item: E) {
        if (diffCallback != null) {
            differ?.submitList(differ!!.currentList.toMutableList().apply { add(item) })
        } else {
            items.add(item)

            notifyDataSetChanged()
        }
        isEmpty = false
    }

    open fun removeItem(item: E) {
        var isRemoved = false
        val size: Int

        if (diffCallback != null) {
            size = differ!!.currentList.size
            differ?.submitList(differ!!.currentList.toMutableList().apply {
                isRemoved = remove(item)
            })
        } else {
            size = items.size
            isRemoved = items.remove(item)

            notifyDataSetChanged()
        }
        isEmpty = isRemoved && size == 1
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
            holder.bind(differ!!.currentList[holder.adapterPosition])
        } else {
            holder.bind(items[holder.adapterPosition])
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
abstract class BaseItemViewModel(val type: Int = 0) : BaseObservable(), KoinComponent {

    var compositeDisposable: CompositeDisposable? = null

    fun onAttached() {
        compositeDisposable = CompositeDisposable()
    }

    fun onCleared() {
        compositeDisposable?.clear()
    }
}

abstract class BaseViewHolder<T : ViewDataBinding>(private val binding: T) :
    RecyclerView.ViewHolder(binding.root) {

    abstract var isDraggable: Boolean
    abstract var isSwipeable: Boolean
    abstract var swipeableView: View?

    fun bind(viewModel: BaseItemViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }

    open fun onHolderDrag() {
        // do implement in sub class
    }

    @CallSuper
    open fun onHolderSwipe(
        dX: Float,
        dY: Float,
        actionState: Int
    ) {
        swipeableView?.let {
            it.translationX = dX
        }
    }

    @CallSuper
    open fun onHolderClear() {
        swipeableView?.let {
            it.translationX = 0.0f
        }
    }
}