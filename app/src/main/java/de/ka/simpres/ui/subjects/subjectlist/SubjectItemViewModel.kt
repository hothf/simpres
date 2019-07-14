package de.ka.simpres.ui.subjects.subjectlist

import android.graphics.Color
import android.view.View
import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.utils.toDate
import de.ka.simpres.utils.toEuro

class SubjectItemViewModel(
    val item: SubjectItem,
    val click: (SubjectItemViewModel, View) -> Unit,
    val remove: (SubjectItemViewModel) -> Unit
) : BaseItemViewModel() {

    val title = item.title

    val sum = if (!item.sum.isBlank() && item.sum.toInt() > 0) {
        item.sum.toEuro()
    } else {
        ""
    }

    val doneAmount = if (item.ideasCount > 0) "${item.ideasDoneCount} of ${item.ideasCount}" else ""

    val date = item.date.toDate()

    val color = Color.parseColor(item.color)

    val progress = if (item.ideasCount > 0) {
        ((item.ideasDoneCount.toFloat() / item.ideasCount.toFloat()) * 100).toInt()
    } else {
        100
    }

    val progressVisibility = if (item.ideasCount > 0) View.VISIBLE else View.GONE
}