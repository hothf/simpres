package de.ka.simpres.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("touchHelper")
fun useTouchHelperFor(recyclerView: RecyclerView, touchHelper: ItemTouchHelper?) {
    touchHelper?.attachToRecyclerView(recyclerView)
}