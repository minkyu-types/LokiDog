package dev.loki.dog.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.expect.Platform
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.ScrimLight
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.apple_logo_white_xxxhdpi_192x192
import lokidog.composeapp.generated.resources.google_g_logo_xxxhdpi_192x192
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    viewModel: LoginViewModel,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAuthState()
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { sideEffect ->
            when (sideEffect) {
                is LoginSideEffect.NavigateToMain -> onNavigateToMain()
                is LoginSideEffect.ShowError -> {
                    // Handle error display (could use Snackbar)
                }

                is LoginSideEffect.ShowGoogleSignIn -> {
                    // This is handled by the button click
                }
            }
        }
    }

    LoginScreenContent(
        state = state,
        onGoogleSignInClick = { viewModel.signInWithGoogle() },
        onAppleSignInClick = { viewModel.signInWithApple() }
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    onGoogleSignInClick: () -> Unit,
    onAppleSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Logo or Icon
            Text(
                text = "Malarm",
                style = MaterialTheme.typography.displayLarge,
                color = OnTertiaryLight
            )

            Text(
                text = "알람을 그룹화하고, 관리하세요.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = OnTertiaryLight
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onGoogleSignInClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnTertiaryLight
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(Res.drawable.google_g_logo_xxxhdpi_192x192),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterStart),
                    )
                    Text(
                        text = "Sign in with Google",
                        fontSize = 18.sp,
                        color = ScrimLight,
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                }
            }

            // Apple Sign-In 버튼은 iOS에서만 표시
            if (Platform.isIOS) {
                Button(
                    onClick = onAppleSignInClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ScrimLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.apple_logo_white_xxxhdpi_192x192),
                            contentDescription = null,
                            modifier = Modifier
                                .size(34.dp)
                                .align(Alignment.CenterStart),
                        )
                        Text(
                            text = "Sign in with Apple",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.Center),
                        )
                    }
                }
            }

            // Error message display
            state.loadState.let { loadState ->
                if (loadState is dev.loki.dog.feature.base.LoadState.Error) {
                    Text(
                        text = loadState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenContent(
            state = LoginState(),
            onGoogleSignInClick = {},
            onAppleSignInClick = {}
        )
    }
}