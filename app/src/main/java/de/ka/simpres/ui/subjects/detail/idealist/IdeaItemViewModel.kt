package de.ka.simpres.ui.subjects.detail.idealist

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.toEuro

class IdeaItemViewModel(val item: IdeaItem) : IdeaBaseItemViewModel() {

    override val id = item.id.toInt()

    val title = item.title

    val sum = if (!item.sum.isBlank() && item.sum.toInt() > 0) {
        item.sum.toEuro()
    } else {
        ""
    }

    val done = item.done

    /**
     * Sets the done flag to the given parameter.
     *
     * @param done set to true to mark the item as done
     */
    fun checkDone(done: Boolean) {
        item.done = done
        doneAlpha.postValue(alphaForDone(done))
    }

    val doneAlpha = MutableLiveData<Float>().apply { postValue(alphaForDone(item.done)) }

    private fun alphaForDone(done: Boolean) = if (done) 0.25f else 1.0f
}