package de.ka.simpres.repo.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
data class IdeaItem(
    @Id var id: Long,
    var subjectId: Long,
    var title: String = "",
    var done: Boolean = false,
    var sum: String = ""
) : Serializable