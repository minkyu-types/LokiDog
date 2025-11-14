package dev.loki.dog.expect

actual object Platform {
    actual val type: PlatformType = PlatformType.IOS
    actual val isAndroid: Boolean = false
    actual val isIOS: Boolean = true
}