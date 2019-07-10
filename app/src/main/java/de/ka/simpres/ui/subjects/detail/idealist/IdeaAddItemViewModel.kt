package de.ka.simpres.ui.subjects.detail.idealist

import de.ka.simpres.repo.model.IdeaItem

class IdeaAddItemViewModel(override val item: IdeaItem = IdeaItem(-1, -1)) : IdeaBaseItemViewModel() {

    override val id = ADD_ID
}