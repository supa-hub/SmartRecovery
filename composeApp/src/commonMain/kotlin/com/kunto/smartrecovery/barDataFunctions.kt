package com.kunto.smartrecovery

import com.kunto.smartrecovery.backend.DateRepresentation
import com.kunto.smartrecovery.backend.filehandling.FileHandler
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import network.chaintech.cmpcharts.ui.barchart.config.BarData
import network.chaintech.cmpcharts.ui.barchart.config.GroupBar
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun getBarDataBetween(
    start: Instant,
    end: Instant,
    intervalType: DateTimeUnit.TimeBased,
    baseWriter: FileHandler,
    barChartMappings: MutableMap<String, List<Triple<String, List<List<String>>, Instant>>>,
    userWeight: Double,
    entryColor: Int,
    weightEntryColor: Int,
    groupingFunc: (Instant) -> DateRepresentation,
    entryCreatingFunc: (List<List<String>>, Int) -> List<BarData>
): List<GroupBar>
{
    try {
        val values = baseWriter.filesWithDateInRange("r_step_data_", start, end)
            .groupBy { groupingFunc(it.third) }
            .map { Pair(it.key, it.value.filter { aValue -> aValue.second.isNotEmpty() }) }
            .toMap()

        // create the empty data for the whole possible value range
        var calendar = start
        val allPossibleValuesInRange = mutableMapOf<DateRepresentation, List<Triple<String, List<List<String>>, Instant>>>()
        val baseValue = Triple("", arrayListOf("0".repeat(22).split("")), start)

        while (calendar <= end) {
            allPossibleValuesInRange[groupingFunc(calendar)] = listOf(baseValue)
            calendar = calendar.plus(1, intervalType)
        }

        allPossibleValuesInRange.putAll(values)

        // sort the values based on their DateRepresentation value
        val valuesSorted = allPossibleValuesInRange.toList()
            .toTypedArray()
            .sortedBy { it.first }


        if (valuesSorted.isNotEmpty()) {
            val allDataSets = mutableListOf<GroupBar>()
            var previousLastX = 0


            // create BarDataSets for the remaining values
            for (idx in valuesSorted.indices) {
                val aFilesData = valuesSorted[idx]
                val entries = entryCreatingFunc(
                    aFilesData.second.flatMap { it.second },
                    previousLastX
                )
                // create the BarDataset with all of the values
                // grouped into given key value -pairs
                val dataSet = GroupBar(
                    label = aFilesData.first.getText(),
                    barList = entries
                )

                barChartMappings[aFilesData.first.getText()] = aFilesData.second
                previousLastX += entries.size
                //dataSet.setColors(colors[idx % n])
                //dataSet.setColors(entryColor)
                allDataSets.add(dataSet)
            }

            val data = allDataSets.toList()

            /*
        // create the LineData for displaying the users weight alongside the force
        val totalEntriesMin = data.xMin.toInt()
        val totalEntriesMax = data.dataSets.sumOf { it.entryCount }
        val userWeightDataSet = LineDataSet((0..<totalEntriesMax)
            .map { Entry(it.toFloat(), userWeight.toFloat()) },
            "user weight"
        )
        userWeightDataSet.setDrawCircles(false)
        userWeightDataSet.setColors(weightEntryColor)
        userWeightDataSet.lineWidth = 2F
        val userWeightData = LineData(userWeightDataSet)

        val combined = CombinedData()
        combined.setData(data)
        combined.setData(userWeightData)
        */
            return data
        }
    }
    catch (e: Exception) {

    }
    return listOf()
}