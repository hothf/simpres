package de.ka.simpres.utils

import android.graphics.Canvas
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import de.ka.simpres.base.BaseAdapter
import de.ka.simpres.base.BaseViewHolder
import kotlin.math.abs

/**
 * An implementation of [ItemTouchHelper.Callback] that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br></br>
 *
 * This callback will only work in conjunction with the usage of a [BaseAdapter] and [BaseViewHolder].
 * If you want to disable the movement callbacks for certain viewHolders, create [BaseViewHolder] with its isDraggable
 * field and isSwipeable set to false.
 *
 * Converted to kotlin and heavily edited for this project use by Thomas Hofmann.
 *
 * @author mainly Paul Burke (ipaulpro) on gihub, Thomas Hofmann
 */
class DragAndSwipeItemTouchHelperCallback(private val mAdapter: BaseAdapter<*>) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags = 0
        var swipeFlags = 0
        if (viewHolder is BaseViewHolder<*>) {
            if (recyclerView.layoutManager is GridLayoutManager) {
                dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            } else {
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            }
            return makeMovementFlags(
                if (viewHolder.isDraggable) dragFlags else 0,
                if (viewHolder.isSwipeable) swipeFlags else 0
            )
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder):
            Boolean {
        if (source.itemViewType != target.itemViewType) {
            return false
        }

        mAdapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            val alpha = ALPHA_FULL - abs(dX) / viewHolder.itemView.width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is BaseViewHolder<*>) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.onItemDrag()
            } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                viewHolder.onItemSwipe()
            }
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.alpha = ALPHA_FULL

        if (viewHolder is BaseViewHolder<*>) {
            viewHolder.onItemClear()
        }
    }

    companion object {
        const val ALPHA_FULL = 1.0f
    }
}