package com.kunto.smartrecovery.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kunto.smartrecovery.Platform
import com.kunto.smartrecovery.getPlatform
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_CONNECT
import kotlinx.coroutines.flow.MutableStateFlow
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_LE
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_SCAN
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import dev.icerock.moko.permissions.location.LOCATION
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PermissionsViewModel(val permissionsController: PermissionsController) : ViewModel()
{
    private val permissionTypes = arrayOf(
        Permission.LOCATION,
        Permission.COARSE_LOCATION,
        Permission.BLUETOOTH_LE,
        Permission.BLUETOOTH_SCAN,
        Permission.BLUETOOTH_CONNECT,
    )
    private val _permissionStates = MutableStateFlow(Array(permissionTypes.size) { PermissionState.NotDetermined })
    val permissionStates = _permissionStates.asStateFlow()

    init {
        viewModelScope.launch {
            _permissionStates.update {
                permissionTypes.map { permissionsController.getPermissionState(it) }
                    .toTypedArray()
            }
            println(_permissionStates)
        }
    }

    fun provideOrRequestPermissions()
    {
        if (getPlatform().isAndroid()) {
            viewModelScope.launch {
                val auxPermissionStates = Array(permissionTypes.size) { PermissionState.NotDetermined }

                permissionTypes.mapIndexed { idx, perm ->
                    try {
                        permissionsController.providePermission(perm)
                        auxPermissionStates[idx] = PermissionState.Granted
                    } catch (e: DeniedAlwaysException) {
                        auxPermissionStates[idx] = PermissionState.DeniedAlways
                    } catch (e: DeniedException) {
                        auxPermissionStates[idx] = PermissionState.Denied
                    } catch (e: RequestCanceledException) {
                        e.printStackTrace()
                    }
                }
                _permissionStates.update { auxPermissionStates }
            }
        }
        else if (getPlatform().isIOS()) {
            _permissionStates.update { Array(permissionTypes.size) { PermissionState.Granted } }
        }
    }
}