# ktZ85
ktZ85 provides an implementation of the Z85 codec following the [zeromq rfc](rfc.zeromq.org/spec:32/Z85).

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
