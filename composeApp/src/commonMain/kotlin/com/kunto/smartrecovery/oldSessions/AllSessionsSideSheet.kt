package com.kunto.smartrecovery.oldSessions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kunto.smartrecovery.Main
import com.kunto.smartrecovery.backend.sendData.SenderClient
import com.kunto.smartrecovery.dataModels.files.FileData
import com.kunto.smartrecovery.dataModels.files.FileSessionData
import com.kunto.smartrecovery.json.SessionPayload
import com.kunto.smartrecovery.json.UserProfile
import com.kunto.smartrecovery.json.convertToSessions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import org.jetbrains.compose.resources.stringResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.close
import smartrecovery.composeapp.generated.resources.delete
import smartrecovery.composeapp.generated.resources.send_data_to_server
import smartrecovery.composeapp.generated.resources.train_session
import smartrecovery.composeapp.generated.resources.your_old_sessions
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AllSavedSessionsSideSheet(drawerState: DrawerState, viewModel: AllSessionsViewModel, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val allFiles by viewModel.allFiles.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                //drawerContainerColor = Color.LightGray,
                drawerShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                // Sheet content (using ColumnScope)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier
                            .width(300.dp),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Text(
                            text = stringResource(Res.string.your_old_sessions),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        //Spacer(modifier = Modifier.height(16.dp))
                        // Add your sheet content here

                        LazyColumn(
                            state = listState
                        ) {
                            items(allFiles) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 3.dp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp)
                                        ),
                                    horizontalArrangement = Arrangement.SpaceBetween                            ,
                                ) {
                                    var checked by remember { mutableStateOf(false) }
                                    Text(
                                        text = it
                                            .replace("r_combined_force_", "")
                                            .takeWhile { aChar -> aChar != '.' },
                                        modifier = Modifier
                                            .padding(15.dp)
                                    )
                                    Checkbox(
                                        modifier = Modifier
                                            .padding(5.dp),
                                        checked = checked,
                                        onCheckedChange = { isChecked ->
                                            checked = isChecked
                                            when (isChecked) {
                                                true -> viewModel.chooseSession(it)
                                                else -> viewModel.removeSession(it)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        //Spacer(modifier = Modifier.height(16.dp))
                    }
                    Column(
                        modifier = Modifier
                            .width(300.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .safeContentPadding(),
                            onClick = {
                                scope.launch {
                                    val sessionNamesWithFileNamesWithData: List<FileSessionData> =
                                        viewModel.chosenSessionNames
                                            .map { sessionName ->
                                                sessionName.replace("r_combined_force_", "")
                                                    .takeWhile { it != '.' }
                                            }
                                            .map { name ->
                                                FileSessionData(
                                                    sessionName = name,
                                                    sessionCreationDate = viewModel.fileHandler.getFileCreationDate(
                                                        "r_combined_force_${name}.csv"
                                                    )
                                                        ?.format(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET)
                                                        ?: "",
                                                    fileData = viewModel.fileHandler.allFileNamesWithString(
                                                        "_${name}.csv"
                                                    )
                                                        .map {
                                                            FileData(
                                                                it,
                                                                viewModel.fileHandler.getStringData(
                                                                    it
                                                                )
                                                            )
                                                        }
                                                )
                                            }

                                    val sessions = convertToSessions(sessionNamesWithFileNamesWithData)
                                    val payload = SessionPayload(
                                        userId = viewModel.fileHandler.getUserProfile()?.userName
                                            ?: "Could_not_find_user_profile",
                                        sessionsCount = sessions.size,
                                        sessions = sessions
                                    )

                                    val sent = SenderClient.sendData(payload)
                                    println(sent.msg)
                                }
                            }
                        ) {
                            Text(stringResource(Res.string.send_data_to_server))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.deleteChosen()
                                    }
                                }
                            ) {
                                Text(stringResource(Res.string.delete))
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.clearChosen()
                                        drawerState.close()
                                    }
                                }
                            ) {
                                Text(stringResource(Res.string.close))
                            }
                        }
                    }
                }
            }
        },
        content = content
    )
}