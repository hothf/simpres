package de.ka.simpres.ui.subjects.detail.idealist

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R
import de.ka.simpres.repo.Repository
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.resources.ResourcesProvider
import de.ka.simpres.utils.toEuro
import org.koin.standalone.inject

class IdeaItemViewModel(val item: IdeaItem, color: String, val click: (IdeaItem) -> Unit) : IdeaBaseItemViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
    private val repository: Repository by inject()

    val doneAlpha = ObservableFloat().apply { set(alphaForDone()) }
    val done = ObservableBoolean().apply { set(item.done) }
    val checkerBackground =
        ObservableField<Drawable>().apply { set(retrieveCheckerBackground()) }
    val color = Color.parseColor(color)

    override val id = item.id.toInt()

    val title = item.title

    val sum = if (!item.sum.isBlank() && item.sum.toInt() > 0) {
        item.sum.toEuro()
    } else {
        ""
    }

    fun onItemClick() {
        click(item)
    }

    /**
     * Sets the done flag to the given parameter.
     */
    fun toggleDone() {
        item.done = item.done.not()
        doneAlpha.set(alphaForDone())
        done.set(item.done)
        checkerBackground.set(retrieveCheckerBackground())
        repository.saveOrUpdateIdea(item)
    }

    private fun alphaForDone() = if (item.done) 0.25f else 1.0f

    private fun retrieveCheckerBackground(): Drawable? {
        return if (item.done) {
            resourcesProvider.getDrawable(R.drawable.bg_circle)
        } else {
            resourcesProvider.getDrawable(R.drawable.bg_circle_stroke)
        }
    }
}