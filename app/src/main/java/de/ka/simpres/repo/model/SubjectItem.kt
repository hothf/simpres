package de.ka.simpres.repo.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable
import java.util.*

@Entity
data class SubjectItem(
    @Id var id: Long,
    var title: String = "",
    var sum: String = "0",
    var date: Long = Calendar.getInstance().timeInMillis
) : Serializable