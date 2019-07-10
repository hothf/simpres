package de.ka.simpres.ui.subjects.detail.idealist

import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.IdeaItem
import de.ka.simpres.utils.toEuro

class IdeaItemViewModel(val item: IdeaItem): BaseItemViewModel(){

    val title = item.title

    val sum = if (!item.sum.isBlank() && item.sum.toInt() > 0) {
        item.sum.toEuro()
    } else {
        ""
    }

    val done = item.done
}