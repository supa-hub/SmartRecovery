package com.kunto.smartrecovery.newSessionName


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.enter_the_new_sessions_name
import smartrecovery.composeapp.generated.resources.your_old_sessions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> NewSessionNamePopUp(navController: NavController, predecessor: @Serializable T)
{
    var sessionName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = sessionName,
            onValueChange = {
                sessionName = it
            },
            modifier = Modifier
                .safeContentPadding()
                .padding(top = 5.dp),

            label = { Text(stringResource(Res.string.enter_the_new_sessions_name)) },
        )
    }
}