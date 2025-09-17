package com.kunto.smartrecovery.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.kunto.smartrecovery.Main
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.all
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


@Composable
fun <T : Any> PermissionPage(navController: NavController, viewModel: PermissionsViewModel, destination: @Serializable T)
{
    val permissionStates by viewModel.permissionStates.collectAsState()
    var workCompleted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.provideOrRequestPermissions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (permissionStates.all { it == PermissionState.Granted }) {
            true -> {
                if (!workCompleted) {
                    navController.navigate(destination)
                    workCompleted = true
                }
            }
            false -> Text("You have to grant the required permissions for the app to work correctly")
        }
    }
}