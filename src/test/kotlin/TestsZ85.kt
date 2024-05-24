import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

private val TESTS = listOf(
    Pair("Hello".toByteArray(), "nm=QNzV"),
    Pair("Hell".toByteArray(), "nm=QN"),
    Pair("Hel".toByteArray(), "nm=P"),
    Pair("He".toByteArray(), "nm."),
    Pair("H".toByteArray(), "nb"),
    Pair("".toByteArray(), ""),
    Pair("Hello, World!".toByteArray(), "nm=QNz.92jz/PV8aP"),
    Pair("The quick brown 🦊 jumps over 13 lazy 🐶.".toByteArray(), "ra]?=ADL#9yAN8bz*c7w[sMOnazM4oAc0duC4CXpf/6}*vs0hw[sL/Je=")
)

class TestsZ85 {
    @Test
    fun testEncoding() {
        assertAll("Testing encoding", encoding_assertions())
    }

    @Test
    fun testDecoding() {
        assertAll("Testing decoding", decoding_assertions())
    }

    private fun encoding_assertions(): Collection<() -> Unit> = TESTS.map { (bytes, result) ->
        {
            val encoded = Z85.encode(bytes)

            if (encoded == result)
                println("PASS [encoding] - expected \"$result\", got \"$encoded\".")
            else
                println("FAIL [encoding] - expected \"$result\", got \"$encoded\".")

            assertEquals(encoded, result, "Encoding failed.")
        }
    }

    private fun decoding_assertions(): Collection<() -> Unit> = TESTS.map { (resultB, string) ->
        {
            val decoded = Z85.decode(string).decodeToString()
            val result = resultB.decodeToString()

            if (decoded == result)
                println("PASS [decoding] - expected \"$result\", got \"$decoded\".")
            else
                println("FAIL [decoding] - expected \"$result\", got \"$decoded\".")

            assertEquals(decoded, result, "Decoding failed.")
        }
    }
}
