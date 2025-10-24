import androidx.compose.ui.window.ComposeUIViewController
import dev.loki.dog.App
import dev.loki.dog.doInitKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    doInitKoin()
    return ComposeUIViewController { App() }
}
