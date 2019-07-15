package de.ka.simpres.ui.subjects.detail.idealist

import androidx.lifecycle.MutableLiveData
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.toEuro

class IdeaItemViewModel(val item: IdeaItem) : IdeaBaseItemViewModel() {

    val doneAlpha = MutableLiveData<Float>().apply { postValue(alphaForDone()) }
    val done = MutableLiveData<Boolean>().apply { postValue(item.done) }

    override val id = item.id.toInt()

    val title = item.title

    val sum = if (!item.sum.isBlank() && item.sum.toInt() > 0) {
        item.sum.toEuro()
    } else {
        ""
    }

    /**
     * Sets the done flag to the given parameter.
     */
    fun toggleDone() {
        item.done = item.done.not()
        doneAlpha.postValue(alphaForDone())
        done.postValue(item.done)
    }

    private fun alphaForDone() = if (item.done) 0.25f else 1.0f
}