package com.kunto.smartrecovery.chooseDevicesPopUp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kunto.smartrecovery.UIComponents.AlertDialogTemplate
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kunto.smartrecovery.CurrentSession
import com.kunto.smartrecovery.UIComponents.IconButton
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import smartrecovery.composeapp.generated.resources.Continue
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.baseline_add_24


@Composable
fun ChooseDevicesDialog(viewModel: ChooseBLEDevicesViewModel, navController: NavController, sessionName: String)
{
    var openAlertDialog by remember { mutableStateOf(true) }
    val listOfFoundDevices = remember {  viewModel.foundDevices }
    val listState = rememberLazyListState()

    viewModel.collectAvailableDevices()


    AlertDialogTemplate(
        onDismissRequest = {
            openAlertDialog = false
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
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                state = listState
            ) {
                items(listOfFoundDevices) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var checked by remember { mutableStateOf(false) }
                        Text(it.name ?: "Unnamed ble device", fontSize = 22F.sp)
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                checked = isChecked
                                if (isChecked) {
                                    viewModel.chosenDevices += it
                                } else {
                                    viewModel.chosenDevices -= it
                                }
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.connectToDevices(viewModel.chosenDevices)
                    viewModel.startListening(sessionName)
                    navController.navigate(CurrentSession)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .safeContentPadding()
                    .padding(horizontal = 20.dp, vertical = 1.dp)
                    .padding(top = 5.dp, bottom = 10.dp),
            ) {
                IconButton {
                    Icon(
                        imageVector = vectorResource(Res.drawable.baseline_add_24),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(stringResource(Res.string.Continue), fontSize = 22F.sp)
                    Box(Modifier)
                }
            }
        }
    }
}