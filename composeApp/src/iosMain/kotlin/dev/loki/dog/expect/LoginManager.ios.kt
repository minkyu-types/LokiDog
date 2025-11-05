package dev.loki.dog.expect

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AuthenticationServices.*
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.darwin.NSObject

actual class LoginManager : BaseLoginManager {

    companion object {
        const val TAG = "AppleLogin"
    }

    actual override fun requestGoogleLogin(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        onFailure("Google 로그인은 Android에서만 지원됩니다.")
    }

    actual override fun requestAppleLogin(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Logger.d("Starting Apple Sign-In request...", tag = TAG)

        val appleIDProvider = ASAuthorizationAppleIDProvider()
        val request = appleIDProvider.createRequest()
        request.requestedScopes = listOf(
            ASAuthorizationScopeFullName,
            ASAuthorizationScopeEmail
        )

        val controller = ASAuthorizationController(listOf(request))

        val delegate = AppleSignInDelegate(onSuccess, onFailure)
        controller.delegate = delegate
        controller.presentationContextProvider = ApplePresentationContextProvider()
        controller.performRequests()
    }

    private class AppleSignInDelegate(
        private val onSuccess: (String) -> Unit,
        private val onFailure: (String) -> Unit
    ) : NSObject(), ASAuthorizationControllerDelegateProtocol {

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithAuthorization: ASAuthorization
        ) {
            when (val credential = didCompleteWithAuthorization.credential) {
                is ASAuthorizationAppleIDCredential -> {
                    val identityToken = credential.identityToken
                    if (identityToken != null) {
                        val tokenString = NSString.create(
                            data = identityToken,
                            encoding = NSUTF8StringEncoding
                        ) as? String

                        if (tokenString != null) {
                            Logger.d("Apple Sign-In Success", tag = TAG)
                            Logger.d("User ID: ${credential.user}", tag = TAG)
                            Logger.d("Email: ${credential.email ?: "N/A"}", tag = TAG)
                            onSuccess(tokenString)
                        } else {
                            Logger.e("Failed to convert token", tag = TAG)
                            onFailure("Token 변환 오류")
                        }
                    } else {
                        Logger.e("Identity token is null", tag = TAG)
                        onFailure("Identity token을 가져올 수 없습니다.")
                    }
                }
                else -> {
                    Logger.e("Unknown credential type", tag = TAG)
                    onFailure("알 수 없는 인증 타입입니다.")
                }
            }
        }

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithError: NSError
        ) {
            Logger.e("Apple Sign-In failed: ${didCompleteWithError.localizedDescription}", null, TAG)

            when (didCompleteWithError.code) {
                ASAuthorizationErrorCanceled -> onFailure("로그인이 취소되었습니다.")
                ASAuthorizationErrorFailed -> onFailure("로그인에 실패했습니다.")
                ASAuthorizationErrorInvalidResponse -> onFailure("잘못된 응답을 받았습니다.")
                ASAuthorizationErrorNotHandled -> onFailure("요청을 처리할 수 없습니다.")
                ASAuthorizationErrorUnknown -> onFailure("알 수 없는 오류가 발생했습니다.")
                else -> onFailure("로그인 오류: ${didCompleteWithError.localizedDescription}")
            }
        }
    }

    private class ApplePresentationContextProvider : NSObject(),
        ASAuthorizationControllerPresentationContextProvidingProtocol {

        override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor? {
            return UIApplication.sharedApplication.keyWindow!!
        }
    }
}