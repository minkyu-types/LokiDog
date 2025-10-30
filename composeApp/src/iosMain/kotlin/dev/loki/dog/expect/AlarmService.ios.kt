package dev.loki.dog.expect

import platform.UserNotifications.*
import platform.AVFAudio.*
import platform.Foundation.*
import kotlinx.cinterop.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class AlarmService {

    companion object {
        private const val CATEGORY_ID = "dev.loki.dog.alarm.category"
        private const val ACTION_STOP = "dev.loki.dog.alarm.stop"

        private var audioPlayer: AVAudioPlayer? = null

        /**
         * iOS 앱 시작 시 알림 카테고리를 등록합니다.
         * AppDelegate나 초기화 시점에 호출해야 합니다.
         */
        fun setupNotificationCategories() {
            dispatch_async(dispatch_get_main_queue()) {
                val stopAction = UNNotificationAction.actionWithIdentifier(
                    identifier = ACTION_STOP,
                    title = "정지",
                    options = UNNotificationActionOptionForeground
                )

                val category = UNNotificationCategory.categoryWithIdentifier(
                    identifier = CATEGORY_ID,
                    actions = listOf(stopAction),
                    intentIdentifiers = listOf(UNNotificationDefaultActionIdentifier),
                    options = 0u
                )

                val center = UNUserNotificationCenter.currentNotificationCenter()
                center.setNotificationCategories(setOf(category))

                println("✅ iOS: 알림 카테고리 등록 완료")
            }
        }

        /**
         * 알람 소리를 재생합니다 (앱이 foreground에 있을 때)
         * iOS는 백그라운드에서 Critical Alert로 소리가 나므로,
         * 이 메서드는 앱이 foreground일 때만 동작합니다.
         */
        @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
        fun playAlarmSound() {
            dispatch_async(dispatch_get_main_queue()) {
                memScoped {
                    try {
                        // 오디오 세션 설정
                        val audioSession = AVAudioSession.sharedInstance()
                        val errorPtr = alloc<ObjCObjectVar<NSError?>>()

                        // 카테고리 설정 (재생 + 스피커 사용)
                        audioSession.setCategory(
                            AVAudioSessionCategoryPlayback,
                            withOptions = AVAudioSessionCategoryOptionMixWithOthers,
                            error = errorPtr.ptr
                        )

                        if (errorPtr.value != null) {
                            println("❌ iOS: 오디오 세션 카테고리 설정 실패 - ${errorPtr.value?.localizedDescription}")
                            return@dispatch_async
                        }

                        // 오디오 세션 활성화
                        audioSession.setActive(true, error = errorPtr.ptr)
                        if (errorPtr.value != null) {
                            println("❌ iOS: 오디오 세션 활성화 실패 - ${errorPtr.value?.localizedDescription}")
                            return@dispatch_async
                        }

                        // 시스템 사운드 사용 (ID 1005 = Anticipate.caf - 기본 알람 소리)
                        // 또는 다른 시스템 사운드 ID 사용 가능
                        platform.AudioToolbox.AudioServicesPlaySystemSound(1005u)

                        println("✅ iOS: 알람 소리 재생 (시스템 사운드 1005)")

                        // 참고: 시스템 사운드는 한 번만 재생되므로,
                        // 반복 재생이 필요하면 타이머로 반복 호출해야 합니다

                    } catch (e: Exception) {
                        println("❌ iOS: 알람 소리 재생 실패 - ${e.message}")
                    }
                }
            }
        }

        /**
         * 알람 소리를 정지합니다
         */
        @OptIn(ExperimentalForeignApi::class)
        fun stopAlarmSound() {
            dispatch_async(dispatch_get_main_queue()) {
                audioPlayer?.stop()
                audioPlayer = null

                val audioSession = AVAudioSession.sharedInstance()
                audioSession.setActive(false, error = null)

                println("✅ iOS: 알람 소리 정지")
            }
        }

        /**
         * 특정 알람 ID의 알림을 제거합니다
         */
        fun removeDeliveredNotification(alarmId: Long) {
            val center = UNUserNotificationCenter.currentNotificationCenter()

            center.getDeliveredNotificationsWithCompletionHandler { notifications ->
                val notificationsList = notifications as? List<UNNotification>
                val identifiersToRemove = notificationsList
                    ?.filter { notification ->
                        val userInfo = notification.request.content.userInfo
                        val id = (userInfo["alarmId"] as? NSNumber)?.longValue
                        id == alarmId
                    }
                    ?.map { it.request.identifier }
                    ?: emptyList()

                if (identifiersToRemove.isNotEmpty()) {
                    center.removeDeliveredNotificationsWithIdentifiers(identifiersToRemove)
                    println("✅ iOS: 알림 제거 완료 - $identifiersToRemove")
                }
            }
        }
    }
}