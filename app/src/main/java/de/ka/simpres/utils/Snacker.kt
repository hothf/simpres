package de.ka.simpres.utils

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import de.ka.simpres.R
import de.ka.simpres.base.events.ShowSnack

/**
 * Represents a snacker. This is some kind of snack bar, but much cooler!
 */
class Snacker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    enum class SnackType(@ColorRes val textColorRes: Int, @DrawableRes val backgroundRes: Int) {
        DEFAULT(R.color.colorBackgroundSecondary, R.drawable.bg_snacker_default),
        WARNING(R.color.colorBackgroundSecondary, R.drawable.bg_snacker_warning),
        ERROR(R.color.colorBackgroundSecondary, R.drawable.bg_snacker_error)
    }

    private val snackHandler = Handler()

    private var container: View
    private var snackText: TextView
    private var snackAction: TextView
    private var isHidingStopped = false

    init {
        inflate(context, R.layout.layout_snacker, this)

        container = findViewById(R.id.snacker)
        snackText = findViewById(R.id.snackText)
        snackAction = findViewById(R.id.snackAction)
        visibility = View.INVISIBLE
    }

    /**
     * Reveals the snacker. Will auto dismiss itself. A snacker can be revealed several times.
     */
    fun reveal(showSnack: ShowSnack) {
        snackHandler.removeCallbacksAndMessages(null)
        isHidingStopped = true

        if (showSnack.action != null && showSnack.actionText != null) {
            snackAction.text = showSnack.actionText
            snackAction.visibility = View.VISIBLE
        } else {
            snackAction.visibility = View.GONE
        }

        container.setOnClickListener {
            if (snackAction.visibility == GONE) {
                snackHandler.removeCallbacksAndMessages(null)
                hide()
            }
        }

        snackAction.setOnClickListener {
            showSnack.action?.invoke()
            snackHandler.removeCallbacksAndMessages(null)
            hide()
        }

        container.setBackgroundResource(showSnack.type.backgroundRes)
        snackText.setTextColor(ContextCompat.getColor(context, showSnack.type.textColorRes))
        snackText.text = showSnack.message

        visibility = View.VISIBLE
        createReveal(container)

        val timeMs = if (showSnack.type == SnackType.ERROR) HIDE_TIME_MS * 2 else HIDE_TIME_MS

        snackHandler.postDelayed({
            hide()
        }, timeMs)
    }

    private fun hide() {
        isHidingStopped = false
        createHide(container)
    }

    private fun createReveal(view: View) {
        if (!view.isAttachedToWindow) {
            return
        }

        view.animate().translationY(0.0f).setInterpolator(AccelerateInterpolator())
    }

    private fun createHide(view: View) {
        if (!view.isAttachedToWindow) {
            return
        }

        view.animate().translationY(view.height.toFloat()).setInterpolator(DecelerateInterpolator())
    }

    companion object {
        const val HIDE_TIME_MS = 3_500.toLong()
    }
}