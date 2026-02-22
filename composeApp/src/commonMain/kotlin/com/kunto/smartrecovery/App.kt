package com.kunto.smartrecovery

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kunto.smartrecovery.bluetooth.ConnectionHandler
import com.kunto.smartrecovery.pages.chooseDevicesPopUp.ChooseBLEDevicesViewModel
import com.kunto.smartrecovery.pages.chooseDevicesPopUp.ChooseDevicesDialog
import com.kunto.smartrecovery.pages.currentSession.CurrentSessionPage
import com.kunto.smartrecovery.pages.currentSession.CurrentSessionViewModel
import com.kunto.smartrecovery.pages.mainPage.MainPage
import com.kunto.smartrecovery.pages.mainPage.MainPageViewModel
import com.kunto.smartrecovery.pages.newSessionName.NewSessionNamePopUp
import com.kunto.smartrecovery.pages.permissions.PermissionPage
import com.kunto.smartrecovery.pages.permissions.PermissionsViewModel
import com.kunto.smartrecovery.theming.MyApplicationTheme
import com.kunto.smartrecovery.userDataGetter.UserDataGetterDialog
import com.kunto.smartrecovery.userDataGetter.UserDataGetterViewModel
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import org.jetbrains.compose.ui.tooling.preview.Preview


/**
 * Handles the navigation between windows in the app.
 * Also is the entry point for the app.
 */
@Composable
@Preview
fun App() {
    MyApplicationTheme {
        val navController = rememberNavController()

        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val controller: PermissionsController = remember(factory) { factory.createPermissionsController() }
        BindEffect(controller)

        val handler = remember { ConnectionHandler() }
        val userProfile by viewModel { UserDataGetterViewModel() }.profile.collectAsState()
        val currSession = viewModel { CurrentSessionViewModel(userProfile, handler) }

        /*
         * Uses the standard Kotlin multiplatform navigation style.
         * You can read more about it here: https://kotlinlang.org/docs/multiplatform/compose-navigation.html#basic-navigation-example
         */
        NavHost(
            navController = navController,
            startDestination = Main
        ) {
            composable<Main> {
                MainPage(
                    viewModel { MainPageViewModel() },
                    navController
                )
            }
            composable<CurrentSession> {
                CurrentSessionPage(
                    currSession,
                    viewModel { ChooseBLEDevicesViewModel(handler) { currSession.updateValues(it) } },
                    navController
                )
            }
            composable<UserProfileGetter> {
                UserDataGetterDialog(viewModel { UserDataGetterViewModel() }, navController, Main)
            }
            composable<ChooseBLEDevicesPopUp> { backStackEntry ->
                val chooseBLEDevicesPopUp: ChooseBLEDevicesPopUp = backStackEntry.toRoute()

                ChooseDevicesDialog(viewModel {
                    ChooseBLEDevicesViewModel(handler) {
                        currSession.updateValues(
                            it
                        )
                    }
                }, navController, chooseBLEDevicesPopUp.sessionName)
            }
            composable<NewSessionName> {
                NewSessionNamePopUp(navController, Main)
            }
            composable<RequestPermissions> { backStackEntry ->
                val requestPermissions: RequestPermissions = backStackEntry.toRoute()

                when (Route.entries.getOrElse(requestPermissions.destination) { Route.Main }) {
                    Route.Main -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        Main
                    )

                    Route.AllSavedSessions -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        AllSavedSessions
                    )

                    Route.ChooseBLEDevicesPopUp -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        ChooseBLEDevicesPopUp()
                    )

                    Route.CurrentSession -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        CurrentSession
                    )

                    Route.NewSessionName -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        NewSessionName
                    )

                    Route.UserProfileGetter -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        UserProfileGetter
                    )

                    Route.RequestPermissions -> PermissionPage(
                        navController,
                        viewModel { PermissionsViewModel(controller) },
                        Main
                    )
                }
            }
        }
    }
}