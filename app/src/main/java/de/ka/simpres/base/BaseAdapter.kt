package de.ka.simpres.base

import android.app.Application
import android.content.Context
import androidx.databinding.ViewDataBinding

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.core.view.MotionEventCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import de.ka.simpres.BR
import de.ka.simpres.repo.Repository
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import androidx.recyclerview.widget.ItemTouchHelper
import de.ka.simpres.R
import de.ka.simpres.utils.DragAndSwipeItemTouchHelperCallback
import timber.log.Timber


abstract class BaseAdapter<E : BaseItemViewModel>(
    private val owner: LifecycleOwner,
    private val items: ArrayList<E> = arrayListOf(),
    diffCallback: DiffUtil.ItemCallback<E>? = null
) : RecyclerView.Adapter<BaseViewHolder<*>>(), KoinComponent {

    val app: Application by inject()
    val layoutInflater: LayoutInflater = LayoutInflater.from(app.applicationContext)

    private var differ: AsyncListDiffer<E>? = null

    private var itemTouchHelper: ItemTouchHelper? = null

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
        return true
    }

    fun onItemDismiss(position: Int) {
        Timber.e("LIST -- dismiss")
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
        if (differ != null) {
            differ!!.submitList(differ!!.currentList.toMutableList().apply { add(index, item) })
        } else {
            items.add(item)

            notifyDataSetChanged()
        }
        isEmpty = false
    }

    open fun setItems(newItems: List<E>) {
        if (differ != null) {
            differ!!.submitList(newItems)
        } else {
            items.clear()

            items.addAll(newItems)

            notifyDataSetChanged()
        }
        isEmpty = newItems.isEmpty()
    }

    open fun addItems(newItems: List<E>) {
        if (differ != null) {
            val items = ArrayList(differ!!.currentList)
            items.addAll(newItems)

            differ!!.submitList(items)
        } else {
            items.addAll(newItems)
            notifyDataSetChanged()
        }
        isEmpty = newItems.isEmpty()
    }

    override fun getItemCount(): Int {
        if (differ != null) {
            return differ!!.currentList.size
        }
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (differ != null) {
            holder.bind(owner, differ!!.currentList[holder.adapterPosition])
        } else {
            holder.bind(owner, items[holder.adapterPosition])
        }

        if (holder.adapterPosition in 0 until itemCount) {
            if (differ != null) {
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
        return if (differ != null) {
            differ!!.currentList[position].type
        } else {
            items[position].type
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        if (holder.adapterPosition in 0 until itemCount) {
            if (differ != null) {
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

    val appContext: Context by inject()
    val repository: Repository by inject()

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
    }

    fun onItemClear() {
        Timber.e("LIST -- clear")
    }
}



