package com.kunto.smartrecovery.backend

import com.kunto.smartrecovery.localizedName
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


// extension function used to get the textual representation of a day
fun LocalDate.daysShift(days: Int): LocalDate = when {
    days < 0 -> {
        minus(1, DateTimeUnit.DayBased(-days))
    }
    days > 0 -> {
        plus(1, DateTimeUnit.DayBased(days))
    }
    else -> this
}


interface DateRepresentation : Comparable<DateRepresentation>
{
    fun getText(): String
    fun dayValue(): Int
    fun getPossibleDateNames(): List<String>
    override fun hashCode(): Int

    override fun compareTo(other: DateRepresentation): Int
    {
        return if (dayValue() > other.dayValue()) { 1 }
        else if (dayValue() == other.dayValue()) { 0 }
        else { -1 }
    }
}



class HourRepresentation(private val epochSecs: Int, private val day: DayOfWeek) : DateRepresentation
{
    override fun getText(): String
    {
        return "${epochSecs / 3600} ${day.name}"
    }

    override fun dayValue(): Int
    {
        return epochSecs + day.isoDayNumber * 3600 * 24
    }

    override fun getPossibleDateNames(): List<String>
    {
        val hours = 0..24

        return hours.map { "${it}, ${day.name}" }
    }

    override fun hashCode(): Int
    {
        return getText().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HourRepresentation

        if (epochSecs != other.epochSecs) return false
        if (day != other.day) return false

        return true
    }
}


class DayRepresentation(private val epochSecs: Int, private val day: DayOfWeek, private val dayNum: Int) : DateRepresentation
{
    @OptIn(ExperimentalTime::class)
    override fun getText(): String
    {
        return day.name.take(3)
    }

    override fun dayValue(): Int
    {
        return epochSecs + day.isoDayNumber * 3600 * 24 //+ dayNum * 3600 * 24 * 32
    }

    override fun getPossibleDateNames(): List<String>
    {
        return DayOfWeek.entries
            .map { it.name }

        /*
        val auxDay = DayOfWeek.MONDAY
        val allNames = arrayListOf<String>()

        for (i in 1..<7) {
            allNames.add(auxDay.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
            auxDay.plus(1)
        }

        return allNames.toList()
         */
    }

    override fun hashCode(): Int
    {
        return getText().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DayRepresentation

        return day == other.day
    }
}


class MonthRepresentation(private val dayNum: Int, private val month: Month) : DateRepresentation
{
    override fun getText(): String
    {
        return "${dayNum}. ${month.name.take(3)}"
    }

    override fun dayValue(): Int
    {
        return dayNum + month.number * 32
    }

    override fun getPossibleDateNames(): List<String>
    {
        return Month.entries
            .map { it.name }
    }

    override fun hashCode(): Int
    {
        return getText().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MonthRepresentation

        return month == other.month
    }
}


class YearRepresentation(private val dayNum: Int, private val month: Month, private val year: Int) : DateRepresentation
{
    override fun getText(): String
    {
        return "${month.number} ${year}"
    }

    override fun dayValue(): Int
    {
        return month.number * 32 + year * 384
    }

    override fun getPossibleDateNames(): List<String>
    {
        return listOf(year.toString())
    }

    override fun hashCode(): Int
    {
        return getText().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as YearRepresentation

        return year == other.year
    }
}