package com.kunto.smartrecovery.json

import kotlinx.serialization.Serializable


sealed interface SessionDataTypes


@Serializable
data class SensorForce(
    val s0: Int,
    val s1: Int,
    val s2: Int,
    val s3: Int,
    val s4: Int,
    val s5: Int,
    val s6: Int,
    val timeStamp_2: Int
) : SessionDataTypes

@Serializable
data class StepData(
    val bluetoothAddress: String,    /* Ble MAC address */
    val labelID: Int,      /* label id thats on the device */
    val compIcode0: Int,   /* Adv data Company Identifier Code */
    val compIcode1: Int,   /* Adv data Company Identifier Code */
    val deviceId: Int,     /* Device identifier number */
    val uuid: Int,          /* Service Class UUID */
    val handedness: Int,    /* Smart Insole handedness, 1 is right and 0 is left */
    val size: Int,          /* Smart Insole size */
    val eswTimeCode: String,
    val prodDay: Int,      /* Production date: day */
    val prodMonth: Int,    /* Production date: month */
    val prodYear: Int,     /* Production date: year */
    val battery: Int,       /* Smart Insole battery capacity */
    val ltsCnt: Int,       /* Lifetime step count of the Smart Insole */
    val errCode: Int,      /* internal error code */
    val stepType: Int,
    val f1: Int,
    val f1Time: Int,
    val f2: Int,
    val f2Time: Int,
    val f3: Int,
    val f3Time: Int
) : SessionDataTypes

@Serializable
data class CombinedForce(
    val totalForce1: Int,
    val totalForce2: Int,
    val timeStamp_2: Int
) : SessionDataTypes

@Serializable
data class Session(
    val sessionName: String,
    val sessionCreationDate: String, // ISO 8601 date string
    var rightSensorForce: List<SensorForce>,
    var rightStepData: List<StepData>,
    var rightCombinedForce: List<CombinedForce>,
    var leftSensorForce: List<SensorForce>,
    var leftStepData: List<StepData>,
    var leftCombinedForce: List<CombinedForce>
)

@Serializable
data class SessionPayload(
    val userId: String,
    val sessionsCount: Int,
    val sessions: List<Session>
)

