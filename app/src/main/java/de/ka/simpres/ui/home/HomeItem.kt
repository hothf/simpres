package de.ka.simpres.ui.home

import de.ka.simpres.ui.home.detail.HomeDetailItem
import java.io.Serializable
import java.util.*

data class HomeItem(
    var id: String = "0",
    var title: String = "",
    var sum: Double = 0.0,
    var date: Long = Calendar.getInstance().timeInMillis,
    var items: MutableList<HomeDetailItem> = mutableListOf()
) : Serializable