package dev.loki.dog.feature.main

import dev.loki.dog.feature.alarm.AlarmMapper
import dev.loki.dog.feature.alarmgroup.AlarmGroupMapper
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.BaseStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope

class AlarmMainStoreFactory(
    private val alarmMapper: AlarmMapper,
    private val alarmGroupMapper: AlarmGroupMapper
): BaseStoreFactory<AlarmMainState, AlarmMainSideEffect>() {
    override fun create(coroutineScope: CoroutineScope, scope: Scope): BaseStore<AlarmMainState, AlarmMainSideEffect> {
        return AlarmMainStore(
            coroutineScope = coroutineScope,
            scope = scope
        )
    }
}