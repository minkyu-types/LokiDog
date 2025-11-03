package dev.loki.di

import dev.loki.alarm.usecase.DeleteAlarmUseCase
import dev.loki.alarm.usecase.GetAlarmByIdUseCase
import dev.loki.alarm.usecase.GetAlarmsByGroupIdUseCase
import dev.loki.alarm.usecase.RescheduleAlarmUseCase
import dev.loki.alarm.usecase.RescheduleAllAlarmsOnBootUseCase
import dev.loki.alarm.usecase.UpsertAlarmUseCase
import dev.loki.alarmgroup.usecase.InsertAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteSelectedAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupByIdUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupWithAlarmsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetTempAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import dev.loki.timerhistory.usecase.CreateTimerHistoryUseCase
import dev.loki.timerhistory.usecase.DeleteTimerHistoryUseCase
import dev.loki.timerhistory.usecase.GetTimerHistoriesUseCase
import org.koin.dsl.module

val alarmUseCaseModule = module {
    single { GetAlarmByIdUseCase(get())}
    single { GetAlarmsByGroupIdUseCase(get()) }
    single { RescheduleAlarmUseCase(get()) }
    single { UpsertAlarmUseCase(get(), get()) }
    single { DeleteAlarmUseCase(get(), get()) }

    single { GetAlarmGroupByIdUseCase(get()) }
    single { InsertAlarmGroupUseCase(get()) }
    single { DeleteAlarmGroupUseCase(get()) }
    single { DeleteSelectedAlarmGroupsUseCase(get()) }
    single { RescheduleAllAlarmsOnBootUseCase(get(), get()) }
    single { GetAlarmGroupsUseCase(get()) }
    single { GetTempAlarmGroupsUseCase(get()) }
    single { GetAlarmGroupWithAlarmsUseCase(get()) }
    single { UpdateAlarmGroupUseCase(get(), get()) }

    single { GetTimerHistoriesUseCase(get()) }
    single { CreateTimerHistoryUseCase(get()) }
    single { DeleteTimerHistoryUseCase(get()) }
    single { GetTimerHistoriesUseCase(get()) }
}