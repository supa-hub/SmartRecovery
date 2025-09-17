package com.example.testing_2.backend.bluetoothDecoding

/*
This file contains the structs and functions from
buffer.h and buffer.c.
*/

data class bit_buf(var bit_cnt: Int) /* Bits in the buffer */
{
    var buf: ArrayList<Int> = ArrayList() /* Bit buffer, an Int is used for representing Unsigned Byte */
}



fun init_buffer(b: bit_buf, buf: ArrayList<Int>): Unit
{
    b.bit_cnt = 0
    b.buf = buf
    b.buf[0] = 0
}




fun put_bits(b: bit_buf, value: Int, number: Int): Unit
{
    var bits: Int = 0
    var word: Long = 0
    var buf_idx: Int = b.bit_cnt / 8

    bits = number + b.bit_cnt % 8
    word = (b.buf[buf_idx].toLong() shl 24) or ( value.toLong() shl ( 32 - bits ) )

    while (bits > 7) {
        b.buf[buf_idx] = ( (word shr 24) and 0xFF ).toInt()
        bits -= 8
        word = word shl 8
    }

    b.buf[buf_idx] = ( (word shr 24) and 0xFF ).toInt()
    b.bit_cnt += number
}




fun get_bits(b: bit_buf, number: Int): Int
{
    var bits: Int = 0
    var word: UInt = 0u
    var buf_idx: Int = 0
    var tmp: Int = 0

    buf_idx = b.bit_cnt / 8
    bits = b.bit_cnt % 8
    tmp = bits + number

    if ( tmp <= 8 ) {
        word = b.buf[buf_idx].toUInt() shl 24
    }
    else if ( tmp <= 16 ) {
        word = (b.buf[buf_idx].toUInt() shl 24) or (b.buf[buf_idx + 1].toUInt() shl 16)
    }
    else {
        word = ( (b.buf[buf_idx].toUInt() shl 24)
                or (b.buf[buf_idx + 1].toUInt() shl 16)
                or (b.buf[buf_idx + 2].toUInt() shl 8)
                or (b.buf[buf_idx + 3].toUInt()) )
    }

    word = (word shl bits) shr (32 - number)
    b.bit_cnt += number

    return word.toInt() and 0x7FFFFFFF
}