# ktZ85
ktZ85 provides an implementation of the Z85 codec following the [zeromq rfc](https://rfc.zeromq.org/spec/32/).

## Performance
This implementation of the Z85 codec has been measured with [Kotlin's Experimental Base64](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io.encoding/-base64/).

It has been made use of the [`measureNanoTime`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.system/measure-nano-time.html) to track the time (in nano-seconds) taken for the function to complete the execution.\
The benchmark consisted into taking the total time to encode 1.000.000 times the string `"The quick brown 🦊 jumps over 13 lazy 🐶."` for both Z85 and Base64.\
The same has been done for decoding.

The result have shown that:
- encoding: Z85 was 10% slower than Base64.
- decoding: Z85 was 20% faster than Base64.

It is worth noticing, however, that the time has been measured in nano-seconds and for real world applications, such delta for both encoding and decoding is negligible.
For both codecs, in fact, roughly 78MB worth of data have been digested in roughly 150ms.

Please note that even though base 85 allows saving more space than base 64, it might not be the best choice for certain use cases (i.e. url encoding).

## Examples
```kotlin
val toEncode = "Hello, World!"

// ENCODING EXAMPLE
val encoded = Z85.encode(toEncode.toByteArray())
println(encoded) // Outputs: nm=QNz.92jz/PV8aP

// DECODING EXAMPLE
val decoded = Z85.decode(encoded)
println(decoded.decodeToString()) // Outputs: Hello, World!
```
