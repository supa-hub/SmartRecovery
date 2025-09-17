package com.kunto.smartrecovery.mainPage

import androidx.compose.ui.graphics.Color
import com.kunto.smartrecovery.theming.Blue
import com.kunto.smartrecovery.theming.LightBlue
import com.kunto.smartrecovery.theming.Red
import network.chaintech.cmpcharts.common.extensions.formatNumber
import network.chaintech.cmpcharts.common.model.LegendLabel
import network.chaintech.cmpcharts.common.model.Point
import network.chaintech.cmpcharts.ui.barchart.config.BarData
import network.chaintech.cmpcharts.ui.barchart.config.GroupBar
import kotlin.random.Random


fun getLegendsLabelDataBarChart(colorPaletteList: List<Color>): List<LegendLabel> {
    val legendLabelList = mutableListOf<LegendLabel>()
    for (index in colorPaletteList.indices) {
        legendLabelList.add(
            LegendLabel(
                colorPaletteList[index],
                "B$index"
            )
        )
    }
    return legendLabelList
}

fun getGroupBarChartData(groupAmount: Int, maxRange: Int, groupSize: Int): List<GroupBar> =
    (0 until groupAmount)
        .map { index ->
            val barList = (0 until groupSize)
                .map { Random.nextDouble(1.0, maxRange.toDouble()).formatNumber().toFloat() }
                .mapIndexed { i, barValue ->
                    BarData(
                        Point(
                            index.toFloat(),
                            barValue
                        ),
                        label = "B$i",
                        description = "Bar at $index with label B$i has value ${
                            barValue.formatNumber()
                        }"
                    )
                }

            GroupBar(index.toString(), barList)
        }



fun getGroupBarChartData1(listSize: Int, maxRange: Int, groupSize: Int): List<GroupBar> {
    val list = mutableListOf<GroupBar>()
    for (index in 0 until listSize) {
        val barList = mutableListOf<BarData>()
        for (i in 0 until groupSize) {
            val barValue = Random.nextDouble(1.0, maxRange.toDouble()).formatNumber().toFloat()
            barList.add(
                BarData(
                    Point(
                        index.toFloat(),
                        barValue
                    ),
                    label = "B$i",
                    description = "Bar at $index with label B$i has value ${
                        barValue.formatNumber()
                    }"
                )
            )
        }
        list.add(GroupBar(index.toString(), barList))
    }
    return list
}

fun getColorPaletteList1(): List<Color> {
    val colorList = listOf(Blue, LightBlue, Red, Blue, Red, LightBlue, LightBlue, LightBlue, Blue, Red)
    return colorList
}
