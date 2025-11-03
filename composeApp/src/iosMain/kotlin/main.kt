import androidx.compose.ui.window.ComposeUIViewController
import dev.loki.dog.App
import dev.loki.dog.expect.AlarmReceiver
import dev.loki.dog.expect.PlatformAlarmScheduler
import platform.UIKit.UIViewController
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

fun MainViewController(): UIViewController {
    println("🚀 iOS: MainViewController 초기화 시작")

    // 1. 알림 델리게이트 등록 (가장 먼저! 이게 없으면 foreground 알림을 받을 수 없음)
    AlarmReceiver().register()
    println("✅ iOS: AlarmReceiver 델리게이트 등록 완료")

    // 2. iOS 알람 카테고리 초기화
    PlatformAlarmScheduler.setupNotificationCategories()

    // 3. 앱 시작 시 바로 알림 권한 요청
    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.requestAuthorizationWithOptions(
        options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
    ) { granted, error ->
        if (granted) {
            println("✅ iOS: 알림 권한 승인됨!")
        } else {
            println("❌ iOS: 알림 권한 거부됨! error: ${error?.localizedDescription}")
            println("⚠️ iOS 설정 > LokiDog > 알림 에서 권한을 켜주세요!")
        }
    }

    return ComposeUIViewController { App() }
}
