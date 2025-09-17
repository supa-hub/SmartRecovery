package com.kunto.smartrecovery.dataModels

data class CurrSessionDataPacket(
    val maxForce: Int?,
    val totalForce: Int?,
    val totalSteps: Int?,
    val timeSpent: Long?,
    val chartXValue: Int?,
    val chartYValue: Int?
)
