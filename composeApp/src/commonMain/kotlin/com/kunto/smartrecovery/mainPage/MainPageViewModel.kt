package com.kunto.smartrecovery.mainPage

import androidx.lifecycle.ViewModel
import com.kunto.smartrecovery.backend.DayRepresentation
import com.kunto.smartrecovery.backend.MonthRepresentation
import com.kunto.smartrecovery.backend.YearRepresentation
import com.kunto.smartrecovery.backend.filehandling.FileHandler
import com.kunto.smartrecovery.getBarDataBetween
import com.kunto.smartrecovery.getPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import network.chaintech.cmpcharts.common.model.Point
import network.chaintech.cmpcharts.ui.barchart.config.BarData
import network.chaintech.cmpcharts.ui.barchart.config.GroupBar
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class MainPageViewModel : ViewModel()
{
    private enum class ChartViewTypes(val dateUnit: DateTimeUnit.TimeBased)
    {
        DAY(DateTimeUnit.TimeBased(nanoseconds = 24 * 60 * 60 * 1_000_000_000L)),
        WEEK(DateTimeUnit.TimeBased(nanoseconds = 7 * 24 * 60 * 60 * 1_000_000_000L)),
        MONTH(DateTimeUnit.TimeBased(nanoseconds = 30 * 24 * 60 * 60 * 1_000_000_000L)),
        YEAR(DateTimeUnit.TimeBased(nanoseconds = 365 * 24 * 60 * 60 * 1_000_000_000L))
    }

    private val fileHandler = FileHandler()
    private val _barData = MutableStateFlow<List<GroupBar>>(listOf())
    val barData = _barData.asStateFlow()

    init {
        showOneWeekActivity()

    }

    @OptIn(ExperimentalTime::class)
    fun showOneWeekActivity(): StateFlow<List<GroupBar>>
    {
        val colorPaletteList = getColorPaletteList1()

        val data = getBarDataBetween(
            start = Clock.System.now().minus(6, ChartViewTypes.DAY.dateUnit),
            end = Clock.System.now(),
            intervalType = ChartViewTypes.DAY.dateUnit,
            baseWriter = fileHandler,
            barChartMappings = mutableMapOf(),
            userWeight = 0.0,
            entryColor = 0,
            weightEntryColor = 0,
            groupingFunc = {
                val zonedDate = it.toLocalDateTime(TimeZone.UTC)
                val epochSecs = zonedDate.second + zonedDate.minute * 60 + zonedDate.hour * 3600
                DayRepresentation(epochSecs, zonedDate.dayOfWeek, zonedDate.day)
            },
            entryCreatingFunc = { values, previousLastX ->
                listOf(
                    BarData(
                        point = Point(
                            previousLastX.toFloat() + 1,
                            values.filter { it.isNotEmpty() }.maxOf { it.getOrElse(16) {"0.0"} }.toFloat()
                        ),
                        color = colorPaletteList.first()
                    )
                )
            }
        )

        _barData.update {
            data
        }

        return barData
    }

    @OptIn(ExperimentalTime::class)
    fun showOneMonthActivity(): StateFlow<List<GroupBar>>
    {
        val data = getBarDataBetween(
            start = Clock.System.now().minus(1, ChartViewTypes.MONTH.dateUnit),
            end = Clock.System.now(),
            intervalType = ChartViewTypes.MONTH.dateUnit,
            baseWriter = fileHandler,
            barChartMappings = mutableMapOf(),
            userWeight = 0.0,
            entryColor = 0,
            weightEntryColor = 0,
            groupingFunc = {
                val zonedDate = it.toLocalDateTime(TimeZone.UTC)
                MonthRepresentation(zonedDate.day, zonedDate.month)
            },
            entryCreatingFunc = { values, previousLastX ->
                listOf(
                    BarData(
                        Point(
                            previousLastX.toFloat() + 1,
                            values
                                .ifEmpty { listOf(List(17){ "0.0" }) }
                                .maxOf { it[16] }
                                .toFloat())
                    )
                )
            }
        )

        _barData.update {
            data
        }

        return barData
    }

    @OptIn(ExperimentalTime::class)
    fun showOneAllActivity(): StateFlow<List<GroupBar>>
    {
        val data = getBarDataBetween(
            start = Clock.System.now().minus(1, ChartViewTypes.YEAR.dateUnit),
            end = Clock.System.now(),
            intervalType = ChartViewTypes.YEAR.dateUnit,
            baseWriter = fileHandler,
            barChartMappings = mutableMapOf(),
            userWeight = 0.0,
            entryColor = 0,
            weightEntryColor = 0,
            groupingFunc = {
                val zonedDate = it.toLocalDateTime(TimeZone.UTC)
                YearRepresentation(zonedDate.day, zonedDate.month, zonedDate.year)
            },
            entryCreatingFunc = { values, previousLastX ->
                listOf(
                    BarData(
                        Point(
                            previousLastX.toFloat() + 1,
                            values
                                .ifEmpty { listOf(List(17){ "0.0" }) }
                                .maxOf { it[16] }
                                .toFloat()
                        )
                    )
                )
            }
        )

        _barData.update {
            data
        }

        return barData
    }

    fun showActivity(choice: String): StateFlow<List<GroupBar>>
    {
        return when (choice) {
            "week" -> showOneWeekActivity()
            "month" -> showOneMonthActivity()
            "year" -> showOneAllActivity()
            else -> showOneWeekActivity()
        }
    }
}