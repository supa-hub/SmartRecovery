package com.kunto.smartrecovery.UIComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunto.smartrecovery.getPlatform
import com.kunto.smartrecovery.mainPage.getColorPaletteList1
import com.kunto.smartrecovery.theming.Transparent
import network.chaintech.cmpcharts.axis.AxisProperties
import network.chaintech.cmpcharts.common.model.Point
import network.chaintech.cmpcharts.ui.barchart.BarChart
import network.chaintech.cmpcharts.ui.barchart.GroupBarChart
import network.chaintech.cmpcharts.ui.barchart.config.BarChartConfig
import network.chaintech.cmpcharts.ui.barchart.config.BarChartStyle
import network.chaintech.cmpcharts.ui.barchart.config.BarData
import network.chaintech.cmpcharts.ui.barchart.config.BarPlotData
import network.chaintech.cmpcharts.ui.barchart.config.GroupBar
import network.chaintech.cmpcharts.ui.barchart.config.GroupBarChartData
import network.chaintech.cmpcharts.ui.barchart.config.GroupSeparatorProperties
import network.chaintech.cmpcharts.ui.barchart.config.SelectionHighlightData
import network.chaintech.cmpcharts.ui.circularchart.model.ChartData
import smartrecovery.composeapp.generated.resources.Res
import kotlin.math.roundToInt


@Composable
fun CreateGroupedBarChart(data: List<GroupBar>) {
    val maxRange = data.ifEmpty {
        listOf(
            GroupBar(
                label = "",
                barList = listOf(BarData(Point(0.0f, 100.0f)))
            )
        ) }
        .maxBy { it.yMax }
        .yMax
    val groupSize = 1
    val yStepSize = 10

    val xAxisProperties = AxisProperties(
        stepSize = 50.dp,
        bottomPadding = 5.dp,
        initialDrawPadding = 50.dp,
        lineColor = MaterialTheme.colorScheme.primary,
        labelColor = MaterialTheme.colorScheme.primary,
        labelFontSize = 12.sp,
        labelFormatter = { index -> data.getOrNull(index)?.label ?: "" }
    )
    val yAxisProperties = AxisProperties(
        stepCount = yStepSize,
        bottomPadding = 20.dp,
        initialDrawPadding = 0.dp,
        lineColor = MaterialTheme.colorScheme.primary,
        labelColor = MaterialTheme.colorScheme.primary,
        labelFontSize = 16.sp,
        labelFormatter = { index -> "${(index * (maxRange / yStepSize)).roundToInt()} N" }
    )

    val colorPaletteList = getColorPaletteList1()

    /*
    val groupBarPlotData = BarPlotData(
        groupBarList = data,
        barStyle = BarChartStyle(barWidth = 50.dp, cornerRadius = 20.dp),
        barColorPaletteList = colorPaletteList
    )

    val groupBarChartData = GroupBarChartData(
        barPlotData = groupBarPlotData,
        xAxisProperty = xAxisProperties,
        yAxisProperty = yAxisProperties,
        backgroundColor = MaterialTheme.colorScheme.background,
        groupSeparatorConfig = GroupSeparatorProperties(0.dp)
    )
     */

    val barChartData = BarChartConfig(
        chartData = data.flatMapIndexed { idx, groupBar -> groupBar.barList }
            .ifEmpty { List(size = 10) { BarData(point = Point(it.toFloat(), 0.0f), color = colorPaletteList.first()) } },
        xAxisData = xAxisProperties,
        yAxisData = yAxisProperties,
        backgroundColor = MaterialTheme.colorScheme.background,
        barStyle = BarChartStyle(
            barWidth = 50.dp,
            cornerRadius = 20.dp,
            selectionHighlightData = SelectionHighlightData(
                highlightBarColor = Transparent,
                highlightTextColor = Transparent,
                highlightTextTypeface = FontWeight.Bold,
                highlightTextBackgroundColor = Transparent,
                popUpLabel = { _, y -> "" }
            )
        ),
        horizontalExtraSpace = 40.dp,
    )

    Column(
        Modifier
            .height(480.dp)
            .width(400.dp)
            .padding(top = 30.dp)
    ) {
        /*
        GroupBarChart(
            modifier = Modifier
                .height(300.dp),
            groupBarChartData = groupBarChartData
        )
         */

        BarChart(
            modifier = Modifier
                .height(300.dp),
            barChartData = barChartData
        )
    }
}