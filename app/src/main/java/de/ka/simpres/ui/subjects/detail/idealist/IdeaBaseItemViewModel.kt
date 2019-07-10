package de.ka.simpres.ui.subjects.detail.idealist

import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.IdeaItem

abstract class IdeaBaseItemViewModel : BaseItemViewModel() {

    abstract val id: Int

    abstract val item: IdeaItem

    override fun equals(other: Any?): Boolean {
        if (other is IdeaBaseItemViewModel && other.id == this.id) return true
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        const val ADD_ID = -999
    }
}