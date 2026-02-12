package com.kunto.smartrecovery.mainPage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import com.kunto.smartrecovery.CurrentSession
import com.kunto.smartrecovery.Greeting
import com.kunto.smartrecovery.UIComponents.IconButton
import com.kunto.smartrecovery.RequestPermissions
import com.kunto.smartrecovery.Route
import com.kunto.smartrecovery.UIComponents.CreateGroupedBarChart
import com.kunto.smartrecovery.UserProfileGetter
import com.kunto.smartrecovery.oldSessions.AllSavedSessionsSideSheet
import com.kunto.smartrecovery.oldSessions.AllSessionsViewModel
import com.kunto.smartrecovery.theming.Blue
import com.kunto.smartrecovery.theming.LightBlue
import com.kunto.smartrecovery.theming.backgroundColorDark
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources._1_month
import smartrecovery.composeapp.generated.resources._1_week
import smartrecovery.composeapp.generated.resources.all_time
import smartrecovery.composeapp.generated.resources.baseline_add_24
import smartrecovery.composeapp.generated.resources.baseline_menu_24_light
import smartrecovery.composeapp.generated.resources.compose_multiplatform
import smartrecovery.composeapp.generated.resources.dark_mode_person_24
import smartrecovery.composeapp.generated.resources.light_mode_person_24
import smartrecovery.composeapp.generated.resources.logo_dark
import smartrecovery.composeapp.generated.resources.logo_light
import smartrecovery.composeapp.generated.resources.previous_sessions
import smartrecovery.composeapp.generated.resources.test_session
import smartrecovery.composeapp.generated.resources.train_session


@Composable
fun MainPage(viewModel: MainPageViewModel, navController: NavController, darkTheme: Boolean = isSystemInDarkTheme()) {
    var showContent by remember { mutableStateOf(false) }
    val showSheet = mutableStateOf(false)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val barData by viewModel.barData.collectAsState()

    var selectedNavigationIndex by rememberSaveable { mutableIntStateOf(0) }


    val navBarEntries = listOf(
        Pair("week", stringResource(Res.string._1_week)),
        Pair("month", stringResource(Res.string._1_month)),
        Pair("year", stringResource(Res.string.all_time))
    )

    val logo = when (darkTheme) {
        true -> painterResource(Res.drawable.logo_dark)
        false -> painterResource(Res.drawable.logo_light)
    }

    val userIcon = when (darkTheme) {
        true -> vectorResource (Res.drawable.dark_mode_person_24)
        false -> vectorResource (Res.drawable.light_mode_person_24)
    }

    AllSavedSessionsSideSheet(
        drawerState,
        viewModel { AllSessionsViewModel() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { showSheet.value = false })
                    },
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(logo, null)

                Column(
                    modifier = Modifier
                        .safeContentPadding()
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    //.background(MaterialTheme.colorScheme.primaryContainer)
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier
                            .safeContentPadding()
                            .wrapContentSize()
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(RequestPermissions(Route.NewSessionName.ordinal))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .safeContentPadding()
                                .padding(horizontal = 20.dp, vertical = 1.dp)
                                .padding(top = 5.dp),
                        ) {
                            IconButton {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.baseline_add_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.primaryContainer
                                )
                                Text(stringResource(Res.string.train_session), fontSize = 22F.sp)
                                Box(Modifier)
                            }
                        }

                        Button(
                            onClick = {
                                navController.navigate(CurrentSession)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .safeContentPadding()
                                .padding(horizontal = 20.dp, vertical = 1.dp),
                        ) {
                            IconButton {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.baseline_add_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.primaryContainer
                                )
                                Text(stringResource(Res.string.test_session), fontSize = 22F.sp)
                                Box(Modifier)
                            }
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .safeContentPadding()
                                .padding(horizontal = 20.dp, vertical = 1.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.inverseSurface
                            ),
                        ) {
                            IconButton {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.baseline_menu_24_light),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    stringResource(Res.string.previous_sessions),
                                    fontSize = 22F.sp
                                )
                                Box(Modifier)
                            }
                        }
                    }

                    /*
                    AnimatedVisibility(showContent) {
                        val greeting = remember { Greeting().greet() }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(painterResource(Res.drawable.compose_multiplatform), null)
                            Text("Compose: $greeting")
                        }
                    }
                     */

                    CreateGroupedBarChart(barData)

                }
            }

            NavigationBar(
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                navBarEntries.forEachIndexed { index, pair ->
                    NavigationBarItem(
                        selected = selectedNavigationIndex == index,
                        onClick = {
                            viewModel.showActivity(pair.first)
                            selectedNavigationIndex = index
                        },
                        colors = NavigationBarItemColors(
                            selectedIconColor = Color.Transparent,
                            selectedTextColor = Blue,
                            selectedIndicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Transparent,
                            unselectedTextColor = Blue,
                            disabledIconColor = Color.Transparent,
                            disabledTextColor = Color.Transparent
                        ),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person, // any icon
                                contentDescription = null,
                                tint = Color.Transparent //if (darkTheme) backgroundColorDark else LightBlue
                            )
                        },
                        label = { Text(pair.second, fontSize = 16F.sp) }
                    )
                }
            }

            Image(
                userIcon,
                null,
                modifier = Modifier
                    .padding(top = 45.dp)
                    .width(80.dp)
                    .height(80.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { navController.navigate(UserProfileGetter) })
                    }
                    .align(Alignment.TopStart)
            )
        }
    }
}

