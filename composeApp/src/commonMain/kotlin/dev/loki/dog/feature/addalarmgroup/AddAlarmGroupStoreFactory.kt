package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.BaseStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope

class AddAlarmGroupStoreFactory: BaseStoreFactory<AddAlarmGroupState, AddAlarmGroupSideEffect>() {
    override fun create(
        coroutineScope: CoroutineScope,
        scope: Scope
    ): BaseStore<AddAlarmGroupState, AddAlarmGroupSideEffect> {
        return AddAlarmGroupStore(
            coroutineScope = coroutineScope,
            scope = scope
        )
    }
}