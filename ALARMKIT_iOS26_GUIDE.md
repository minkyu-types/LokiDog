# iOS 26 AlarmKit 통합 가이드

LokiDog 앱을 iOS 26의 AlarmKit 프레임워크를 사용하도록 업그레이드하는 가이드입니다.

## 개요

iOS 26부터 Apple은 **AlarmKit** 프레임워크를 제공하여 타사 앱에서 시스템 레벨 알람을 만들 수 있게 되었습니다. 이는 기존 UNUserNotificationCenter 방식보다 다음과 같은 장점이 있습니다:

### AlarmKit의 장점

✅ **무음/진동 모드 무시**: 알람이 항상 소리와 함께 울립니다
✅ **Focus 모드 관통**: 사용자의 Focus 설정을 무시하고 알람이 표시됩니다
✅ **Lock Screen 통합**: Lock Screen, Dynamic Island, StandBy 모드에 알람이 표시됩니다
✅ **시스템 레벨 신뢰성**: 백그라운드에서 앱이 종료되어도 알람이 안정적으로 작동합니다
✅ **스누즈 기능**: 기본적으로 스누즈 기능이 제공됩니다

### 제한사항

⚠️ **iOS 26+ 전용**: iOS 26 이상에서만 사용 가능합니다
⚠️ **사용자 권한 필요**: `NSAlarmKitUsageDescription` 권한 요청이 필요합니다
⚠️ **Kotlin/Native Interop**: Kotlin/Native에서 직접 호출이 어려워 Swift 브리지가 필요합니다

## 완료된 작업

### 1. iOS Deployment Target 업데이트

`iosApp/iosApp.xcodeproj/project.pbxproj` 파일에서:
```
IPHONEOS_DEPLOYMENT_TARGET = 26.0;
```

### 2. Info.plist 설정

`iosApp/iosApp/Info.plist`에 AlarmKit 권한 추가:
```xml
<key>MinimumOSVersion</key>
<string>26.0</string>

<key>NSAlarmKitUsageDescription</key>
<string>알람을 설정하고 시간에 맞춰 울리기 위해 AlarmKit 권한이 필요합니다.</string>
```

### 3. Swift 브리지 구현

`iosApp/iosApp/AlarmKitBridge.swift` 파일 생성:
- `@objc public class AlarmKitBridge`로 Objective-C 호환 브리지 클래스 구현
- 고정 시간 알람, 주간 반복 알람, 카운트다운 타이머 지원
- 권한 요청 및 알람 취소 기능 포함

주요 메서드:
```swift
- requestAuthorization(completion:) // 권한 요청
- scheduleFixedAlarm(...) // 1회성 알람
- scheduleWeeklyAlarm(...) // 주간 반복 알람
- scheduleTimer(...) // 카운트다운 타이머
- cancelAlarm(...) // 알람 취소
- cancelAllAlarms(...) // 모든 알람 취소
```

## 구현 방법

### Swift 브리지 사용 (Kotlin/Native에서)

AlarmKit은 Swift 전용 API이므로 Kotlin/Native에서 직접 호출할 수 없습니다. 대신 Objective-C 호환 브리지를 통해 접근해야 합니다:

```kotlin
// Kotlin/Native에서 Swift 브리지 사용 예시 (cinterop 필요)
@ObjCName("AlarmKitBridge", exact = true)
external class AlarmKitBridge : NSObject {
    companion object {
        val shared: AlarmKitBridge
    }

    fun requestAuthorizationWithCompletion(completion: (Boolean) -> Unit)

    fun scheduleFixedAlarmWithIdentifier(
        identifier: String,
        hour: Int,
        minute: Int,
        title: String,
        body: String,
        completion: (Boolean, String?) -> Unit
    )

    fun scheduleWeeklyAlarmWithIdentifier(
        identifier: String,
        hour: Int,
        minute: Int,
        weekdays: List<Int>,
        title: String,
        body: String,
        completion: (Boolean, String?) -> Unit
    )

    fun scheduleTimerWithIdentifier(
        identifier: String,
        durationSeconds: Double,
        title: String,
        body: String,
        completion: (Boolean, String?) -> Unit
    )

    fun cancelAlarmWithIdentifier(
        identifier: String,
        completion: (Boolean) -> Unit
    )

    fun cancelAllAlarmsWithCompletion(completion: (Boolean) -> Unit)
}
```

### Xcode 프로젝트 설정

1. **Xcode에서 iosApp.xcodeproj 열기**

2. **AlarmKitBridge.swift 파일 추가 확인**
   - `iosApp` 그룹에 `AlarmKitBridge.swift` 파일이 포함되어 있는지 확인
   - Target Membership에서 `iosApp`이 체크되어 있는지 확인

3. **Build Settings 확인**
   - iOS Deployment Target: 26.0
   - Swift Language Version: Swift 5.0 이상

4. **Framework 링크**
   - AlarmKit.framework는 iOS 26+에 기본 포함되어 있으므로 별도 추가 불필요
   - Build Phases → Link Binary With Libraries에서 자동으로 링크됨

### 앱 시작 시 권한 요청

`iosApp/iosApp/iosApp.swift` 파일에서 권한 요청:

```swift
import SwiftUI
import ComposeApp

@main
struct ComposeApp: App {

    init() {
        KoinHelperKt.doInitKoin()

        // iOS 알람 델리게이트 등록
        let alarmReceiver = AlarmReceiver()
        alarmReceiver.register()

        // AlarmKit 권한 요청 (iOS 26+)
        if #available(iOS 26.0, *) {
            AlarmKitBridge.shared.requestAuthorization { granted in
                if granted {
                    print("✅ AlarmKit 권한 승인됨")
                } else {
                    print("⚠️ AlarmKit 권한 거부됨")
                }
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.all)
        }
    }
}
```

## 마이그레이션 전략

현재 앱은 `UNUserNotificationCenter` 기반으로 구현되어 있습니다. AlarmKit으로 전환하려면:

### 옵션 1: iOS 버전별 분기 처리

iOS 26+ 기기는 AlarmKit 사용, 이전 버전은 기존 방식 유지:

```kotlin
// PlatformAlarmScheduler.ios.kt
actual class PlatformAlarmScheduler : AlarmScheduler {

    private val useAlarmKit = isIOS26OrLater()

    override suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        if (useAlarmKit) {
            scheduleWithAlarmKit(repeatDays, alarm)
        } else {
            scheduleWithUNNotification(repeatDays, alarm)
        }
    }

    private fun isIOS26OrLater(): Boolean {
        // iOS 버전 체크 로직
        return false // TODO: 구현 필요
    }
}
```

### 옵션 2: AlarmKit 전용 빌드

iOS 26+만 지원하는 전용 빌드 생성:
- 최소 버전 요구사항: iOS 26.0
- App Store에서 iOS 26+ 기기만 다운로드 가능하도록 설정

## 테스트 방법

### 1. 시뮬레이터 테스트

Xcode에서 iOS 26 시뮬레이터 선택 후 빌드:
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
    -scheme iosApp \
    -destination 'platform=iOS Simulator,name=iPhone 16 Pro,OS=26.0'
```

### 2. 실기기 테스트

iOS 26이 설치된 iPhone/iPad에서:
1. Xcode에서 기기 연결
2. Run 버튼 클릭하여 앱 설치
3. 알람 권한 허용
4. 알람 설정 후 시간 확인

### 3. 알람 기능 확인

- [ ] 고정 시간 알람이 정확한 시간에 울리는지
- [ ] 주간 반복 알람이 지정한 요일에만 울리는지
- [ ] 타이머가 설정한 시간 후에 울리는지
- [ ] Lock Screen에 알람이 표시되는지
- [ ] Dynamic Island에 알람이 표시되는지
- [ ] 무음/진동 모드에서도 소리가 나는지
- [ ] Focus 모드에서도 알람이 울리는지
- [ ] 스누즈 버튼이 작동하는지
- [ ] 알람 취소가 정상 동작하는지

## 주의사항

### 1. iOS 26 SDK 필요

AlarmKit을 빌드하려면 Xcode 17 이상이 필요합니다 (iOS 26 SDK 포함).

### 2. Kotlin Multiplatform 제약

Kotlin/Native는 Swift 코드를 직접 호출할 수 없습니다. Objective-C 브리지를 통해야 하며, `@objc` 어노테이션으로 노출된 API만 사용 가능합니다.

### 3. WidgetKit/ActivityKit 의존성

AlarmKit은 내부적으로 WidgetKit과 ActivityKit에 의존합니다. 특정 기능(Live Activities 등)을 사용하려면 추가 설정이 필요할 수 있습니다.

### 4. App Store 제출

- AlarmKit 사용 이유를 App Store Review Notes에 명시
- `NSAlarmKitUsageDescription`에 명확한 사용 목적 기재
- 스크린샷에 알람 기능 포함

## 추가 자료

- [Apple Developer Documentation - AlarmKit](https://developer.apple.com/documentation/AlarmKit)
- [WWDC 2025 - Wake up to the AlarmKit API](https://developer.apple.com/videos/play/wwdc2025/230/)
- [AlarmKit Sample Code](https://developer.apple.com/documentation/AlarmKit/scheduling-alarms)

## 문제 해결

### Q: "AlarmKit framework not found" 에러
A: iOS 26 SDK가 설치된 Xcode 17+ 버전을 사용하고 있는지 확인하세요.

### Q: 알람이 울리지 않음
A:
1. `NSAlarmKitUsageDescription`이 Info.plist에 있는지 확인
2. 권한이 승인되었는지 확인 (`AlarmManager.shared.authorizationState`)
3. 알람이 정상적으로 스케줄되었는지 로그 확인

### Q: Kotlin에서 AlarmKitBridge를 찾을 수 없음
A:
1. AlarmKitBridge.swift가 iosApp 타겟에 포함되어 있는지 확인
2. `@objc public class`로 선언되어 있는지 확인
3. Kotlin cinterop 설정이 필요할 수 있음

## 다음 단계

1. **Xcode 프로젝트 빌드 테스트**
   ```bash
   cd iosApp
   xcodebuild -project iosApp.xcodeproj -scheme iosApp -configuration Debug
   ```

2. **Kotlin/Native Interop 설정**
   - AlarmKitBridge를 Kotlin에서 호출할 수 있도록 cinterop 설정
   - 또는 Kotlin Multiplatform의 `expect`/`actual` 패턴으로 구현

3. **기존 PlatformAlarmScheduler 마이그레이션**
   - UNUserNotificationCenter 코드를 AlarmKitBridge 호출로 교체
   - 버전별 분기 처리 구현

4. **통합 테스트**
   - 앱의 모든 알람 기능이 AlarmKit으로 정상 작동하는지 확인
   - 다양한 시나리오 테스트 (백그라운드, 재부팅, 앱 삭제 등)

## 결론

iOS 26의 AlarmKit을 사용하면 타사 앱에서도 시스템 레벨 알람을 구현할 수 있어 사용자 경험이 크게 향상됩니다. Swift 브리지를 통해 Kotlin Multiplatform 앱에서도 AlarmKit을 활용할 수 있습니다.

현재 프로젝트에는:
- ✅ iOS 26 타겟팅 완료
- ✅ Info.plist 권한 추가 완료
- ✅ AlarmKitBridge.swift 구현 완료
- ⚠️ Kotlin/Native Interop 설정 필요
- ⚠️ PlatformAlarmScheduler 마이그레이션 필요

다음 작업은 Xcode에서 프로젝트를 열어 AlarmKitBridge.swift를 빌드하고, Kotlin 코드에서 호출할 수 있도록 통합하는 것입니다.