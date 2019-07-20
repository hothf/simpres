package de.ka.simpres.utils.color

import java.util.*

object ColorResources {

    val indicatorColors = listOf(
        "#5BB6A6",
        "#F7D24B",
        "#544EE6",
        "#D88C48",
        "#C2445B",
        "#AF62F6"
    )

    fun getRandomColorString(): String {
        val randomIndex= Random().nextInt(indicatorColors.size)

        return indicatorColors[randomIndex]
    }
}