package com.kunto.smartrecovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunto.smartrecovery.mainPage.getColorPaletteList1
import com.kunto.smartrecovery.mainPage.getGroupBarChartData
import com.kunto.smartrecovery.mainPage.getLegendsLabelDataBarChart
import network.chaintech.cmpcharts.axis.AxisProperties
import network.chaintech.cmpcharts.common.model.LegendsConfig
import network.chaintech.cmpcharts.ui.barchart.GroupBarChart
import network.chaintech.cmpcharts.ui.barchart.config.BarChartStyle
import network.chaintech.cmpcharts.ui.barchart.config.BarPlotData
import network.chaintech.cmpcharts.ui.barchart.config.GroupBar
import network.chaintech.cmpcharts.ui.barchart.config.GroupBarChartData
import network.chaintech.cmpcharts.ui.barchart.config.GroupSeparatorProperties



@Composable
fun CreateGroupedBarChart(data: List<GroupBar>) {
    val maxRange = 100
    val groupSize = 1
    val yStepSize = 10

    val xAxisProperties = AxisProperties(
        stepSize = 30.dp,
        bottomPadding = 5.dp,
        initialDrawPadding = 16.dp,
        lineColor = MaterialTheme.colorScheme.primary,
        labelColor = MaterialTheme.colorScheme.primary,
        labelFontSize = 16.sp,
        labelFormatter = { index -> index.toString() }
    )
    val yAxisProperties = AxisProperties(
        stepCount = yStepSize,
        bottomPadding = 20.dp,
        initialDrawPadding = 0.dp,
        lineColor = MaterialTheme.colorScheme.primary,
        labelColor = MaterialTheme.colorScheme.primary,
        labelFontSize = 16.sp,
        labelFormatter = { index -> "${(index * (maxRange / yStepSize))} N" }
    )

    val colorPaletteList = getColorPaletteList1()
    val groupBarPlotData = BarPlotData(
        groupBarList = data,
        barStyle = BarChartStyle(barWidth = 35.dp, cornerRadius = 15.dp),
        barColorPaletteList = colorPaletteList
    )
    val groupBarChartData = GroupBarChartData(
        barPlotData = groupBarPlotData,
        xAxisProperty = xAxisProperties,
        yAxisProperty = yAxisProperties,
        backgroundColor = MaterialTheme.colorScheme.background,
        groupSeparatorConfig = GroupSeparatorProperties(0.dp)
    )
    Column(
        Modifier
            .height(480.dp)
            .width(400.dp)
            .padding(top = 30.dp)
    ) {
        GroupBarChart(
            modifier = Modifier
                .height(300.dp),
            groupBarChartData = groupBarChartData
        )
    }
}