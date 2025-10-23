package dev.loki.dog.expect

import dev.loki.AlarmScheduler
import platform.Foundation.runUntilDate

import dev.loki.alarm.model.Alarm // alarm: id, groupId, time("HH:mm"), memo, isActivated 등 가정
import kotlinx.datetime.DayOfWeek
import platform.Foundation.NSDateComponents
import platform.Foundation.NSRunLoop
import platform.UserNotifications.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class PlatformAlarmScheduler() : AlarmScheduler {

    companion object {
        private const val CATEGORY_ID = "dev.loki.dog.alarm.category"
        private const val THREAD_ID = "dev.loki.dog.alarm.thread"
        private const val ID_PREFIX_ALARM = "alarm"          // "alarm_<alarmId>_<weekday>"
        private const val ID_PREFIX_GROUP = "group"          // "group_<groupId>_<alarmId>_<weekday>"
    }

    // iOS 요일: 1=Sun ... 7=Sat
    private fun DayOfWeek.toAppleWeekday(): Int = when (this) {
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
        DayOfWeek.SUNDAY -> 1
    }

    override suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        if (!alarm.isActivated) return

        val center = UNUserNotificationCenter.currentNotificationCenter()
        // 권한 요청(한 번만 승인되면 이후 no-op)
        requestAuthIfNeeded(center)

        val (hour, minute) = parseHourMinute(alarm.time)

        // 반복 요일이 비어있다면: “다음 최근 시각 1회성” 대신
        // iOS에선 정확한 nextDate 계산이 번거로우므로 실무에선 UNCalendarNotificationTrigger(repeats=false)로
        // next fire를 계산해 넣습니다. 여기선 요일 기반 반복만 다룹니다.
        val days = if (repeatDays.isEmpty()) DayOfWeek.entries.toSet() else repeatDays

        days.forEach { dow ->
            val weekday = dow.toAppleWeekday()
            val requestId = buildId(alarmId = alarm.id, groupId = alarm.groupId, weekday = weekday)

            val content = UNMutableNotificationContent().apply {
                setTitle("Alarm")
                setBody(alarm.memo)
                setCategoryIdentifier(CATEGORY_ID)
                setThreadIdentifier(THREAD_ID)
                setSound(UNNotificationSound.defaultSound())
                setUserInfo(
                    mapOf(
                        "alarmId" to alarm.id,
                        "alarmMemo" to (alarm.memo ?: ""),
                        "weekday" to weekday
                    )
                )
            }

            // 요일/시/분 반복 트리거
            val comps = NSDateComponents().apply {
                this.weekday = weekday.toLong()
                this.hour = hour.toLong()
                this.minute = minute.toLong()
            }
            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = comps,
                repeats = true
            )

            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = requestId,
                content = content,
                trigger = trigger
            )
            center.addNotificationRequest(request, withCompletionHandler = null)
        }
    }

    override suspend fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val targetPrefix = "${ID_PREFIX_GROUP}_${alarm.groupId}_${alarm.id}_"

        // 식별자 prefix로 일괄 제거
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val requestData = requests as? List<UNNotificationRequest>
            val ids = requestData?.map { it.identifier } // OK
                ?.filter { id ->
                    id.startsWith(targetPrefix) || id.startsWith("${ID_PREFIX_ALARM}_${alarm.id}_")
                }
            if (!ids.isNullOrEmpty()) {
                center.removePendingNotificationRequestsWithIdentifiers(ids)
            }
            // 이미 발송 대기도 제거
            center.getDeliveredNotificationsWithCompletionHandler { delivered ->
                val deliveredData = delivered as? List<UNNotification>
                val dIds = deliveredData?.map { it.request.identifier }
                    ?.filter { it.startsWith(targetPrefix) || it.startsWith("${ID_PREFIX_ALARM}_${alarm.id}_") }
                if (!dIds.isNullOrEmpty()) center.removeDeliveredNotificationsWithIdentifiers(dIds)
            }
        }

        NSDateComponents().date?.let { NSRunLoop.currentRunLoop.runUntilDate(it) }
    }

    override suspend fun cancelByGroup(groupId: Long) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val prefix = "${ID_PREFIX_GROUP}_${groupId}_"
        center.getPendingNotificationRequestsWithCompletionHandler { list ->
            val listData = list as? List<UNNotificationRequest>
            val ids = listData?.mapNotNull { it.identifier }?.filter { it.startsWith(prefix) }
            if (!ids.isNullOrEmpty()) center.removePendingNotificationRequestsWithIdentifiers(ids)
            center.getDeliveredNotificationsWithCompletionHandler { delivered ->
                val requestData = delivered as? List<UNNotification>
                val dIds = requestData?.mapNotNull { it.request.identifier }?.filter { it.startsWith(prefix) }
                if (!dIds.isNullOrEmpty()) center.removeDeliveredNotificationsWithIdentifiers(dIds)
            }
        }
    }

    // --- helpers ---

    private fun buildId(alarmId: Long, groupId: Long, weekday: Int): String {
        // 그룹 단위 취소/정리를 위해 groupId까지 prefix에 포함
        return "${ID_PREFIX_GROUP}_${groupId}_${alarmId}_$weekday"
    }

    private fun parseHourMinute(hhmm: String): Pair<Int, Int> {
        // "HH:mm"
        val parts = hhmm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h to m
    }

    private fun requestAuthIfNeeded(center: UNUserNotificationCenter) {
        dispatch_async(dispatch_get_main_queue()) {
            center.getNotificationSettingsWithCompletionHandler { settings ->
                val status = settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined
                if (status == UNAuthorizationStatusNotDetermined) {
                    center.requestAuthorizationWithOptions(
                        options = UNAuthorizationOptionAlert or
                                UNAuthorizationOptionSound or
                                UNAuthorizationOptionBadge
                    ) { _, _ -> /* 무시 */ }
                }
            }
        }
    }
}
