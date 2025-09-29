import androidx.compose.ui.window.ComposeUIViewController
import dev.loki.dog.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
