package de.ka.simpres.ui.subjects.detail.idealist

import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.IdeaItem

class IdeaItemViewModel(val item: IdeaItem): BaseItemViewModel(){

    val title = item.title

    val sum = item.sum

    val done = item.done

    override fun equals(other: Any?): Boolean {
        if (other is IdeaItemViewModel && other.item == this.item) return true
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }
}