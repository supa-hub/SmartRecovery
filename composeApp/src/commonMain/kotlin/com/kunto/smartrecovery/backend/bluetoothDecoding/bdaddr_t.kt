package com.example.testing_2.backend.bluetoothDecoding

import kotlinx.serialization.Serializable

@Serializable
class bdaddr_t(var s: String = "")
{
    var b: Array<UByte> = Array<UByte>(6) { 0u }
}


/*
 * Construct a bdaddr_t from the given address string
 */
fun ba2str(address: String): bdaddr_t
{
    val returnVal = bdaddr_t(address)

    // Split the address by ":"
    val hexParts = address.split(":")
    // Map each part to its UByte value and create a UByteArray
    returnVal.b = hexParts.map { it.toInt(16).toUByte() }.toTypedArray()
    return returnVal
}