# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LokiDog is a Kotlin Multiplatform (KMP) alarm application targeting Android and iOS using Compose Multiplatform. The project implements MVI architecture using Orbit MVI framework with Clean Architecture separation across domain, data, and presentation layers.

## Build & Development Commands

### Prerequisites
- JDK 17 or higher
- Add `local.properties` with Android SDK path
- For iOS: Xcode installed

### Build Commands

**Android:**
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Find APK at: composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Build release
./gradlew :composeApp:assembleRelease
```

**iOS:**
```bash
# Build XCFramework for iOS
./gradlew assembleXCFramework

# Or open in Xcode: iosApp/iosApp.xcodeproj
```

### Testing

**Android:**
```bash
# Run instrumented tests on connected device
./gradlew :composeApp:connectedDebugAndroidTest

# Run unit tests
./gradlew :composeApp:testDebugUnitTest
```

**iOS:**
```bash
# Run iOS simulator tests
./gradlew :composeApp:iosSimulatorArm64Test
```

**All targets:**
```bash
# Run all tests across all platforms
./gradlew allTests

# Run checks (tests + lint)
./gradlew check
```

### Run Application

**Android:**
- Open project in Android Studio
- Run imported Android configuration

**iOS:**
- Open `iosApp/iosApp.xcodeproj` in Xcode
- Or use Kotlin Multiplatform Mobile plugin in Android Studio

### Clean Build
```bash
./gradlew clean
```

## Architecture Overview

### MVI Pattern with Orbit

The codebase uses Model-View-Intent (MVI) architecture powered by Orbit MVI framework:

**Base Classes (composeApp/src/commonMain/kotlin/feature/base/):**
- `BaseState`: Interface with `loadState: LoadState` property
- `BaseAction`: Marker interface for all user actions/intents
- `BaseSideEffect`: Base for one-time effects (navigation, snackbars, dialogs)
- `BaseStore<S, SE>`: Extends `ContainerHost<S, SE>` - core MVI orchestrator
- `BaseSharedViewModel<S, SE>`: Bridges Jetpack ViewModel with Orbit Store

**State Management Pattern:**
```kotlin
// Update state immutably
setState { copy(items = newItems) }

// Post one-time side effects
postEffect(ShowSnackbar("Success"))
```

**Feature Structure Example:**
```
feature/
├── main/
│   ├── AlarmMainScreen.kt         # Composable UI
│   ├── AlarmMainViewModel.kt      # ViewModel bridge
│   ├── AlarmMainStore.kt          # MVI Store (business logic)
│   ├── AlarmMainStoreFactory.kt   # Store factory
│   ├── AlarmMainState.kt          # Immutable state
│   ├── AlarmMainAction.kt         # User intents
│   └── AlarmMainSideEffect.kt     # One-time effects
```

### Module Architecture

```
LokiDog/
├── composeApp/                    # Main app module
│   ├── src/commonMain/            # Shared code (UI, ViewModels, DI)
│   ├── src/androidMain/           # Android-specific (AlarmManager, BroadcastReceiver)
│   ├── src/iosMain/               # iOS-specific (UNNotifications)
│   ├── alarm-domain/              # Pure domain layer (models, use cases, repo interfaces)
│   └── alarm-data/                # Data layer (Room DB, repo implementations)
└── iosApp/                        # iOS native wrapper
```

**Dependency Flow:**
- `composeApp` depends on `alarm-domain`
- `composeApp` (androidMain/iosMain) depends on `alarm-data`
- `alarm-data` depends on `alarm-domain`
- `alarm-domain` has no external module dependencies (pure domain)

### Domain Layer (alarm-domain)

**Core Models:**
- `Alarm`: Individual alarm with time, memo, activation status
- `AlarmGroup`: Group of alarms with repeat days, activation status
- `AlarmGroupWithAlarms`: Composite of group + associated alarms
- `DayOfWeek`: From kotlinx.datetime

**Repository Interfaces:**
- `AlarmRepository`: CRUD for individual alarms
- `AlarmGroupRepository`: CRUD for alarm groups, returns `Flow<>` for reactive streams

**Use Cases (16+ total):**
- `GetAlarmGroupsUseCase`, `GetTempAlarmGroupsUseCase`
- `InsertAlarmGroupUseCase`, `UpdateAlarmGroupUseCase`, `DeleteAlarmGroupUseCase`
- `GetAlarmGroupWithAlarmsUseCase`
- `UpsertAlarmUseCase`, `DeleteAlarmUseCase`
- `RescheduleAlarmUseCase` - Re-schedules alarms when repeat days change

**Error Handling:**
```kotlin
sealed class DomainResult<out T> {
    data class Success<T>(val data: T)
    data class Error(val throwable: Throwable)
}
```

### Data Layer (alarm-data)

**Room Database:**
- Database version: 7
- Entities: `AlarmEntity`, `AlarmGroupEntity`
- Schema directory: `composeApp/schemas`
- DAOs: `AlarmDao`, `AlarmGroupDao`

**Type Converters:**
- `DayOfWeekSetConverter`: Converts `Set<DayOfWeek>` to/from String for Room

**Platform-Specific DB Setup:**
- `DatabaseBuilder.kt` (commonMain): expect function
- `DatabaseBuilder.android.kt`: Uses `Room.databaseBuilder()` with Android Context
- `DatabaseBuilder.ios.kt`: iOS-specific SQLite configuration
- Configured with `Dispatchers.IO` for queries

**Mappers:**
- `AlarmMapper`: Entity ↔ Domain model transformation
- `AlarmGroupMapper`: Entity ↔ Domain model transformation

### Presentation Layer (composeApp)

**Navigation:**
- Uses Jetpack Navigation Compose
- Routes defined in `navigation/Screens.kt`:
  - `MainScreen.ALARM` - Main alarm list
  - `MainScreen.TIMER` - Timer feature
  - `SubScreen.ALARM_GROUP_ADD` - Add/edit alarm group (with groupId argument)
  - `SubScreen.ALARM_GROUP_TEMP_LIST` - Temporary unsaved groups

**Key Features:**
- `AlarmMainScreen`: Main list with FAB menu, selection mode, sorting
- `AddAlarmGroupScreen`: Create/edit groups with time wheel picker
- `TempAlarmGroupsScreen`: Manage unsaved draft groups

**Shared Components (composeApp/src/commonMain/kotlin/component/):**
- `AlarmGroupItem.kt`, `AlarmGroupSwipeToDeleteItem.kt`
- `TimeWheelBottomSheet.kt` - Custom time picker
- `InfiniteCircularList.kt` - Circular scrolling for time picker
- `SwipeToDeleteItem.kt`, `DraggableItem.kt`, `SelectionModeItem.kt`

### Platform-Specific Implementations (Expect/Actual)

**Alarm Scheduling:**

Common interface (composeApp/src/commonMain/kotlin/expect/AlarmScheduler.kt):
```kotlin
interface AlarmScheduler {
    suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm)
    suspend fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm)
    suspend fun cancelByGroup(groupId: Long)
}
```

**Android Implementation:**
- Uses `AlarmManager.setExactAndAllowWhileIdle()`
- Requires `SCHEDULE_EXACT_ALARM` permission (API 31+)
- Routes to `AlarmReceiver` (BroadcastReceiver)

**iOS Implementation:**
- Uses `UNUserNotificationCenter` for local notifications
- `UNCalendarNotificationTrigger` for weekday-based repeating
- Notification IDs format: `"group_{groupId}_{alarmId}_{weekday}"`

**Platform Module Discovery:**

Each platform provides its own DI modules via expect/actual:
```kotlin
expect fun getAlarmDatabaseModule(): Module
expect fun getRepositoryModule(): List<Module>
expect fun getAlarmScheduler(): Module
```

### Dependency Injection (Koin)

**Module Organization:**

Presentation (composeApp/src/commonMain/di/):
- `ViewModelModule.kt` - ViewModels
- `StoreFactoryModule.kt` - Store factories
- `MapperModule.kt` - Presentation mappers

Domain (alarm-domain/src/commonMain/di/):
- `AlarmUseCaseModule.kt` - All use cases

Data (alarm-data/src/commonMain/di/):
- `AlarmRepositoryModule.kt` - Repository implementations
- `AlarmMapperModule.kt` - Data mappers

Platform-specific:
- Database module (expect/actual)
- Alarm scheduler module (expect/actual)

**Initialization:**
```kotlin
// Android: MainApplication.onCreate()
startKoin {
    androidContext(this@MainApplication)
    modules(getAppModules())
}

// iOS: main.kt
initKoin()
```

**Koin Scope Management:**
- `BaseSharedViewModel` creates scoped `KoinScopeComponent`
- Dependencies resolved via `by inject()`
- Scope cleaned up in `ViewModel.onCleared()`

## Key Patterns & Conventions

### Temporary Items Pattern
- Models have `isTemp: Boolean` flag for unsaved drafts
- Separate repository method: `getTempAlarmGroups()`
- Dedicated screen for managing drafts

### Reactive Data Flow
- Repositories return `Flow<>` for observable data
- Use cases wrap flows with `DomainResult` for error handling
- ViewModels/Stores collect flows and update state

### Load State Management
```kotlin
sealed interface LoadState {
    data object Idle
    data object Loading
    data object Success
    data class Error(val message: String)
}
```

### Batch Operations
Use coroutines with `async`/`awaitAll` for concurrent operations:
```kotlin
val jobs = items.map { async { process(it) } }
jobs.awaitAll()
```

### String Resources
- Uses Compose Resources (multiplatform)
- Located in `composeApp/src/commonResources/`
- Access via `stringResource(Res.string.key)`

## Important Implementation Notes

### When Adding New Features

1. **Domain First**: Create models, repository interface, use cases in `alarm-domain`
2. **Data Layer**: Implement repository, create entities/DAOs in `alarm-data`
3. **Presentation**: Create State/Action/SideEffect, Store, ViewModel, Screen
4. **DI**: Register in appropriate Koin modules
5. **Platform-Specific**: Add expect/actual implementations if needed

### When Modifying Database Schema

1. Update entity classes in `alarm-data/src/commonMain/kotlin/.../model/`
2. Increment database version in `AlarmDatabase.kt`
3. Update type converters if needed
4. Schemas are exported to `composeApp/schemas/`
5. Room is configured to **NOT** use destructive migrations - add migration strategy if needed

### When Adding Platform-Specific Code

1. Define expect declaration in `commonMain`
2. Implement actual in `androidMain` and `iosMain`
3. Update platform DI modules via `Platform.kt`
4. Common patterns:
   - Database builder
   - Alarm/notification scheduling
   - File I/O operations
   - Platform UI components

### Navigation with Arguments

```kotlin
// Define route with argument
composable(
    route = "${SubScreen.ALARM_GROUP_ADD.name}/{groupId}",
    arguments = listOf(navArgument("groupId") { type = NavType.LongType })
) { backstackEntry ->
    val groupId = backstackEntry.arguments?.getLong("groupId") ?: 0L
    // Use groupId
}

// Navigate with argument
navController.navigate("${SubScreen.ALARM_GROUP_ADD.name}/$groupId")
```

### MVI Store Implementation Pattern

```kotlin
class MyStore(
    private val useCase: UseCase
) : BaseStore<MyState, MySideEffect>() {

    override val container = scope.container<MyState, MySideEffect>(MyState())

    fun onAction(action: MyAction) {
        when (action) {
            is MyAction.Load -> load()
            is MyAction.Update -> update(action.item)
        }
    }

    private fun load() = intent {
        setState { copy(loadState = LoadState.Loading) }
        useCase().collect { result ->
            when (result) {
                is DomainResult.Success -> {
                    setState {
                        copy(
                            items = result.data,
                            loadState = LoadState.Success
                        )
                    }
                }
                is DomainResult.Error -> {
                    setState {
                        copy(loadState = LoadState.Error(result.throwable.message ?: ""))
                    }
                }
            }
        }
    }
}
```

## Testing

- Common tests: `composeApp/src/commonTest/`
- Android instrumented tests: Use `connectedDebugAndroidTest`
- Test dependencies: `kotlin("test")`, `compose.uiTest`, `kotlinx.coroutines.test`
- Test source set migration note: Migrate from `src/test/kotlin` to `src/androidUnitTest/kotlin` (see Gradle warning)

## Code Locations Reference

| Component | Path |
|-----------|------|
| MVI Base Classes | `composeApp/src/commonMain/kotlin/feature/base/` |
| Domain Models | `composeApp/alarm-domain/src/commonMain/kotlin/dev/loki/[alarm\|alarmgroup]/model/` |
| Use Cases | `composeApp/alarm-domain/src/commonMain/kotlin/dev/loki/[alarm\|alarmgroup]/usecase/` |
| Repository Interfaces | `composeApp/alarm-domain/src/commonMain/kotlin/dev/loki/[alarm\|alarmgroup]/repository/` |
| Room Database | `composeApp/alarm-data/src/commonMain/kotlin/dev/loki/alarm_data/database/` |
| Data Entities | `composeApp/alarm-data/src/commonMain/kotlin/dev/loki/alarm_data/model/` |
| Repository Implementations | `composeApp/alarm-data/src/commonMain/kotlin/dev/loki/alarm_data/repository/` |
| Features (Screens/Stores) | `composeApp/src/commonMain/kotlin/feature/[main\|addalarmgroup\|temp]/` |
| Shared Components | `composeApp/src/commonMain/kotlin/component/` |
| Navigation | `composeApp/src/commonMain/kotlin/navigation/` |
| DI Modules | `composeApp/src/commonMain/kotlin/di/` |
| Platform Code | `composeApp/src/[androidMain\|iosMain]/` |
| Theme | `composeApp/src/commonMain/kotlin/theme/` |

## Additional Notes

- **Min SDK**: Android 29 (Android 10)
- **Target SDK**: Android 36
- **Compile SDK**: 36
- **Application ID**: `dev.loki.dog.androidApp`
- **iOS Framework**: Static XCFramework named "ComposeApp"
- **Database Schema Export**: `composeApp/schemas/`
