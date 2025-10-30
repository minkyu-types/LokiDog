package dev.loki.dog.expect

import MainViewController
import platform.Foundation.NSNumber
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UserNotifications.*
import platform.darwin.NSObject

// Android의 BroadcastReceiver 대체: iOS 알림 델리게이트
actual class AlarmReceiver {

    private val center = UNUserNotificationCenter.currentNotificationCenter()
    private var delegateImpl: NotificationDelegate? = null

    fun register() {
        // 앱 시작 시 1회 등록 권장(예: AppDelegate.didFinishLaunching 또는 Swift 진입점에서)
        if (delegateImpl == null) {
            delegateImpl = NotificationDelegate(::openAppForAlarm)
            center.setDelegate(delegateImpl)
        }
    }

    // 알림 탭 시 열 화면(Compose VC 등) 연결
    private fun openAppForAlarm(alarmId: Long, memo: String?) {
        // 최상단 VC 획득
        val root = topMostViewController() ?: return

        // Compose Multiplatform의 엔트리 VC
        val composeVC: UIViewController = MainViewController()
        composeVC.modalPresentationStyle = UIModalPresentationFullScreen

        // 전달이 필요하면 싱글톤/SharedState/NSUserDefaults 등으로 alarmId/memo를 주입
        // Example: AppSharedState.currentAlarm = AlarmPayload(alarmId, memo)

        root.presentViewController(composeVC, animated = true, completion = null)
    }

    @Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
    private fun topMostViewController(): UIViewController? {
        // iOS 13+ scene-based approach
        val application = UIApplication.sharedApplication

        // connectedScenes를 배열로 변환 (NSSet.allObjects는 프로퍼티)
        val scenesSet = application.connectedScenes
        val scenesArray = (scenesSet as? Set<*>)?.toList() ?: emptyList()

        // UIWindowScene 필터링 및 windows 수집
        val allWindows = mutableListOf<UIWindow>()
        for (obj in scenesArray) {
            if (obj is UIWindowScene) {
                val windowsArray = obj.windows as? List<*> ?: continue
                for (window in windowsArray) {
                    if (window is UIWindow) {
                        allWindows.add(window)
                    }
                }
            }
        }

        // keyWindow 찾기
        val keyWindow = allWindows.firstOrNull { it.keyWindow }
            ?: (application.windows as? List<*>)?.filterIsInstance<UIWindow>()?.firstOrNull()

        // rootViewController부터 최상위 ViewController까지 탐색
        var top = keyWindow?.rootViewController ?: return null
        while (true) {
            val presented = top.presentedViewController
            if (presented != null) {
                top = presented
            } else {
                break
            }
        }
        return top
    }
}

// iOS 알림 델리게이트 구현
private class NotificationDelegate(
    private val onOpen: (alarmId: Long, memo: String?) -> Unit
) : NSObject(), UNUserNotificationCenterDelegateProtocol {

    // 앱이 포그라운드일 때도 배너/사운드 표시
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        // 앱이 foreground에 있을 때 추가로 알람 소리 재생
        AlarmService.playAlarmSound()

        withCompletionHandler(UNNotificationPresentationOptionAlert or UNNotificationPresentationOptionSound)
    }

    // 사용자가 알림을 탭한 경우
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        val userInfo = didReceiveNotificationResponse.notification.request.content.userInfo
        val actionIdentifier = didReceiveNotificationResponse.actionIdentifier

        val alarmId = (userInfo["alarmId"] as? NSNumber)?.longLongValue ?: -1L
        val memo = userInfo["alarmMemo"] as? String

        // "정지" 버튼을 눌렀거나 알림을 탭한 경우
        if (actionIdentifier == "dev.loki.dog.alarm.stop" ||
            actionIdentifier == UNNotificationDefaultActionIdentifier) {

            // 알람 소리 정지
            AlarmService.stopAlarmSound()

            // 해당 알람의 알림 제거
            AlarmService.removeDeliveredNotification(alarmId)
        }

        // 알림 탭한 경우에만 앱 열기
        if (actionIdentifier == UNNotificationDefaultActionIdentifier) {
            onOpen(alarmId, memo)
        }

        withCompletionHandler()
    }
}