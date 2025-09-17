package com.example.testing_2.backend.bluetoothDecoding

/*
This file contains the structs from
sensor_data.h.
 */


const val SENSOR_COUNT: Int = 7
const val CALIB_COUNT: Int = SENSOR_COUNT + 7
const val CALIB_SCALER: Int = 1 shl 12


data class sensor_data(var force: Int, var time_max: Int, var time_start: Int, var time_end: Int)


data class adv_force(
    var step_type: Int,
    var f1: Int,
    var f2: Int,
    var f3: Int,
    var f1_time: Int,
    var f2_time: Int,
    var f3_time: Int,
    var esw_time: Int
) {
    var sensor_data: Array<sensor_data> = Array(SENSOR_COUNT){ sensor_data(0, 0, 0, 0) }


    override fun toString(): String {
        val res: ArrayList<String> = ArrayList()

        res.add( step_type.toString() )
        res.add( f1.toString() )
        res.add( f1_time.toString() )
        res.add( f2.toString() )
        res.add( f2_time.toString() )
        res.add( f3.toString() )
        res.add( f3_time.toString() )

        return res.joinToString(",")
    }
}