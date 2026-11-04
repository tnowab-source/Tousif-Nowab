package com.example.util

/**
 * Standards-compliant ISO/IEC 18004 QR Code Generator (Byte Mode, Error Correction Level L).
 * Supports Version 2 (25x25, up to 32 bytes) and Version 3 (29x29, up to 53 bytes)
 * so that both WhatsApp ("https://wa.me/918100375680") and Instagram
 * ("https://www.instagram.com/food.09674/") URLs produce scannable QR codes offline.
 */
object QrCodeGenerator {

    fun encode(content: String): Array<BooleanArray> {
        val bytes = content.toByteArray(Charsets.UTF_8)
        // Choose Version 2 (25x25, 34 data codewords, 10 EC codewords) or Version 3 (29x29, 55 data codewords, 15 EC codewords)
        val version = if (bytes.size <= 32) 2 else 3
        val size = 17 + 4 * version
        val totalDataCodewords = if (version == 2) 34 else 55
        val ecCodewordsCount = if (version == 2) 10 else 15
        val alignmentCenter = if (version == 2) 18 else 22

        val dataBits = ArrayList<Int>()
        fun appendBits(value: Int, length: Int) {
            for (i in length - 1 downTo 0) {
                dataBits.add((value shr i) and 1)
            }
        }

        // Byte mode indicator: 0100
        appendBits(0b0100, 4)
        // Character count (8 bits for versions 1-9 byte mode)
        appendBits(bytes.size, 8)
        for (b in bytes) {
            appendBits(b.toInt() and 0xFF, 8)
        }
        // Terminator up to 4 bits
        val maxDataBits = totalDataCodewords * 8
        val terminatorLen = minOf(4, maxDataBits - dataBits.size)
        appendBits(0, terminatorLen)

        // Pad to byte boundary
        while (dataBits.size % 8 != 0) {
            dataBits.add(0)
        }

        // Pad with alternating 0xEC and 0x11
        val padBytes = intArrayOf(0xEC, 0x11)
        var padIdx = 0
        while (dataBits.size < maxDataBits) {
            appendBits(padBytes[padIdx % 2], 8)
            padIdx++
        }

        // Convert bits to data codewords
        val dataCodewords = IntArray(totalDataCodewords)
        for (i in 0 until totalDataCodewords) {
            var b = 0
            for (bit in 0 until 8) {
                b = (b shl 1) or dataBits[i * 8 + bit]
            }
            dataCodewords[i] = b
        }

        // Compute Reed-Solomon Error Correction codewords in GF(256)
        val ecCodewords = computeReedSolomon(dataCodewords, ecCodewordsCount)

        // Combine all bits (data + EC + 7 remainder bits for V2/V3)
        val allBits = ArrayList<Int>((totalDataCodewords + ecCodewordsCount) * 8 + 7)
        for (cw in dataCodewords) {
            for (bit in 7 downTo 0) allBits.add((cw shr bit) and 1)
        }
        for (cw in ecCodewords) {
            for (bit in 7 downTo 0) allBits.add((cw shr bit) and 1)
        }
        repeat(7) { allBits.add(0) }

        val modules = Array(size) { BooleanArray(size) }
        val isFunction = Array(size) { BooleanArray(size) }

        fun setFunctionModule(r: Int, c: Int, dark: Boolean) {
            if (r in 0 until size && c in 0 until size) {
                modules[r][c] = dark
                isFunction[r][c] = true
            }
        }

        // Finder patterns + separators
        fun placeFinderPattern(top: Int, left: Int) {
            for (dr in -1..7) {
                for (dc in -1..7) {
                    val r = top + dr
                    val c = left + dc
                    if (r in 0 until size && c in 0 until size) {
                        val inOuter = dr in 0..6 && dc in 0..6 && (dr == 0 || dr == 6 || dc == 0 || dc == 6)
                        val inInner = dr in 2..4 && dc in 2..4
                        setFunctionModule(r, c, inOuter || inInner)
                    }
                }
            }
        }

        placeFinderPattern(0, 0)
        placeFinderPattern(0, size - 7)
        placeFinderPattern(size - 7, 0)

        // Timing patterns
        for (i in 8 until size - 8) {
            setFunctionModule(6, i, i % 2 == 0)
            setFunctionModule(i, 6, i % 2 == 0)
        }

        // Alignment pattern for V2 / V3 at (alignmentCenter, alignmentCenter)
        for (dr in -2..2) {
            for (dc in -2..2) {
                val dark = maxOf(kotlin.math.abs(dr), kotlin.math.abs(dc)) != 1
                setFunctionModule(alignmentCenter + dr, alignmentCenter + dc, dark)
            }
        }

        // Dark module
        setFunctionModule(4 * version + 9, 8, true)

        // Reserve format information areas
        for (i in 0..8) {
            if (!isFunction[8][i]) setFunctionModule(8, i, false)
            if (!isFunction[i][8]) setFunctionModule(i, 8, false)
        }
        for (i in 0..7) {
            if (!isFunction[8][size - 1 - i]) setFunctionModule(8, size - 1 - i, false)
            if (!isFunction[size - 1 - i][8]) setFunctionModule(size - 1 - i, 8, false)
        }

        // Place data bits in upward/downward 2-column zig-zag
        var bitIdx = 0
        var col = size - 1
        var upward = true
        while (col > 0) {
            if (col == 6) col-- // Skip vertical timing pattern column
            val rows = if (upward) (size - 1 downTo 0) else (0 until size)
            for (row in rows) {
                for (cOffset in 0..1) {
                    val c = col - cOffset
                    if (!isFunction[row][c]) {
                        val bit = if (bitIdx < allBits.size) allBits[bitIdx++] == 1 else false
                        // Apply Mask Pattern 0: (row + c) % 2 == 0
                        val mask = (row + c) % 2 == 0
                        modules[row][c] = bit xor mask
                    }
                }
            }
            upward = !upward
            col -= 2
        }

        // Write Format Information for Level L (01) and Mask 0 (000) -> precomputed BCH 15-bit value: 0x77C4
        val formatBits = 0x77C4
        fun getFormatBit(i: Int): Boolean = ((formatBits shr i) and 1) == 1

        // Around top-left finder
        val tlCoords = arrayOf(
            8 to 0, 8 to 1, 8 to 2, 8 to 3, 8 to 4, 8 to 5, 8 to 7, 8 to 8,
            7 to 8, 5 to 8, 4 to 8, 3 to 8, 2 to 8, 1 to 8, 0 to 8
        )
        for (i in 0..14) {
            val (r, c) = tlCoords[i]
            modules[r][c] = getFormatBit(14 - i)
        }

        // Split between bottom-left and top-right
        for (i in 0..6) {
            modules[size - 1 - i][8] = getFormatBit(14 - i)
        }
        for (i in 7..14) {
            modules[8][size - 15 + i] = getFormatBit(14 - i)
        }

        return modules
    }

    private fun computeReedSolomon(data: IntArray, ecCount: Int): IntArray {
        val exp = IntArray(512)
        val log = IntArray(256)
        var x = 1
        for (i in 0 until 255) {
            exp[i] = x
            log[x] = i
            x = x shl 1
            if (x and 0x100 != 0) x = x xor 0x11D
        }
        for (i in 255 until 512) {
            exp[i] = exp[i - 255]
        }

        fun gfMul(a: Int, b: Int): Int {
            if (a == 0 || b == 0) return 0
            return exp[log[a] + log[b]]
        }

        // Generator polynomial
        var gen = intArrayOf(1)
        for (i in 0 until ecCount) {
            val next = IntArray(gen.size + 1)
            val root = exp[i]
            for (j in gen.indices) {
                next[j] = next[j] xor gen[j]
                next[j + 1] = next[j + 1] xor gfMul(gen[j], root)
            }
            gen = next
        }

        val remainder = IntArray(ecCount)
        for (byteVal in data) {
            val factor = byteVal xor remainder[0]
            System.arraycopy(remainder, 1, remainder, 0, ecCount - 1)
            remainder[ecCount - 1] = 0
            for (i in 0 until ecCount) {
                remainder[i] = remainder[i] xor gfMul(gen[i + 1], factor)
            }
        }
        return remainder
    }
}
