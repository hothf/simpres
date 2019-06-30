package de.ka.simpres.utils

import android.graphics.Rect
import android.view.View
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView

/**
 * A item decoration for consensuses.
 */
class DecorationUtil(
    private val spacingTop: Int,
    private val spacingLeftAndRight: Int = 0,
    private val columnCount: Int = 0
) :
    RecyclerView.ItemDecoration() {



    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildViewHolder(view).adapterPosition;

        val layoutParams = view.layoutParams as RecyclerView.LayoutParams

        // no position, leave it alone
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (itemPosition % 2 == 0) {
            layoutParams.topMargin = 25
            layoutParams.leftMargin = 50
            layoutParams.rightMargin = 25
            layoutParams.bottomMargin = 25
        } else {
            layoutParams.topMargin = 25
            layoutParams.leftMargin = 25
            layoutParams.rightMargin = 50
            layoutParams.bottomMargin = 25
        }


        view.layoutParams = layoutParams
    }
}