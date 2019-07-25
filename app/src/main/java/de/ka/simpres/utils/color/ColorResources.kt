package de.ka.simpres.utils.color

import java.util.*

object ColorResources {

    val indicatorColors = listOf(
        "#5BB6A6",
        "#77B06D",
        "#05DEFB",
        "#468BC5",
        "#544EE6",
        "#F7D24B",
        "#D88C48",
        "#C2445B",
        "#FB23EA",
        "#AF62F6",
        "#5E3B2F",
        "#40464B"
    )

    fun getRandomColorString(): String {
        val randomIndex= Random().nextInt(indicatorColors.size)

        return indicatorColors[randomIndex]
    }
}