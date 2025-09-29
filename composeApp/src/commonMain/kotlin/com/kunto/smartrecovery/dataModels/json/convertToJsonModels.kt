package com.kunto.smartrecovery.json

import com.kunto.smartrecovery.dataModels.files.FileSessionData
import com.kunto.smartrecovery.getPlatform
import kotlinx.coroutines.*


suspend fun convertToSessions(sessionNamesWithFileNamesWithData: List<FileSessionData>): List<Session> =
    coroutineScope {
        // asynchronously convert the data into the data classes
        val jsonData: List<Triple<String, String, List<Pair<String, List<SessionDataTypes>>>>> = sessionNamesWithFileNamesWithData.map {
            Triple(
                it.sessionName,
                it.sessionCreationDate,
                it.fileData.map { data ->
                    when {
                        data.fileName.contains("sensor_force") -> async { Pair(data.fileName, convertToSensorForce(data.data)) }
                        data.fileName.contains("step_data") -> async { Pair(data.fileName, convertToStepData(data.data)) }
                        data.fileName.contains("combined_force") -> async { Pair(data.fileName, convertToCombinedForce(data.data)) }
                        else -> async { Pair("", listOf()) }
                    }
                }.awaitAll()
            )
        }

        // combine the data into Sessions
        val sessions: List<Session> = jsonData.map {
            async {
                val ses = Session(
                    sessionName = it.first,
                    sessionCreationDate = it.second,
                    rightSensorForce = listOf(),
                    rightStepData = listOf(),
                    rightCombinedForce = listOf(),
                    leftSensorForce = listOf(),
                    leftStepData = listOf(),
                    leftCombinedForce = listOf()
                )

                for (aValuePair in it.third) {
                    when {
                        aValuePair.first.contains("r_sensor_force") -> ses.rightSensorForce = aValuePair.second as List<SensorForce>
                        aValuePair.first.contains("r_step_data") -> ses.rightStepData = aValuePair.second as List<StepData>
                        aValuePair.first.contains("r_combined_force") -> ses.rightCombinedForce = aValuePair.second as List<CombinedForce>
                        aValuePair.first.contains("l_sensor_force") -> ses.leftSensorForce = aValuePair.second as List<SensorForce>
                        aValuePair.first.contains("l_step_data") -> ses.leftStepData = aValuePair.second as List<StepData>
                        aValuePair.first.contains("l_combined_force") -> ses.leftCombinedForce = aValuePair.second as List<CombinedForce>
                        else -> Unit
                    }
                }

                ses
            }
        }.awaitAll()

        sessions
    }


private fun convertToSensorForce(data: Collection<List<String>>): List<SensorForce>
{
    val res = data.drop(1)
        .filter { it.all { it.isNotEmpty() } }
        .map {
            SensorForce(
                s0 = it[0].toInt(),
                s1 = it[1].toInt(),
                s2 = it[2].toInt(),
                s3 = it[3].toInt(),
                s4 = it[4].toInt(),
                s5 = it[5].toInt(),
                s6 = it[6].toInt(),
                timeStamp_2 = it[7].toInt()
            )
        }
    /*
    val dropped = data.drop(1)

    val res1 = SensorForce(
        s0 = dropped.map { it[0] },
        s1 = dropped.map { it[1] },
        s2 = dropped.map { it[2] },
        s3 = dropped.map { it[3] },
        s4 = dropped.map { it[4] },
        s5 = dropped.map { it[5] },
        s6 = dropped.map { it[6] },
        timeStamp_2 = dropped.map { it[7] }
    )
     */

    return res
}


private fun convertToStepData(data: Collection<List<String>>): List<StepData>
{
    val res = data.drop(1)
        .filter { it.all { it.isNotEmpty() } }
        .map {
            StepData(
                bluetoothAddress = it[0],
                labelID = it[1].toInt(),
                compIcode0 = it[2].toInt(),
                compIcode1 = it[3].toInt(),
                deviceId = it[4].toInt(),
                uuid = it[5].toInt(),
                handedness = it[6].toInt(),
                size = it[7].toInt(),
                eswTimeCode = it[8],
                prodDay = it[9].toInt(),
                prodMonth = it[10].toInt(),
                prodYear = it[11].toInt(),
                battery = it[12].toInt(),
                ltsCnt = it[13].toInt(),
                errCode = it[14].toInt(),
                stepType = it[15].toInt(),
                f1 = it[16].toInt(),
                f1Time = it[17].toInt(),
                f2 = it[18].toInt(),
                f2Time = it[19].toInt(),
                f3 = it[20].toInt(),
                f3Time = it[21].toInt()
            )
        }

    val dropped = data.drop(1)

    return res
}

private fun convertToCombinedForce(data: Collection<List<String>>): List<CombinedForce>
{
    // data.drop(1).filter { it.isNotEmpty() }.forEach { getPlatform().log(it.toString()) }
    val res = data.drop(1)
        .filter { it.all { it.isNotEmpty() } }
        .map {
            CombinedForce(
                totalForce1 = it.getOrElse(0){ "0" }.toInt(),
                totalForce2 = it.getOrElse(2){ "0" }.toInt(),
                timeStamp_2 = it.getOrElse(3){ "0" }.toInt()
            )
        }

    return res
}