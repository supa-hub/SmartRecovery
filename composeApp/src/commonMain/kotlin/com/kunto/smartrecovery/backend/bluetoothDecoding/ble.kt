package com.example.testing_2.backend.bluetoothDecoding

/*
This file contains the structs and functions from
ble.h and ble.c.
*/

data class ble(
    var bdaddr: bdaddr_t = bdaddr_t(),    /* Ble MAC address */
    var comp_icode0: Int = 0,   /* Adv data Company Identifier Code */
    var comp_icode1: Int = 0,   /* Adv data Company Identifier Code */
    var device_id: Int = 0,     /* Device identifier number */
    var handedness: Int = 0,    /* Smart Insole handedness, 1 is right and 0 is left */
    var size: Int = 0,          /* Smart Insole size */
    var prod_day: Int = 0,      /* Production date: day */
    var prod_month: Int = 0,    /* Production date: month */
    var prod_year: Int = 0,     /* Production date: year */
    var lts_cnt: Int = 0,       /* Lifetime step count of the Smart Insole */
    var battery: Int = 0,       /* Smart Insole battery capacity */
    var err_code: Int = 0,      /* internal error code */
    var uuid: Int = 0,          /* Service Class UUID */
    var device_label_id: Int = 0,  /* label id thats on the device */
    var adv_force: adv_force,
    var diag_id: Int = 0        /* Device identifier number */
) {
    var zero_level: LongArray = LongArray(SENSOR_COUNT)
    var calib_prm: LongArray = LongArray(CALIB_COUNT)
    var boot_cnt: Int = 0
    var error_cnt: Int = 0
    var sum: IntArray = IntArray(SENSOR_COUNT)   /* Sum of raw sample */
    var version: CharArray = CharArray(9)   /* GIT version hash */


    override fun toString(): String
    {
        val res: ArrayList<String> = ArrayList()

        res.add( bdaddr.s )
        res.add( device_label_id.toString() )
        res.add( comp_icode0.toString() )
        res.add( comp_icode1.toString() )
        res.add( device_id.toString() )
        res.add( uuid.toString() )
        res.add( handedness.toString() )
        res.add( size.toString() )
        res.add( (comp_icode0 and 0x0FFF).toString() )
        res.add( prod_day.toString() )
        res.add( prod_month.toString() )
        res.add( prod_year.toString() )
        res.add( battery.toString() )
        res.add( lts_cnt.toString() )
        res.add( err_code.toString() )

        return res.joinToString(",")
    }
}


fun reset_ble(ble: ble): Unit
{
    ble.bdaddr = bdaddr_t()
    ble.comp_icode0 = 0
    ble.comp_icode1 = 0
    ble.device_id = 0
    ble.handedness = 0
    ble.size = 0
    ble.prod_day = 0
    ble.prod_month = 0
    ble.prod_year = 0
    ble.lts_cnt = 0
    ble.battery = 0
    ble.err_code = 0
    ble.uuid = 0
    ble.device_label_id = 0

    ble.adv_force.f1 = 0
    ble.adv_force.f2 = 0
    ble.adv_force.f3 = 0
    ble.adv_force.f1_time = 0
    ble.adv_force.f2_time = 0
    ble.adv_force.f3_time = 0
    ble.adv_force.esw_time = 0
    ble.adv_force.step_type = 0
    ble.adv_force.sensor_data.forEach {
        it.force = 0
        it.time_start = 0
        it.time_end = 0
        it.time_max = 0
    }

    ble.diag_id = 0
    ble.zero_level.fill(0, 0, SENSOR_COUNT )
    ble.calib_prm.fill(0, 0, CALIB_COUNT )
    ble.boot_cnt = 0
    ble.error_cnt = 0
    ble.sum.fill(0, 0, SENSOR_COUNT )
    ble.version = CharArray(9)
}

/*
As the parameters are unsigned integers, Kotlin requires that
the unsigned integer values are appended with a small u to distinguish between
Int and UInt types
*/
fun decode_sensor_force(value: Int): Int
{
    if ( (value and 0x180) == 0x000 ) {
        return (value and 0x07F)
    }
    if ( (value and 0x180) == 0x080 ) {
        return 128 + (value and 0x07F) * 2
    }
    if ( (value and 0x180) == 0x100 ) {
        return 384 + (value and 0x07F) * 3
    }

    return 768 + (value and 0x07F) * 5
}


fun decode_total_force(value: Int): Int
{
    if ( (value and 0x180) == 0x000 ) {
        return (value and 0x07F) * 2
    }
    if ( (value and 0x180) == 0x080 ) {
        return 256 + (value and 0x07F) * 3
    }
    if ( (value and 0x180) == 0x100 ) {
        return 640 + (value and 0x07F) * 5
    }

    return 1280 + (value and 0x07F) * 10
}



fun decode_adv_data(ble: ble, adv_scan: IntArray, scan_rsp: IntArray): Int
{
    var b: bit_buf = bit_buf(0)
    b.buf = ArrayList(adv_scan.toMutableList())

    val a: adv_force = ble.adv_force

    var len: Int = 0
    var type: Int = 0
    var tmp: Int = 0

    var bdaddr: bdaddr_t = bdaddr_t()

    /* Part 0 */
    bdaddr = ble.bdaddr
    reset_ble(ble)   // reset all the values to 0
    ble.bdaddr = bdaddr
    ble.device_label_id = label_id(bdaddr.s)

    /* The first AD element (flags set by SoftDevice) */
    get_bits(b, 8)  /* AD element length */
    get_bits(b, 8)  /* AD type */
    get_bits(b, 8)  /* AD data */

    /* The second AD element length depends... */
    len = get_bits(b, 8)  /* AD element length */

    if ( len == 3 ) {
        /* Advertising data (connection is possible) */

        /* The second AD type (Complete List of 16-bit Service Class
         * UUIDs = 0x03) */
        type = get_bits(b, 8)

        if ( type != 0x03 ) {
            return -1
        }

        /* We have only one service 0x2309, NOTE: multi byte values in
         * BLE packets are in little-endian order */
        ble.uuid = get_bits(b, 8) or (get_bits(b, 8) shl 8)

        if (ble.uuid != 0x2309) {
            return -1
        }

        /* The third AD type (Manufacturer Specific data 0xFF) */
        len = get_bits(b, 8)   /* AD element length */
        type = get_bits(b, 8)   /* AD element type */
        if (type != 0xFF) {
            return -1
        }

        /* Note little-endian order */
        ble.comp_icode0 = get_bits(b, 8) or (get_bits(b, 8) shl 8)

        if ((b.buf[13] == 0xCB) and (b.buf[14] == 0x1F)) {
            /* Diagnostic data */
            ble.version[0] = '0'
            ble.version[1] = '0'
            ble.version[2] = '0'
            ble.version[3] = '0'
            ble.device_id  = get_bits(b, 16)
            ble.diag_id    = get_bits(b, 16)

            for (i in 0..<SENSOR_COUNT) {
                ble.zero_level[i] = get_bits(b, 10).toLong()
            }

            ble.boot_cnt = get_bits(b, 12)
            ble.error_cnt = get_bits(b, 12)

            ble.calib_prm[0] = get_bits(b, 16).toLong()
            ble.calib_prm[1] = get_bits(b, 16).toLong()
        } else {
            /* Step data */
            ble.lts_cnt    = get_bits(b, 14)
            a.step_type    = get_bits(b, 2)

            ble.device_id  = get_bits(b, 16)
            tmp            = get_bits(b, 4)
            ble.size       = tmp + 34
            ble.handedness = get_bits(b, 1)
            ble.err_code   = get_bits(b, 4)

            a.f1           = decode_total_force(get_bits(b, 9))
            a.f2           = decode_total_force(get_bits(b, 9))
            a.f3           = decode_total_force(get_bits(b, 9))
            a.f1_time      = get_bits(b, 9) * 10
            a.f2_time      = get_bits(b, 9) * 10
            a.f3_time      = get_bits(b, 9) * 10

            for (i in 0..<SENSOR_COUNT) {
                tmp = get_bits(b, 9)
                if ((ble.device_id == 52002) || (ble.device_id == 52003))
                    a.sensor_data[i].force = tmp
                else {
                    a.sensor_data[i].force = decode_sensor_force(tmp)
                }
            }
        }
    } else {
        /* Advertising data (connection is NOT possible) */

        /* The second AD type (Manufacturer Specific data 0xFF) */
        type = get_bits(b, 8)

        if ( type != 0xFF ) {
            return -1
        }

        ble.comp_icode0 = ( get_bits(b, 8) or (get_bits(b, 8) shl 8) )

        if ((b.buf[13] == 0xCB) and (b.buf[14] == 0x1F)) {
            /* Diagnostic data */
            ble.version[0] = get_bits(b, 8).toChar()
            ble.version[1] = get_bits(b, 8).toChar()
            ble.version[2] = get_bits(b, 8).toChar()
            ble.version[3] = get_bits(b, 8).toChar()
            ble.device_id  = get_bits(b, 16)
            ble.diag_id    = get_bits(b, 16)

            for (i in 0..<SENSOR_COUNT) {
                ble.zero_level[i] = get_bits(b, 10).toLong()
            }

            ble.boot_cnt = get_bits(b, 12)
            ble.error_cnt = get_bits(b, 12)

            ble.calib_prm[0] = get_bits(b, 16).toLong()
            ble.calib_prm[1] = get_bits(b, 16).toLong()
        } else {
            /* Step data */
            tmp             = get_bits(b, 12)

            ble.prod_year  = tmp/373 + 2018
            tmp -= 373*(ble.prod_year - 2018)
            ble.prod_month = tmp / 31 + 1
            tmp -= 31*(ble.prod_month - 1)
            ble.prod_day   = tmp + 1

            tmp             = get_bits(b, 10)
            ble.battery    = tmp*4
            ble.lts_cnt    = get_bits(b, 24)
            a.step_type    = get_bits(b, 2)

            ble.device_id  = get_bits(b, 16)
            tmp             = get_bits(b, 4)
            ble.size       = tmp + 34
            ble.handedness = get_bits(b, 1)
            ble.err_code   = get_bits(b, 4)

            a.f1           = decode_total_force(get_bits(b, 9))
            a.f2           = decode_total_force(get_bits(b, 9))
            a.f3           = decode_total_force(get_bits(b, 9))
            a.f1_time      = get_bits(b, 9) * 10
            a.f2_time      = get_bits(b, 9) * 10
            a.f3_time      = get_bits(b, 9) * 10

            for (i in 0..<SENSOR_COUNT) {
                tmp = get_bits(b, 9)
                if ((ble.device_id == 52002) || (ble.device_id == 52003)) {
                    a.sensor_data[i].force = tmp
                }
                else {
                    a.sensor_data[i].force = decode_sensor_force(tmp)
                }
            }
        }
    }

    /* Part 1 */
    b = bit_buf(0)
    b.buf = ArrayList(scan_rsp.toMutableList())

    /* The first AD element (flags set by SoftDevice) */
    get_bits(b, 8)    /* AD element length */
    get_bits(b, 8)    /* AD type */
    get_bits(b, 8)    /* AD data */

    /* The second AD element length depens... */
    len = get_bits(b, 8)        /* AD element length */

    if (len == 3) {
        /* Advertising data (connection is possible) */

        /* The second AD type (Complete List of 16-bit Service Class
         * UUIDs = 0x03 */
        type = get_bits(b, 8)

        if (type != 0x03)
            return -1

        /* We have only one service 0x2309, NOTE: multi byte values in
         * BLE packets are in little-endian order */
        ble.uuid = get_bits(b, 8) or (get_bits(b, 8) shl 8)

        if (ble.uuid != 0x2309)
            return -1

        /* The third AD type (Manufacturer Specific data 0xFF) */
        len = get_bits(b, 8)        /* AD element length */
        type = get_bits(b, 8)        /* AD element type */
        if (type != 0xFF)
            return -1

        ble.comp_icode1 = get_bits(b, 8) or (get_bits(b, 8) shl 8)

        if (ble.diag_id == 51999) {
            /* Diagnostic data */
            ble.version[4] = '0'
            ble.version[5] = '0'
            ble.version[6] = '0'
            ble.version[7] = '0'

            for (i in 2..<CALIB_COUNT) {
                ble.calib_prm[i] = get_bits(b, 16).toLong()
            }
        } else {
            /* Step data */
            for (i in 0..<SENSOR_COUNT) {
                tmp = get_bits(b, 6)
                if ((ble.device_id == 52002) || (ble.device_id == 52003))
                    a.sensor_data[i].time_start = tmp
                else
                    a.sensor_data[i].time_start = tmp * 10
            }

            for (i in 0..<SENSOR_COUNT) {
                a.sensor_data[i].time_max = get_bits(b, 7) * 10
            }

            for (i in 0..<SENSOR_COUNT) {
                a.sensor_data[i].time_end = get_bits(b, 9) * 10
            }
        }
    } else {
        /* Advertising data (connection is NOT possible) */

        /* The second AD type (Manufacturer Specific data 0xFF) */
        type = get_bits(b, 8)
        if (type != 0xFF) {
            return -1
        }

        ble.comp_icode1 = get_bits(b, 8) or (get_bits(b, 8) shl 8)

        if (ble.diag_id == 51999) {
            /* Diagnostic data */
            ble.version[4] = get_bits(b, 8).toChar()
            ble.version[5] = get_bits(b, 8).toChar()
            ble.version[6] = get_bits(b, 8).toChar()
            ble.version[7] = get_bits(b, 8).toChar()

            for (i in 2..<CALIB_COUNT) {
                ble.calib_prm[i] = get_bits(b, 16).toLong()
            }
        } else {
            /* Step data */
            for (i in 0..<SENSOR_COUNT) {
                tmp = get_bits(b, 9)
                if ((ble.device_id == 52002) || (ble.device_id == 52003)) {
                    a.sensor_data[i].time_start = tmp
                }
                else {
                    a.sensor_data[i].time_start = tmp * 10
                }
            }

            for (i in 0..<SENSOR_COUNT) {
                a.sensor_data[i].time_max = get_bits(b, 9) * 10
            }

            for (i in 0..<SENSOR_COUNT) {
                a.sensor_data[i].time_end = get_bits(b, 9) * 10
            }
        }
    }

    /* Calibration mode */
    if ( (ble.device_id == 52002) or (ble.device_id == 52003) ) {
        adv_force_to_sum(a, ble.sum)
    }

    return 0
}


fun adv_force_to_sum(a: adv_force, sum: IntArray): Unit
{
    var tmp: Int = 0
    /* Calibration mode of raw sample sum */
    for (i in 0..<SENSOR_COUNT) {
        tmp = (a.sensor_data[i].force shl 6) or a.sensor_data[i].time_start

        /* Val */
        sum[i] = tmp and 0X3FFF

        /* Sing */
        if ( tmp and 0X4000 != 0 ) {
            sum[i] = -sum[i]
        }
    }
}


fun sum_to_adv_force(sum: IntArray, a: adv_force)
{
    var tmp: Int = 0

    /* Calibration mode of raw sample sum */
    for (i in 0..<SENSOR_COUNT) {
        tmp = sum[i]

        if (tmp < 0) {
            a.sensor_data[i].force = (-tmp or 0X4000) shr 6
            a.sensor_data[i].time_start = -tmp and 0x3F
        } else {
            a.sensor_data[i].force = tmp shr 6
            a.sensor_data[i].time_start = tmp and 0x3F
        }
    }
}