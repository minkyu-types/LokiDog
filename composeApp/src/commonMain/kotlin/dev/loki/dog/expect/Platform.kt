package dev.loki.dog.expect

enum class PlatformType {
    ANDROID,
    IOS
}

expect object Platform {
    val type: PlatformType
    val isAndroid: Boolean
    val isIOS: Boolean
}