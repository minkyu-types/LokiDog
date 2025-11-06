package dev.loki.dog.expect

actual object Platform {
    actual val type: PlatformType = PlatformType.ANDROID
    actual val isAndroid: Boolean = true
    actual val isIOS: Boolean = false
}