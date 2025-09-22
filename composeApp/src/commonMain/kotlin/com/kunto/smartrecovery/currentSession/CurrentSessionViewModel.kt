package com.kunto.smartrecovery.currentSession

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.kunto.smartrecovery.dataModels.CurrSessionDataPacket
import com.kunto.smartrecovery.json.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import network.chaintech.cmpcharts.common.extensions.formatNumber
import network.chaintech.cmpcharts.common.model.Point
import network.chaintech.cmpcharts.ui.barchart.config.BarData
import network.chaintech.cmpcharts.ui.barchart.config.GroupBar


data class SessionUiState(
    val maxForce: Int = 0,
    val totalForce: Int = 0,
    val totalSteps: Int = 0,
    val timeSpent: Long = 0,
    val chartXValues: SnapshotStateList<Float> = mutableStateListOf(),
    val chartYValues: SnapshotStateList<Float> = mutableStateListOf()
)

class CurrentSessionViewModel(private val userProfile: UserProfile) : ViewModel()
{
    private val _uiState = MutableStateFlow<SessionUiState>(SessionUiState())
    private val _barData = MutableStateFlow<MutableList<GroupBar>>(mutableListOf())
    val uiState = _uiState.asStateFlow()
    val barData = _barData.asStateFlow()


    fun updateValues(dataPacket: CurrSessionDataPacket)
    {
        _uiState.update {
            it.copy(
                maxForce = dataPacket.maxForce ?: 0,
                totalForce = dataPacket.totalForce ?: 0,
                totalSteps = dataPacket.totalSteps ?: 0,
                timeSpent = dataPacket.timeSpent ?: 0
            )
        }

        val data = BarData(
            Point(
                barData.value.size.toFloat(),
                dataPacket.chartYValue?.toFloat() ?: 0.0f
            )
        )

        val groupBar = GroupBar(barData.value.size.toString(), listOf(data))

        _barData.update {
            it += groupBar
            it
        }
    }

    fun <T : Number> updateProfile(value: T)
    {
        userProfile.amountOfForcePercentage = value.toInt()
        userProfile.maximumForceOnInjury = 9.81 * userProfile.weight * (userProfile.amountOfForcePercentage / 100.0)
    }

    fun stopSession()
    {
        _uiState.update {
            it.copy(
                maxForce = 0,
                totalForce = 0,
                totalSteps = 0,
                timeSpent = 0
            )
        }
    }
}