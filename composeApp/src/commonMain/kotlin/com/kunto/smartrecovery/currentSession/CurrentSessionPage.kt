package com.kunto.smartrecovery.currentSession

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import com.kunto.smartrecovery.theming.Blue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kunto.smartrecovery.Main
import com.kunto.smartrecovery.chooseDevicesPopUp.ChooseBLEDevicesViewModel
import com.kunto.smartrecovery.mainPage.MainGroupedBarChart
import com.kunto.smartrecovery.theming.Black
import com.kunto.smartrecovery.theming.LightBlue
import com.kunto.smartrecovery.theming.Transparent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentSessionPage(viewModel: CurrentSessionViewModel, chosenDevicesViewModel: ChooseBLEDevicesViewModel, navController: NavController)
{
    val uiState by viewModel.uiState.collectAsState()
    var sliderPosition by remember { mutableFloatStateOf(0F) }

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
            Text(text = uiState.maxForce.toString(), color = MaterialTheme.colorScheme.inverseSurface)
            Text(text = uiState.totalForce.toString(), color = MaterialTheme.colorScheme.inverseSurface)
            Text(text = uiState.totalSteps.toString(), color = MaterialTheme.colorScheme.inverseSurface)
            Text(text = uiState.timeSpent.toString(), color = MaterialTheme.colorScheme.inverseSurface)

            Button(
                onClick = {
                    viewModel.stopSession()
                    navController.navigate(Main)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Stop Session")
            }

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

            MainGroupedBarChart()
        }
    }
}
