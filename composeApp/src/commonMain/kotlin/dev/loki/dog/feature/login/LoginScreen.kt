package dev.loki.dog.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnBackgroundLight
import dev.loki.dog.theme.OnTertiaryLight
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.android_light_rd_na
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

            // Google Sign In Button
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSigningIn,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnTertiaryLight
                )
            ) {
                if (state.isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.android_light_rd_na),
                            contentDescription = null,
                        )
                        Text(
                            text = "Google로 로그인",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackgroundLight
                        )
                    }
                }
            }

            // Apple Sign In Button
            IconButton(
                onClick = onAppleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSigningIn,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                if (state.isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.android_light_rd_na),
                            contentDescription = null,
                        )
                        Text(
                            text = "Apple로 로그인",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackgroundLight
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