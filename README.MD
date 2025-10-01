# Compose Multiplatform Application

## 공통
- 공통 UI 개발을 위한 CMP 사용
- 안드로이드/iOS 각각의 네이티브 기능 연동 및 활용을 위해 Notification 활용이 가능한 알람/타이머 앱
- 1차적으로 로컬 DB를 활용할 계획이며, 추후에 Firebase를 활용한 기능을 추가할 예정
- 공통 코드이니만큼, 유지보수성 증가의 효율이 높다고 판단되어 보일러플레이트 코드 감소와 유지보수성 증가를 최우선으로 고려한 코드를 작성하는 것이 목표
- 

## 아키텍처
- composeApp(androidApp) / iosApp으로 구성되어 있습니다.
- 멀티 모듈 클린 아키텍처 + MVI(Orbit 프레임워크) 구조로 설계하였습니다.
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
- Ai assistant(Chat GPT 5 + Copilot)

## 주요 기능
- 알람 관리
 - 알람 그룹 목록 조회
 - 알람 그룹 생성/임시저장
 - 알람 그룹 수정/삭제
- 타이머
