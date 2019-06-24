package de.ka.simpres.ui.home

import java.util.*

data class HomeItem(
    val id: Int = 0,
    val title: String = "",
    val sum: Double = 0.0,
    val date: Long = Calendar.getInstance().timeInMillis
)