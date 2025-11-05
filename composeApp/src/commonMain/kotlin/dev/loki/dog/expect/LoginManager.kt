package dev.loki.dog.expect

expect class LoginManager: BaseLoginManager {
    override fun requestGoogleLogin(onSuccess: (String) -> Unit, onFailure: (String) -> Unit)
    override fun requestAppleLogin(onSuccess: (String) -> Unit, onFailure: (String) -> Unit)
}

interface BaseLoginManager {
    /**
     * Google 로그인 요청
     * @param onSuccess ID Token 전달
     * @param onFailure 에러 메시지 전달
     */
    fun requestGoogleLogin(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    )

    /**
     * Apple 로그인 요청 (iOS 전용)
     * @param onSuccess ID Token 전달
     * @param onFailure 에러 메시지 전달
     */
    fun requestAppleLogin(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    )
}