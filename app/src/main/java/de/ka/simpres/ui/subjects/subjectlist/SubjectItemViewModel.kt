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

    val doneAmount =
        if (item.ideasCount > 0) resourceProvider.getString(
            R.string.subject_done_amount,
            item.ideasDoneCount,
            item.ideasCount
        )
        else item.generateShortTitle()

    val date = item.date.toDate()

    val color = Color.parseColor(item.color)

    val openSum = if (item.sumUnspent.toInt() > 0) item.sumUnspent.toEuro() else ""

    fun onItemClick(view: View) {
        click(this, view)
    }

    private fun SubjectItem.generateShortTitle(): String {
        val strippedTitle = this.title.trim()

        if (strippedTitle.isBlank()) return ""

        if (strippedTitle.length > 3) {

            return strippedTitle.first().toString() + strippedTitle[1].toString() + strippedTitle.last().toString()

        } else if (strippedTitle.length == 3) {
            return strippedTitle.first().toString() + strippedTitle[strippedTitle.length / 2].toString() + strippedTitle
                .last()
                .toString()
        }
        return strippedTitle

    }
}