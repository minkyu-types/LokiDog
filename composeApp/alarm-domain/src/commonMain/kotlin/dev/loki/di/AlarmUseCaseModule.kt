package dev.loki.di

import dev.loki.alarm.usecase.DeleteAlarmUseCase
import dev.loki.alarm.usecase.GetAlarmByIdUseCase
import dev.loki.alarm.usecase.GetAlarmsByGroupIdUseCase
import dev.loki.alarm.usecase.RescheduleAlarmUseCase
import dev.loki.alarm.usecase.UpsertAlarmUseCase
import dev.loki.alarmgroup.usecase.InsertAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteSelectedAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupById
import dev.loki.alarmgroup.usecase.GetAlarmGroupWithAlarmsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetTempAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import org.koin.dsl.module

val alarmUseCaseModule = module {
    single { GetAlarmByIdUseCase(get())}
    single { GetAlarmsByGroupIdUseCase(get()) }
    single { RescheduleAlarmUseCase(get()) }
    single { UpsertAlarmUseCase(get(), get()) }
    single { DeleteAlarmUseCase(get(), get()) }

    single { GetAlarmGroupById(get()) }
    single { InsertAlarmGroupUseCase(get(), get()) }
    single { DeleteAlarmGroupUseCase(get()) }
    single { DeleteSelectedAlarmGroupsUseCase(get()) }
    single { GetAlarmGroupsUseCase(get()) }
    single { GetTempAlarmGroupsUseCase(get()) }
    single { GetAlarmGroupWithAlarmsUseCase(get()) }
    single { UpdateAlarmGroupUseCase(get(), get()) }
}