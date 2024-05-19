package interfaces

interface Decoder {
    fun decode(data: String): ByteArray
}