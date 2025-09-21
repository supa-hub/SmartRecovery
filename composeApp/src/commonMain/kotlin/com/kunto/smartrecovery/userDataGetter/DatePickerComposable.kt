package com.kunto.smartrecovery.userDataGetter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.close
import smartrecovery.composeapp.generated.resources.date_of_injury
import smartrecovery.composeapp.generated.resources.done
import smartrecovery.composeapp.generated.resources.weight
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun convertMillisToDate(millis: Long): String {
    val timeInstant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = timeInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    return localDateTime.date.toString()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DatePickerDocked(label: String, initialValue: String, onValueChange: (String) -> Unit)
{
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = LocalDate.parse(initialValue).toEpochDays() * 24 * 60 * 60 * 1000)

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.safeContentPadding()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .safeContentPadding()
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {

                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )

                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = {
                            showDatePicker = false
                            onValueChange(selectedDate)
                        }
                    ) {
                        Text(stringResource(Res.string.done))
                    }

                }
            }
        }
    }
}