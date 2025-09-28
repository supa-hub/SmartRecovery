package com.kunto.smartrecovery.chooseDevicesPopUp

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testing_2.backend.bluetoothDecoding.adv
import com.example.testing_2.backend.bluetoothDecoding.adv_force
import com.example.testing_2.backend.bluetoothDecoding.ble
import com.example.testing_2.backend.bluetoothDecoding.decode_adv
import com.example.testing_2.backend.bluetoothDecoding.label_id
import com.juul.kable.Advertisement
import com.juul.kable.Peripheral
import com.juul.kable.PlatformAdvertisement
import com.kunto.smartrecovery.backend.filehandling.FileHandler
import com.kunto.smartrecovery.bluetooth.ConnectionHandler
import com.kunto.smartrecovery.currentSession.CurrentSessionViewModel
import com.kunto.smartrecovery.dataModels.CurrSessionDataPacket
import com.kunto.smartrecovery.getPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.io.bytestring.hexToByteString
import okio.Buffer
import okio.BufferedSink
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
class ChooseBLEDevicesViewModel(private val handler: ConnectionHandler, private val currSessionAction: (CurrSessionDataPacket) -> Unit) : ViewModel()
{
    companion object {
        val fileHandler = FileHandler()
    }
    val foundDevices = mutableStateListOf<PlatformAdvertisement>()
    val chosenDevices = mutableListOf<PlatformAdvertisement>()

    private var sessionTotalSteps = 0
    private var sessionMaxForce = 0
    private var sessionTotalForce = 0
    private var auxSessionTotalSteps = 0

    fun collectAvailableDevices() =
        viewModelScope.launch {
            foundDevices.clear()
            chosenDevices.clear()


                val devicesFlow = handler.listenToAdvertisements()
                devicesFlow.collect {
                    try {
                        val a = it
                        val b = a.manufacturerData?.data ?: byteArrayOf()


                        val testBle = ble(adv_force = adv_force(0, 0, 0, 0, 0, 0, 0, 0))
                        val testAdv = adv(0u, testBle)
                        val test = decode_adv(b.map { it.toInt() and 0xFF }.toIntArray(), testAdv)

                        getPlatform().log(testAdv.ble.toString())
                        getPlatform().log(it.identifier.toString())
                        if (b.size > 3 && (label_id(it.identifier.toString()) == 4695  || it.identifier.toString() == "de10a0a6-4cfd-5bfe-189a-f35492cab6ed")) {
                            val c = a.peripheralName
                            val d = a.name

                            if (!foundDevices.any { curr -> curr.identifier == it.identifier }) {
                                foundDevices += it
                            }
                        }
                    }
                    catch (e: Exception) { }
                }

        }

    fun connectToDevices(advertisements: List<Advertisement>) =
        viewModelScope.async {
            val peripherals = advertisements.map { Peripheral(it) }
            handler.chosenPeripherals = peripherals
            peripherals.map { it.connect() }
        }

    fun connectedPeripherals() = handler.chosenPeripherals

    fun startListening(sessionName: String): Deferred<List<Job>>
    {
        val stepData = fileHandler.parentDir / "r_step_data_${sessionName}.csv".toPath()
        val combinedForce = fileHandler.parentDir / "r_combined_force_${sessionName}.csv".toPath()
        val sensorForce = fileHandler.parentDir / "r_session_force_${sessionName}.csv".toPath()

        val stepDataSink = FileSystem.SYSTEM.sink(stepData).buffer()
        val combinedForceSink = FileSystem.SYSTEM.sink(combinedForce).buffer()
        val sensorForceSink = FileSystem.SYSTEM.sink(sensorForce).buffer()

        return handler.obeservePeripherals(
            coroutineScope = viewModelScope,
            actionOnStepData = {
                if ( auxSessionTotalSteps == 0 ) {
                    auxSessionTotalSteps = it.ble.lts_cnt - 1  // we add the -1 so at the first step we correctly have the number 1
                }
                val totalSessionSteps = it.ble.lts_cnt - auxSessionTotalSteps
                sessionTotalSteps = totalSessionSteps
                val currMax = it.ble.adv_force.f1

                stepDataSink.write(("${it.ble}, ${it.ble.adv_force}\n").encodeUtf8())
                currSessionAction(
                    CurrSessionDataPacket(
                        sessionMaxForce,
                        sessionTotalForce,
                        sessionTotalSteps,
                        null,
                        totalSessionSteps,
                        currMax
                    )
                )
            },
            actionOnCombinedData = {
                val newAux = IntArray(3 + 1)
                newAux[0] = it.values[0]
                newAux[2] = it.values[1]
                newAux[newAux.size - 1] = it.time.toInt()

                combinedForceSink.write(newAux.joinToString(",", postfix = "\n").encodeUtf8())
                sessionTotalForce += it.values[2]
            },
            actionOnSensorForceData = {
                // to also add the timestamp 2, we have to create a new aux array with larger size
                val newAux = IntArray(it.values.size + 1)
                it.values.copyInto(newAux, 0, 0)
                newAux[newAux.size - 1] = it.time.toInt()

                sensorForceSink.write(newAux.joinToString(",", postfix = "\n").encodeUtf8())
                sessionTotalForce += it.values[2]
            },
            finalizerAction = {
                stepDataSink.flush()
                combinedForceSink.flush()
                sensorForceSink.flush()
            }
        )
    }
}
