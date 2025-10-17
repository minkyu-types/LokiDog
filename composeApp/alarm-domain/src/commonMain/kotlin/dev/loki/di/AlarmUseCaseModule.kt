package dev.loki.di

import dev.loki.alarm.usecase.DeleteAlarmUseCase
import dev.loki.alarm.usecase.UpsertAlarmUseCase
import dev.loki.alarmgroup.usecase.UpsertAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteSelectedAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupWithAlarmsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetTempAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import org.koin.dsl.module

val alarmUseCaseModule = module {
    single { UpsertAlarmUseCase(get()) }
    single { DeleteAlarmUseCase(get()) }

    single { UpsertAlarmGroupUseCase(get()) }
    single { DeleteAlarmGroupUseCase(get()) }
    single { DeleteSelectedAlarmGroupsUseCase(get()) }
    single { GetAlarmGroupsUseCase(get()) }
    single { GetTempAlarmGroupsUseCase(get()) }
    single { GetAlarmGroupWithAlarmsUseCase(get()) }
    single { UpdateAlarmGroupUseCase(get()) }
}