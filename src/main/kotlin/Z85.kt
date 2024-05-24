//  --------------------------------------------------------------------------
//  Reference implementation for rfc.zeromq.org/spec:32/Z85
//
//  This implementation provides a Z85 codec as an easy-to-reuse Kotlin
//  object.
//  --------------------------------------------------------------------------
//  Copyright (c) 2024 Ruben Viscomi
//
//  Permission is hereby granted, free of charge, to any person obtaining a
//  copy of this software and associated documentation files (the "Software"),
//  to deal in the Software without restriction, including without limitation
//  the rights to use, copy, modify, merge, publish, distribute, sublicense,
//  and/or sell copies of the Software, and to permit persons to whom the
//  Software is furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
//  DEALINGS IN THE SOFTWARE.
//  --------------------------------------------------------------------------

object Z85 {

    private val Z85_ENCODER = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z', '.', '-', ':', '+', '=', '^', '!', '/',
        '*', '?', '&', '<', '>', '(', ')', '[', ']', '{',
        '}', '@', '%', '$', '#'
    )

    private val Z85_DECODER = arrayOf<UByte>(
        0x00u, 0x44u, 0x00u, 0x54u, 0x53u, 0x52u, 0x48u, 0x00u,
        0x4Bu, 0x4Cu, 0x46u, 0x41u, 0x00u, 0x3Fu, 0x3Eu, 0x45u,
        0x00u, 0x01u, 0x02u, 0x03u, 0x04u, 0x05u, 0x06u, 0x07u,
        0x08u, 0x09u, 0x40u, 0x00u, 0x49u, 0x42u, 0x4Au, 0x47u,
        0x51u, 0x24u, 0x25u, 0x26u, 0x27u, 0x28u, 0x29u, 0x2Au,
        0x2Bu, 0x2Cu, 0x2Du, 0x2Eu, 0x2Fu, 0x30u, 0x31u, 0x32u,
        0x33u, 0x34u, 0x35u, 0x36u, 0x37u, 0x38u, 0x39u, 0x3Au,
        0x3Bu, 0x3Cu, 0x3Du, 0x4Du, 0x00u, 0x4Eu, 0x43u, 0x00u,
        0x00u, 0x0Au, 0x0Bu, 0x0Cu, 0x0Du, 0x0Eu, 0x0Fu, 0x10u,
        0x11u, 0x12u, 0x13u, 0x14u, 0x15u, 0x16u, 0x17u, 0x18u,
        0x19u, 0x1Au, 0x1Bu, 0x1Cu, 0x1Du, 0x1Eu, 0x1Fu, 0x20u,
        0x21u, 0x22u, 0x23u, 0x4Fu, 0x00u, 0x50u, 0x00u, 0x00u
    )

    /**
     * Encode bytes to Z85 (base85) string.
     * @param data the bytes to encode.
     * @return the encoded data in base85 string (Z85) variant.
     */
    fun encode(data: ByteArray): String {
        if (data.size <= 0)
            return ""

        val sizeRemainder = data.size % 4
        val padSize = if (sizeRemainder != 0) 4 - sizeRemainder else 0

        val encodedSize = ((data.size + padSize) * 5 / 4) - padSize
        val encoded = CharArray(encodedSize)

        var charCount = 0
        var byteCount = 0

        while (byteCount < data.size - 4) {
            // Accumulate value in base 256 (binary)
            val value = (getByteAsUintDefaulted(data, byteCount) shl 24) +
                    (getByteAsUintDefaulted(data, byteCount + 1) shl 16) +
                    (getByteAsUintDefaulted(data, byteCount + 2) shl 8) +
                    getByteAsUintDefaulted(data, byteCount + 3)
            byteCount += 4

            // Output value in base85
            encoded[charCount] = Z85_ENCODER[(value / 52_200_625u % 85u).toInt()]
            encoded[charCount + 1] = Z85_ENCODER[(value / 614_125u % 85u).toInt()]
            encoded[charCount + 2] = Z85_ENCODER[(value / 7_225u % 85u).toInt()]
            encoded[charCount + 3] = Z85_ENCODER[(value / 85u % 85u).toInt()]
            encoded[charCount + 4] = Z85_ENCODER[(value % 85u).toInt()]
            charCount += 5
        }

        val value = (getByteAsUintDefaulted(data, byteCount) shl 24) +
                (getByteAsUintDefaulted(data, byteCount + 1) shl 16) +
                (getByteAsUintDefaulted(data, byteCount + 2) shl 8) +
                getByteAsUintDefaulted(data, byteCount + 3)

        encoded[charCount] = Z85_ENCODER[(value / 52_200_625u % 85u).toInt()]
        encoded[charCount + 1] = Z85_ENCODER[(value / 614_125u % 85u).toInt()]
        if (charCount + 2 < encodedSize) {
            encoded[charCount + 2] = Z85_ENCODER[(value / 7_225u % 85u).toInt()]
            if (charCount + 3 < encodedSize) {
                encoded[charCount + 3] = Z85_ENCODER[(value / 85u % 85u).toInt()]
                if (charCount + 4 < encodedSize)
                    encoded[charCount + 4] = Z85_ENCODER[(value % 85u).toInt()]
            }
        }

        return encoded.concatToString()
    }

    /**
     * Decode Z85 (base85) string to bytes.
     * @param data the string to decode.
     * @return the decoded bytes.
     */
    fun decode(data: String): ByteArray {
        if (data.length <= 0)
            return ByteArray(0)

        val lengthRemainder = data.length % 5
        val requiresPadding = lengthRemainder != 0

        val padSize = if (requiresPadding) 5 - lengthRemainder else 0

        val decoded = ByteArray(((data.length + padSize) * 4 / 5) - padSize)

        var charCount = 0
        var byteCount = 0

        while (charCount < data.length - 5) {
            // Accumulate value in base85
            val value = Z85_DECODER[data[charCount].code - 32] * 52_200_625u +
                    Z85_DECODER[data[charCount + 1].code - 32] * 614_125u +
                    getCharAsUintOrDefault(Z85_DECODER, data, charCount + 2) * 7_225u +
                    getCharAsUintOrDefault(Z85_DECODER, data, charCount + 3) * 85u +
                    getCharAsUintOrDefault(Z85_DECODER, data, charCount + 4)
            charCount += 5

            decoded[byteCount] = (value / 16_777_216u % 256u).toByte()
            decoded[byteCount + 1] = (value / 65_536u % 256u).toByte()
            decoded[byteCount + 2] = (value / 256u % 256u).toByte()
            decoded[byteCount + 3] = (value  % 256u).toByte()
            byteCount += 4
        }

        val value = Z85_DECODER[data[charCount].code - 32] * 52_200_625u +
                Z85_DECODER[data[charCount + 1].code - 32] * 614_125u +
                getCharAsUintOrDefault(Z85_DECODER, data, charCount + 2) * 7_225u +
                getCharAsUintOrDefault(Z85_DECODER, data, charCount + 3) * 85u +
                getCharAsUintOrDefault(Z85_DECODER, data, charCount + 4)

        decoded[byteCount] = (value / 16_777_216u % 256u).toByte()
        if (byteCount + 1 < decoded.size) {
            decoded[byteCount + 1] = (value / 65_536u % 256u).toByte()
            if (byteCount + 2 < decoded.size) {
                decoded[byteCount + 2] = (value / 256u % 256u).toByte()
                if (byteCount + 3 < decoded.size) {
                    decoded[byteCount + 3] = (value % 256u).toByte()
                }
            }
        }

        return decoded
    }

}

@Suppress("NOTHING_TO_INLINE")
private inline fun getByteAsUintDefaulted(data: ByteArray, i: Int) =
    if (i < data.size) data[i].toUByte().toUInt() else 0u

@Suppress("NOTHING_TO_INLINE")
private inline fun getCharAsUintOrDefault(data: Array<UByte>, encoded: String, i: Int) =
    if (i < encoded.length) data[encoded[i].code - 32] else 84u
