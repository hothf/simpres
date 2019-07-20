package de.ka.simpres.ui.subjects.subjectlist

import android.graphics.Color
import android.view.View
import de.ka.simpres.R
import de.ka.simpres.base.BaseItemViewModel
import de.ka.simpres.repo.model.SubjectItem
import de.ka.simpres.utils.resources.ResourcesProvider
import de.ka.simpres.utils.toDate
import de.ka.simpres.utils.toEuro
import kotlinx.android.synthetic.main.item_subject.view.*
import org.koin.standalone.inject

class SubjectItemViewModel(
    val item: SubjectItem,
    val click: (SubjectItemViewModel, View) -> Unit,
    val remove: (SubjectItemViewModel) -> Unit
) : BaseItemViewModel() {

    private val resourceProvider: ResourcesProvider by inject()

    val title = item.title

    val sum = if (!item.sum.isBlank() && item.sum.toInt() > 0) {
        item.sum.toEuro()
    } else {
        ""
    }

    val shortTitle = if (item.title.isNotEmpty()) item.title.first().toString() else "-"

    val doneAmount =
        if (item.ideasCount > 0) resourceProvider.getString(
            R.string.subject_done_amount,
            item.ideasDoneCount,
            item.ideasCount
        )
        else shortTitle

    val date = item.date.toDate()

    val color = Color.parseColor(item.color)
}