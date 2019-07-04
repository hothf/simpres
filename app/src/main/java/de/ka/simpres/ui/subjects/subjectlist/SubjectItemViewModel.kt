package de.ka.simpres.ui.subjects.subjectlist

import android.view.View
import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.utils.toDate

class SubjectItemViewModel(val item: SubjectItem, val listener: (SubjectItemViewModel, View) -> Unit) : BaseItemViewModel() {

    val title = item.title

    val sum = item.sum

    val date = item.date.toDate()

    override fun equals(other: Any?): Boolean {
        if (other is SubjectItemViewModel && other.item == this.item) return true
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }
}