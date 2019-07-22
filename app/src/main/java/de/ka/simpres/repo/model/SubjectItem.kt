package de.ka.simpres.repo.model

import de.ka.simpres.utils.color.ColorResources
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable
import java.util.*

@Entity
data class SubjectItem(
    @Id var id: Long,
    var title: String = "",
    var sumUnspent: String = "0",
    var sumSpent: String = "0",
    var date: Long = Calendar.getInstance().timeInMillis,
    var color: String = ColorResources.indicatorColors.first(),
    var position: Int = 0,
    var ideasCount: Int = 0,
    var ideasDoneCount: Int = 0,
    var pushEnabled: Boolean = true
) : Serializable