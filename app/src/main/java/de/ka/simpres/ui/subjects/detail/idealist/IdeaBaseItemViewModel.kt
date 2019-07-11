package de.ka.simpres.ui.subjects.detail.idealist

import de.ka.simpres.base.BaseItemViewModel

abstract class IdeaBaseItemViewModel : BaseItemViewModel() {

    abstract val id: Int

    override fun equals(other: Any?): Boolean {
        if (other is IdeaBaseItemViewModel && other.id == this.id) return true
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}