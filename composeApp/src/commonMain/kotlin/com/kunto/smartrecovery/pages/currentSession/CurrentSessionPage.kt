package com.kunto.smartrecovery.pages.currentSession

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kunto.smartrecovery.UIComponents.CreateGroupedBarChart
import com.kunto.smartrecovery.Main
import com.kunto.smartrecovery.pages.chooseDevicesPopUp.ChooseBLEDevicesViewModel
import com.kunto.smartrecovery.theming.Blue
import com.kunto.smartrecovery.theming.LightBlue
import com.kunto.smartrecovery.theming.Transparent
import org.jetbrains.compose.resources.stringResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.force_peak
import smartrecovery.composeapp.generated.resources.force_perentage
import smartrecovery.composeapp.generated.resources.time_spent
import smartrecovery.composeapp.generated.resources.total_force
import smartrecovery.composeapp.generated.resources.total_steps


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentSessionPage(viewModel: CurrentSessionViewModel, chosenDevicesViewModel: ChooseBLEDevicesViewModel, navController: NavController)
{
    val uiState by viewModel.uiState.collectAsState()
    val barData by viewModel.barData.collectAsState()
    var sliderPosition by remember { mutableFloatStateOf(0F) }
    val options = remember { listOf(0, 25, 50, 75, 100) }

    // create a shared interactionSource for Slider, its Label, and its thumb component
    val interactionSource = remember { MutableInteractionSource() }

    // wrap everything in a Box so that you can safely color the edges which the inner parts cannot reach
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(Res.string.force_peak)}: ${uiState.maxForce}",
                color = MaterialTheme.colorScheme.inverseSurface
            )
            Text(
                text = "${stringResource(Res.string.total_force)}: ${uiState.totalForce}",
                color = MaterialTheme.colorScheme.inverseSurface
            )
            Text(
                text = "${stringResource(Res.string.total_steps)}: ${uiState.totalSteps}",
                color = MaterialTheme.colorScheme.inverseSurface
            )
            Text(
                text = "${stringResource(Res.string.time_spent)}: ${uiState.timeSpent}",
                color = MaterialTheme.colorScheme.inverseSurface
            )

            Button(
                onClick = {
                    viewModel.stopSession()
                    navController.navigate(Main)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Stop Session")
            }

            /*
            DropdownMenu(
                options,
                labelFormatter = {
                    "${it} %"
                }
            ) {
                viewModel.updateProfile(it)
                sliderPosition = it.toFloat()
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
                            viewModel.updateProfile(it)
                            sliderPosition = it.toFloat()
                        }
                    ) {
                        Text("${it} %", fontSize = 14F.sp)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 20.dp),
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
                    },
                    onValueChangeFinished = {
                        viewModel.updateProfile(sliderPosition)
                    },
                    modifier = Modifier.padding(horizontal = 15.dp),
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

            CreateGroupedBarChart(barData)
        }
    }
}
