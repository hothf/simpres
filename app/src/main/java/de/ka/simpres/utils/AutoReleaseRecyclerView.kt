package de.ka.simpres.utils

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import de.ka.simpres.base.BaseAdapter

/**
 * A implementation of a [RecyclerView] which auto dismisses it's adapter, if it is no longer needed.
 */
class AutoReleaseRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onDetachedFromWindow() {
        if (adapter != null) {
            adapter = null
        }
        super.onDetachedFromWindow()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        val baseAdapter = adapter as? BaseAdapter<*>

        baseAdapter?.let {
            val helper = ItemTouchHelper(DragAndSwipeItemTouchHelperCallback(it))
            helper.attachToRecyclerView(this)
        }
    }
}

