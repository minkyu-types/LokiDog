package dev.loki.dog.expect

import LokiDog.composeApp.BuildConfig
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

actual class LoginManager(
    private val context: Context
): BaseLoginManager {

    companion object {
        const val TAG = "GoogleLogin"
    }

    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private fun createGoogleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)  // 모든 계정 표시
            .setAutoSelectEnabled(false)  // 자동 선택 비활성화 -> 사용자가 직접 선택
            .setNonce(generateNonce())  // 보안을 위한 nonce 추가
            .build()
    }

    private fun generateNonce(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun createCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(createGoogleIdOption())
            .build()
    }

    init {
        Logger.d("LoginManager initialized", tag = TAG)
        Logger.d("Web Client ID: ${BuildConfig.GOOGLE_WEB_CLIENT_ID}", tag = TAG)
        Logger.d("Package Name: ${context.packageName}", tag = TAG)
    }

    actual override fun requestGoogleLogin(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        coroutineScope.launch {
            try {
                Logger.d("Starting Google Sign-In request...", tag = TAG)

                val request = createCredentialRequest()
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                handleSignInResult(result, onSuccess, onFailure)
            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                Logger.e("No Google account found on device", e, TAG)
                // Try alternative sign-in method
                handleNoCredentialException(context, onSuccess, onFailure)
            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                Logger.e("User cancelled credential selection", e, TAG)
                onFailure("로그인이 취소되었습니다.")
            } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                Logger.e("Credential error: ${e.type}", e, TAG)
                onFailure("인증 오류: ${e.errorMessage?.toString() ?: e.message}")
            } catch (e: Exception) {
                Logger.e("Unexpected error during sign-in", e, TAG)
                onFailure("로그인 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun handleSignInResult(
        result: GetCredentialResponse,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    // Log user info
                    Logger.d("Google Sign-In Success", tag = TAG)
                    Logger.d("ID Token: $idToken", tag = TAG)
                    Logger.d("Email: ${googleIdTokenCredential.id}", tag = TAG)
                    googleIdTokenCredential.displayName?.let {
                        Logger.d("Display Name: $it", tag = TAG)
                    }

                    // Pass ID Token to UseCase
                    onSuccess(idToken)
                } else {
                    onFailure("구글 로그인에 실패하였습니다. 다시 시도해주세요.")
                }
            }
            else -> {
                onFailure("Unknown credential type")
            }
        }
    }

    actual override fun requestAppleLogin(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Android does not support Apple Sign-In
        onFailure("Apple 로그인은 iOS에서만 지원됩니다.")
    }

    private suspend fun handleNoCredentialException(
        activity: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            Logger.d("Trying alternative sign-in method (GetSignInWithGoogleOption)...", tag = TAG)

            val signInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = CredentialManager.create(activity).getCredential(
                context = activity,
                request = request
            )

            handleSignInResult(result, onSuccess, onFailure)
        } catch (e: Exception) {
            Logger.e("Alternative sign-in also failed", e, TAG)
            onFailure("기기에 Google 계정이 없거나 Google Play Services가 업데이트되지 않았습니다.\n\n설정에서 Google 계정을 추가하거나 Google Play Services를 업데이트해주세요.")
        }
    }
}