import androidx.compose.ui.window.ComposeUIViewController
import dev.loki.dog.App
import dev.loki.dog.expect.PlatformAlarmScheduler
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // iOS 알람 카테고리 초기화
    PlatformAlarmScheduler.setupNotificationCategories()

    return ComposeUIViewController { App() }
}
