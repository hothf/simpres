package de.ka.simpres.ui.home

import java.io.Serializable
import java.util.*

data class HomeItem(
    var id: Int = 0,
    var title: String = "",
    var sum: Double = 0.0,
    var date: Long = Calendar.getInstance().timeInMillis
): Serializable