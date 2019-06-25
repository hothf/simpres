package de.ka.simpres.repo.model

import java.io.Serializable
import java.util.*

data class SubjectItem(
    var id: String = "0",
    var title: String = "",
    var sum: Double = 0.0,
    var date: Long = Calendar.getInstance().timeInMillis,
    var ideas: MutableList<IdeaItem> = mutableListOf()
) : Serializable