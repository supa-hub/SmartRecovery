package com.kunto.smartrecovery.userDataGetter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kunto.smartrecovery.UIComponents.AlertDialogTemplate
import com.kunto.smartrecovery.UIComponents.DropdownMenu
import com.kunto.smartrecovery.theming.Blue
import com.kunto.smartrecovery.theming.LightBlue
import com.kunto.smartrecovery.theming.Transparent
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.close
import smartrecovery.composeapp.generated.resources.date_of_casting
import smartrecovery.composeapp.generated.resources.date_of_injury
import smartrecovery.composeapp.generated.resources.force_perentage
import smartrecovery.composeapp.generated.resources.light_mode_person_24
import smartrecovery.composeapp.generated.resources.name
import smartrecovery.composeapp.generated.resources.save
import smartrecovery.composeapp.generated.resources.weight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> UserDataGetterDialog(viewModel: UserDataGetterViewModel, navController: NavController, predecessor: @Serializable T)
{
    val profile by viewModel.profile.collectAsState()
    var openAlertDialog by remember { mutableStateOf(true) }
    var nameValue by remember { mutableStateOf(profile.userName) }
    var weightValue by remember { mutableStateOf(profile.weight.toString()) }
    var sliderPosition by remember { mutableFloatStateOf(profile.amountOfForcePercentage.toFloat()) }
    val options = remember { listOf(0, 25, 50, 75, 100) }

    // create a shared interactionSource for Slider, its Label, and its thumb component
    val interactionSource = remember { MutableInteractionSource() }

    when {
        openAlertDialog -> {
            AlertDialogTemplate(
                onDismissRequest = {
                    openAlertDialog = false
                    navController.navigate(route = predecessor)
                },
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxWidth()
                    .height(500.dp)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .safeContentPadding()
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.light_mode_person_24),
                        contentDescription = "Example Icon",
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    OutlinedTextField(
                        value = nameValue,
                        onValueChange = {
                            nameValue = it
                        },
                        modifier = Modifier
                            .safeContentPadding()
                            .padding(top = 5.dp),

                        label = { Text(stringResource(Res.string.name)) },
                    )

                    OutlinedTextField(
                        value = weightValue,
                        onValueChange = {
                            weightValue = when {
                                it.isEmpty() -> ""
                                else -> (it.toIntOrNull() ?: weightValue).toString()
                            }
                        },
                        modifier = Modifier
                            .safeContentPadding()
                            .padding(top = 5.dp),
                        label = { Text(stringResource(Res.string.weight)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    /*
                    DropdownMenu(
                        options,
                        labelFormatter = {
                            "${it} %"
                        }
                    ) {
                        viewModel.updateProfile(
                            amountOfForcePercentage = it,
                            maximumForceOnInjury = 9.81 * profile.weight * (it.toDouble() / 100.0)
                        )
                    }
                     */
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        options.forEach {
                            Button(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(40.dp),
                                contentPadding = PaddingValues(2.dp),
                                onClick = {
                                    viewModel.updateProfile(
                                        amountOfForcePercentage = it,
                                        maximumForceOnInjury = 9.81 * profile.weight * (it.toDouble() / 100.0)
                                    )
                                    sliderPosition = it.toFloat()
                                }
                            ) {
                                Text("${it} %", fontSize = 14F.sp)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .safeContentPadding(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {

                            Text(
                                text = stringResource(Res.string.force_perentage),
                                modifier = Modifier
                                    .padding(top = 25.dp)
                            )

                            Slider(
                                value = sliderPosition,
                                onValueChange = {
                                    sliderPosition = it
                                    viewModel.updateProfile(
                                        amountOfForcePercentage = it.toInt(),
                                        maximumForceOnInjury = profile.weight * 9.81 * (it.toDouble() / 100.0)
                                    )
                                },
                                onValueChangeFinished = {
                                    Unit
                                },
                                modifier = Modifier
                                    .padding(end = 25.dp, start = 10.dp)
                                    .padding(top = 20.dp),
                                valueRange = 0F..100F,
                                steps = 99,
                                interactionSource = interactionSource,
                                colors = SliderColors(
                                    thumbColor = Blue,
                                    activeTrackColor = Blue,
                                    activeTickColor = Transparent,
                                    inactiveTrackColor = LightBlue,
                                    inactiveTickColor = Transparent,
                                    disabledThumbColor = Transparent,
                                    disabledActiveTrackColor = LightBlue,
                                    disabledActiveTickColor = LightBlue,
                                    disabledInactiveTrackColor = LightBlue,
                                    disabledInactiveTickColor = LightBlue
                                ),
                                thumb = { sliderState ->
                                    Label(
                                        label = { Text("${sliderState.value.toInt()}%") },
                                        interactionSource = interactionSource,
                                    ) {
                                        SliderDefaults.Thumb(
                                            interactionSource = interactionSource
                                        )
                                    }
                                },
                                track = { sliderState ->
                                    SliderDefaults.Track(
                                        sliderState = sliderState,
                                        modifier = Modifier.height(32.dp),
                                        colors = SliderColors(
                                            thumbColor = Blue,
                                            activeTrackColor = Blue,
                                            activeTickColor = Blue,
                                            inactiveTrackColor = LightBlue,
                                            inactiveTickColor = Transparent,
                                            disabledThumbColor = Transparent,
                                            disabledActiveTrackColor = LightBlue,
                                            disabledActiveTickColor = LightBlue,
                                            disabledInactiveTrackColor = LightBlue,
                                            disabledInactiveTickColor = LightBlue
                                        ),
                                        drawTick = { offset, color -> Unit }
                                    )
                                }
                            )
                        }
                    }

                    DatePickerDocked(stringResource(
                        Res.string.date_of_injury),
                        profile.dayOfInjury
                    ) {
                        viewModel.updateProfile(dayOfInjury = it)
                    }

                    DatePickerDocked(
                        stringResource(Res.string.date_of_casting),
                        profile.dayOfCasting
                    ) {
                        viewModel.updateProfile(dayOfCasting = it)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Button(
                            onClick = {
                                openAlertDialog = false
                                navController.navigate(route = predecessor)
                            }
                        ) {
                            Text(stringResource(Res.string.close))
                        }
                        Button(
                            onClick = {
                                openAlertDialog = false
                                viewModel.updateProfile(userName = nameValue, weight = weightValue.toIntOrNull() ?: 0)
                                viewModel.saveProfile()
                                println("Confirmation registered") // Add logic here to handle confirmation.

                                navController.navigate(predecessor)
                            }
                        ) {
                            Text(stringResource(Res.string.save))
                        }
                    }
                }
            }

        }
    }

}