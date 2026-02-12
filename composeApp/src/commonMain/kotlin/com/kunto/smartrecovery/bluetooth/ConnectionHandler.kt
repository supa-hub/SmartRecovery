package com.kunto.smartrecovery.bluetooth

import com.example.testing_2.backend.bluetoothDecoding.SENSOR_COUNT
import com.example.testing_2.backend.bluetoothDecoding.adv
import com.example.testing_2.backend.bluetoothDecoding.adv_force
import com.example.testing_2.backend.bluetoothDecoding.bdaddr_t
import com.example.testing_2.backend.bluetoothDecoding.ble
import com.example.testing_2.backend.bluetoothDecoding.decode_adv
import com.example.testing_2.backend.bluetoothDecoding.decode_force
import com.example.testing_2.backend.bluetoothDecoding.values

import com.juul.kable.Bluetooth
import com.juul.kable.ExperimentalApi
import com.juul.kable.Filter
import com.juul.kable.Filter.Address
import com.juul.kable.ManufacturerData
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.characteristic
import com.juul.kable.characteristicOf
import com.juul.kable.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import kotlin.text.toInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * Can be used to transport relevant data and operations related to bluetooth
 * to different viewmodels.
 */
@OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
class ConnectionHandler {
    companion object {
        val service = "bebc7d81-0739-4223-9ede-17b82cf93287"
        val UUID_STEP_DATA = "bebc7d82-0739-4223-9ede-17b82cf93287"
        //val UUID_STEP_DATA = "8fec230a-2e54-23be-e822-04c21cc1d42a"
        val UUID_SENSOR_FORCE = "bebc7d83-0739-4223-9ede-17b82cf93287"
        val UUID_COMBINED_FORCE = "bebc7d84-0739-4223-9ede-17b82cf93287"


        val serviceUuid = Uuid.parseHexDash(service)
        val stepDataCharasteristic = characteristicOf(serviceUuid, Uuid.parseHexDash(UUID_STEP_DATA))
        val sensorForceCharasteristic = characteristicOf(serviceUuid, Uuid.parseHexDash(UUID_SENSOR_FORCE))
        val combinedForceCharasteristic = characteristicOf(serviceUuid, Uuid.parseHexDash(UUID_COMBINED_FORCE))

        val scanner = Scanner {
            filters {
                match {
                    /*
                    services = listOf(
                        serviceUuid
                    )
                     */
                }
            }
        }
    }
    private var ble = ble(
        bdaddr = bdaddr_t(),
        adv_force = adv_force(0, 0, 0, 0, 0, 0, 0, 0),
    )

    var chosenPeripherals = listOf<Peripheral>()

    fun listenToAdvertisements() = scanner.advertisements

    fun obeservePeripherals(
        coroutineScope: CoroutineScope,
        actionOnStepData: (adv) -> Unit,
        actionOnCombinedData: (values) -> Unit,
        actionOnSensorForceData: (values) -> Unit,
        finalizerAction: () -> Unit
        ) =
        coroutineScope.async {
            chosenPeripherals.map {
                val stepDataObservation = it.observe(stepDataCharasteristic)
                val sensorForceObservation = it.observe(sensorForceCharasteristic)
                val combinedForceObservation = it.observe(combinedForceCharasteristic)
                var adv = adv(0u, ble)

                launch {
                    combinedForceObservation.collect { data ->
                        val values = values(0u, 0)
                        decode_force(
                            data.map { aByte -> aByte.toInt() and 0xFF }.toIntArray(),
                            values,
                            3
                        )

                        actionOnCombinedData(values)
                    }
                }

                launch {
                    sensorForceObservation.collect { data ->
                        val values = values(0u, 0)
                        decode_force(data.map { aByte -> aByte.toInt() and 0xFF }.toIntArray(), values, SENSOR_COUNT)


                        actionOnSensorForceData(values)
                    }
                }

                launch {
                    stepDataObservation.collect { data ->
                        decode_adv(data.map { aByte -> aByte.toInt() and 0xFF }.toIntArray(), adv)

                        if (data[17].toInt() == 3) {
                            actionOnStepData(adv)

                            ble = ble(
                                bdaddr = bdaddr_t(),
                                adv_force = adv_force(0, 0, 0, 0, 0, 0, 0, 0),
                            )
                            adv = adv(0u, ble)

                            finalizerAction()
                        }
                    }
                }
            }
        }


    fun disconnectAll()
    {
        chosenPeripherals.dropWhile {
            try {
                it.close()
                true
            }
            catch (e: Exception) {
                false
            }
        }
    }
}