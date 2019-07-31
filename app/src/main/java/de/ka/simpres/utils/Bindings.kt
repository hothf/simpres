package de.ka.simpres.utils

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

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
    } catch (e: SecurityException) {
        Timber.e(e, "No permission to load image uri.")
        bitmap = null
    }
    if (bitmap != null) {
        view.setImageURI(uri)
    }
}