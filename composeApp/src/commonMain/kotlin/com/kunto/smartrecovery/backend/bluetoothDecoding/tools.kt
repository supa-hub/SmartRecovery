package com.example.testing_2.backend.bluetoothDecoding

/*
This file contains the structs and functions from
tools.h and tools.c.

NOTE: the struct <val> was renamed to <values>
*/

val cnt: IntArray = IntArray(4)


class values(var time: UInt /* In the docs this is Timestamp 2 */, var cnt: Int)
{
    val values: IntArray = IntArray(SENSOR_COUNT)
}


data class adv(var state: UInt, var ble: ble)
{
    val fill: Array<IntArray> = Array(2){ IntArray(31) }  /* The Ints will contain unsigned byte values */
    val prev: Array<IntArray> = Array(2){ IntArray(31) }  /* The Ints will contain unsigned byte values */

    override fun equals(other: Any?): Boolean {
        if ( other is adv) {
            return this.ble.bdaddr.b.contentEquals(other.ble.bdaddr.b)
        }
        return super.equals(other)
    }

}


/*
Based on djb2 http://www.cse.yorku.ca/~oz/hash.html
*/
private fun hash(data: String): Long
{
    var hash: Long = 5381
    var c: Int = 0

    var idx: Int = 0
    while ( idx < data.length ) {
        c = data[idx].code
        hash = (( hash shl 5 ) + hash) + c   /* hash * 33 + c */

        idx++
    }

    return hash
}



/*
/* Mac address format "CB:CA:C7:15:D2:27" */
 */
fun label_id(data: String): Int
{
    var tmp: Long = hash(data)

    if (tmp < 0) {
        tmp = -tmp
    }

    return (tmp % 10000).toInt()
}


fun decode_force(data: IntArray, values: values, cnt: Int): Unit
{
    var tmp: Short = 0
    var i: Int = 0

    values.cnt = cnt

    for ( j in 0..<(cnt * 2) step 2 ) {
        tmp = ( ( data[j + 0] shl 8 ) or data[j + 1] ).toShort()

        values.values[j / 2] = tmp.toInt()
        i += 2
    }

    values.time = ( (data[i + 0].toUInt() shl 24)
            or (data[i + 1].toUInt() shl 16)
            or (data[i + 2].toUInt() shl 8)
            or (data[i + 3].toUInt()) ).toUInt()
}


private fun adv_force_to_string(a: adv_force, device_id: Int, s: MutableList<String>?): Unit
{
    val sum: IntArray = IntArray(SENSOR_COUNT)
    var i: Int = 0

    if ( s === null ) {
        return
    }

    s.add("Step type                   ${a.step_type}\n")
    s.add("F1 force (N) time (ms)      ${a.f1.toString().padEnd(4)} ${a.f1_time.toString().padEnd(4)}\n")
    s.add("F2 force (N) time (ms)      ${a.f2.toString().padEnd(4)} ${a.f2_time.toString().padEnd(4)}\n")
    s.add("F3 force (N) time (ms)      ${a.f3.toString().padEnd(4)} ${a.f3_time.toString().padEnd(4)}\n")

    if (device_id == 52000) {
        s.add("Production mode interface\n")
    }
    if (device_id == 52001) {
        s.add("Production mode R&D interface\n")
    }
    if (device_id == 52002) {
        s.add("Calibration mode interface\n")
    }
    if (device_id == 52003) {
        s.add("Calibration mode R&D interface\n")
    }

    if (device_id == 52000 || device_id == 52001) {
        s.add("Sensor                  id  N   s   m   e\n")
    } else {
        s.add("Sensor calibration      id  SUM     m   e\n")
    }

    if (device_id == 52000 || device_id == 52001) {
        for (i in a.sensor_data.indices) {
            s.add("${i.toString().padStart(26)}  " +
                    "${a.sensor_data[i].force.toString().padStart(26)} " +
                    "${a.sensor_data[i].time_start.toString().padEnd(3)} " +
                    "${a.sensor_data[i].time_max.toString().padEnd(3)} " +
                    "${a.sensor_data[i].time_end.toString().padEnd(3)}\n")
        }
    } else {
        adv_force_to_sum(a, sum)
        for (i in a.sensor_data.indices) {
            s.add("${i.toString().padStart(26)}  " +
                    "${sum[i].toString().padEnd(6)}  " +
                    "${a.sensor_data[i].time_max.toString().padEnd(3)} " +
                    "${a.sensor_data[i].time_end.toString().padEnd(3)}\n")
        }
    }
    s.add("\n")
}




fun decode_adv(data: IntArray, adv: adv): Int
{
    /* Part 0. NOTE SoftDevice set 7 bytes before our
     * data, see encoding format from embedded-sw/advertising.c */
    if (data[17] == 0) {

        /*
        adv.ble = ble(
            bdaddr = bdaddr_t(),
            adv_force = adv_force(0, 0, 0, 0, 0, 0, 0, 0),
        )

        adv.fill.forEach { it.fill(0) }
        adv.prev.forEach { it.fill(0) }
        */

        cnt[0] = data[18]
        adv.fill[0][0] = 0x02        /* Len */
        adv.fill[0][1] = 0x01        /* Type */
        adv.fill[0][2] = 0x06        /* Data */

        adv.fill[0][3] = 0x1B        /* Len */
        adv.fill[0][4] = 0xFF        /* Type */
        adv.fill[0][5] = 0xFF        /* Data.. */
        adv.fill[0][6] = 0xFF

        /*
        if ( cnt[0] > adv.fill[1].size - 7 ) {
            return 0
        }

         */
        data.copyInto(adv.fill[0], 7, 0, cnt[0])
        return -1
    }

    if (data[17] == 1) {
        cnt[1] = data[18]

        /* ESW time is send using bits of Company Identifier Code, see
         * embedded-sw/advertising.c */
        adv.fill[0][5] = data[cnt[1] - 2]
        adv.fill[0][6] = data[cnt[1] - 1]

        //memcpy(&adv.fill[0][7 + cnt[0]], data, cnt[1] - 2)
        /*
        if ( cnt[1] - 2 > adv.fill[1].size - 7 - cnt[0] ) {
            return 0
        }

         */
        data.copyInto(adv.fill[0], 7 + cnt[0], 0, cnt[1] - 2)
        return -1
    }

    /* Part 1. NOTE SoftDevice set 7 bytes before our
     * data, see encoding format from embedded-sw/advertising.c */
    if (data[17] == 2) {
        cnt[2] = data[18]
        adv.fill[1][0] = 0x02        /* Len */
        adv.fill[1][1] = 0x01        /* Type */
        adv.fill[1][2] = 0x06        /* Data */

        adv.fill[1][3] = 0x1B        /* Len */
        adv.fill[1][4] = 0xFF        /* Type */
        adv.fill[1][5] = 0xFF        /* Data.. */
        adv.fill[1][6] = 0xFF

        //memcpy(&adv.fill[1][7 + 0], data, cnt[2])
        /*
        if ( cnt[2] > adv.fill[1].size - 7 ) {
            return 0
        }

         */
        data.copyInto(adv.fill[1], 7, 0, cnt[2])
        return -1
    }


    cnt[3] = data[18]
    // memcpy(&adv.fill[1][7 + cnt[2]], data, cnt[3])
    /*
    if ( cnt[3] > adv.fill[1].size - 7 - cnt[2] ) {
        return 0
    }

     */
    data.copyInto(adv.fill[1], 7 + cnt[2], 0, cnt[3])

    if (decode_adv_data(adv.ble, adv.fill[0], adv.fill[1]) == -1) {
        return -1
    }

    return 0
}

