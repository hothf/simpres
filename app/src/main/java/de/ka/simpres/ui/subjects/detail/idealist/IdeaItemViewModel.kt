package de.ka.simpres.ui.subjects.detail.idealist

import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.IdeaItem

class IdeaItemViewModel(val item: IdeaItem): BaseItemViewModel(){

    val title = item.title

}