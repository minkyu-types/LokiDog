# Compose Multiplatform Application

## 공통
- 공통 UI 개발을 위해 CMP를 사용하였습니다.
- 안드로이드/iOS 각각의 네이티브 기능 사용 및 활용을 위해 다양한 기능 확장/구현이 가능한 알람/타이머 앱입니다.
- 1차적으로 로컬 DB를 활용할 계획이며, 추후에 Firebase를 활용한 기능을 추가할 예정입니다.
- 공통 코드이니만큼, 유지보수성 증가의 효율이 높다고 판단되어 보일러플레이트 코드 감소와 유지보수성 증가를 최우선으로 고려한 코드를 작성하는 것이 목표입니다.

## 아키텍처
- composeApp(androidApp) / iosApp으로 구성되어 있습니다.
- 멀티 모듈 클린 아키텍처 + MVI 구조로 설계하였습니다.
<img width="2342" height="1531" alt="Image" src="https://github.com/user-attachments/assets/d4a146fe-5c8f-4ac0-bc6a-1ab279d3d833" />

## 사용 기술
- Compose MultiPlatform
- Kotlin `2.2.0`
- Clean Architecture + MVI 패턴(Orbit 프레임워크 `10.0.0`)
- Jetpack Compose, Workmanager, Room, Lifecycle
- Koin `3.6.4` + Ksp `2.2.0-2.0.2`
- Coroutines `1.10.3`
- Kotlinx serialization, datetime
- Kermit `2.0.4`
- Ai assistant(Claude CLI + Copilot)

## 주요 기능
- 알람 관리
 - 알람 그룹 목록 조회
 - 알람 그룹 생성/임시저장
 - 알람 그룹 수정/삭제
- 타이머
