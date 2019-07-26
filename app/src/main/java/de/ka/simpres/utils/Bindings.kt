package de.ka.simpres.utils

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import de.ka.simpres.R
import java.io.FileNotFoundException
import java.io.IOException

@BindingAdapter("touchHelper")
fun useTouchHelperFor(recyclerView: RecyclerView, touchHelper: ItemTouchHelper?) {
    touchHelper?.attachToRecyclerView(recyclerView)
}


@BindingAdapter("uriLoad")
fun load(view: ImageView, url: String? = null) {
    if (url == null) {
        return
    }
    val uri = Uri.parse(url)
    var bitmap: Bitmap?
    try {
        bitmap = MediaStore.Images.Media.getBitmap(view.context.contentResolver, uri)
    } catch (e: FileNotFoundException) {
        bitmap = null
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
        bitmap = null
    }
    if (bitmap != null) {
        view.setImageURI(uri)
    }
}