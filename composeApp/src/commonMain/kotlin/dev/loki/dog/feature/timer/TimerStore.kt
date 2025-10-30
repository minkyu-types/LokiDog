package dev.loki.dog.feature.timer

import dev.loki.AlarmScheduler
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.mapper.TimerHistoryMapper
import dev.loki.timerhistory.model.TimerHistory
import dev.loki.timerhistory.usecase.CreateTimerHistoryUseCase
import dev.loki.timerhistory.usecase.DeleteTimerHistoryUseCase
import dev.loki.timerhistory.usecase.GetTimerHistoriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.scope.Scope
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TimerStore(
    private val coroutineScope: CoroutineScope,
    scope: Scope,
    private val alarmScheduler: AlarmScheduler,
    private val getTimerHistoriesUseCase: GetTimerHistoriesUseCase,
    private val createTimerHistoryUseCase: CreateTimerHistoryUseCase,
    private val deleteTimerHistoryUseCase: DeleteTimerHistoryUseCase,
    private val timerHistoryMapper: TimerHistoryMapper
) : BaseStore<TimerState, TimerSideEffect>(
    coroutineScope = coroutineScope,
    scope = scope,
    initialState = TimerState(
        totalDuration = 0L,
        remainingTime = 0L,
    )
) {
    private val durationFlow = MutableStateFlow(60_000L)
    private var timerJob: Job? = null
    private var recentTime: Long? = null

    init {
        coroutineScope.launch {
            getTimerHistoriesUseCase().collect { histories ->
                val presentationHistories = histories.map {
                    timerHistoryMapper.mapToPresentation(it)
                }
                setState { copy(timerHistories = presentationHistories) }
            }
        }

        coroutineScope.launch {
            durationFlow.collect { time ->
                setState { copy(remainingTime = time) }
            }
        }
    }

    override fun dispatch(action: BaseAction) {
        when (action) {
            is TimerAction.SetTime -> setTimer(action.time)
            is TimerAction.Start -> startTimer()
            is TimerAction.Pause -> pauseTimer()
            is TimerAction.Resume -> resumeTimer()
            is TimerAction.Quit -> quitTimer()
            is TimerAction.DeleteHistory -> deleteHistory(action.history)
        }
    }

    private fun setTimer(time: Long) {
        setState { copy(totalDuration = time) }
        durationFlow.value = time
    }

    @OptIn(ExperimentalTime::class)
    private fun startTimer() {
        if (timerJob?.isActive == true) return

        setState {
            val alarmTime = Clock.System.now().toEpochMilliseconds() + this.remainingTime
            alarmScheduler.scheduleTimer(alarmTime)
            recentTime = totalDuration
            copy(
                isRunning = true,
                isPaused = false,
                alarmTime = Clock.System.now().toEpochMilliseconds() + this.totalDuration
            )
        }
        timerJob = coroutineScope.launch {
            while (durationFlow.value > 0) {
                delay(1_000L)
                durationFlow.update { it - 1_000L }
            }
            postEffect(TimerSideEffect.Finished)
            setState { copy(isRunning = false) }
            durationFlow.value = recentTime ?: 20_000L
            createHistory(recentTime ?: 20_000L)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun resumeTimer() {
        setState {
            val alarmTime = Clock.System.now().toEpochMilliseconds() + this.remainingTime
            alarmScheduler.scheduleTimer(alarmTime)
            copy(
                isRunning = true,
                isPaused = false,
                pausedAt = this.remainingTime,
                alarmTime = alarmTime
            )
        }
        timerJob = coroutineScope.launch {
            while (durationFlow.value > 0) {
                delay(1_000L)
                durationFlow.update { it - 1_000L }
            }
            postEffect(TimerSideEffect.Finished)
            setState { copy(isRunning = false) }
            durationFlow.value = recentTime ?: 20_000L
            createHistory(recentTime ?: 20_000L)
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        setState {
            copy(
                isRunning = false,
                isPaused = true,
                pausedAt = this.remainingTime.coerceAtLeast(0)
            )
        }
        alarmScheduler.cancelTimer()
    }

    private fun quitTimer() {
        timerJob?.cancel()
        timerJob = null
        alarmScheduler.cancelTimer()
        setState { copy(isRunning = false, remainingTime = recentTime ?: 20_000L) }
        durationFlow.value = recentTime ?: 20_000L
    }

    private suspend fun createHistory(timeMillis: Long) {
        createTimerHistoryUseCase(
            TimerHistory(id = 0L, durationTimeMillis = timeMillis)
        )
    }

    private fun deleteHistory(historyModel: TimerHistoryModel) {
        val domainHistory = timerHistoryMapper.mapToDomain(historyModel)
        coroutineScope.launch {
            deleteTimerHistoryUseCase(domainHistory)
        }
    }
}