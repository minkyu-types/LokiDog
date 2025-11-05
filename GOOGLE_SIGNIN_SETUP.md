# Google Sign-In 설정 가이드

## 문제: "GetCredentialResponse error returned from framework"

이 오류는 주로 Google Cloud Console 설정이 완료되지 않았거나 잘못되었을 때 발생합니다.

## 해결 방법

### 1. SHA-1 인증서 지문 확인

**Debug SHA-1 (개발용):**
```bash
# macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**Release SHA-1 (배포용):**
```bash
# Gradle로 확인 (추천)
./gradlew signingReport

# 또는 직접 keystore 확인
keytool -list -v -keystore /path/to/your/release.keystore -alias your-alias
```

출력 예시:
```
Certificate fingerprints:
	 SHA1: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD
	 SHA256: ...
```

### 2. Google Cloud Console 설정

1. **Google Cloud Console 접속**
   - https://console.cloud.google.com/

2. **프로젝트 선택 또는 생성**
   - 기존 프로젝트 선택 또는 새 프로젝트 생성

3. **OAuth 동의 화면 설정**
   - 왼쪽 메뉴: `API 및 서비스` > `OAuth 동의 화면`
   - User Type: `외부` 선택 (테스트용)
   - 앱 이름: `LokiDog`
   - 사용자 지원 이메일 및 개발자 연락처 이메일 입력
   - 저장 후 계속

4. **OAuth 클라이언트 ID 생성**

   **a) Web Client ID 생성:**
   - `API 및 서비스` > `사용자 인증 정보`
   - `+ 사용자 인증 정보 만들기` > `OAuth 클라이언트 ID`
   - 애플리케이션 유형: `웹 애플리케이션`
   - 이름: `LokiDog Web Client`
   - 만들기
   - **생성된 클라이언트 ID 복사 (중요!)**
     - 형식: `xxxxx.apps.googleusercontent.com`

   **b) Android Client ID 생성:**
   - `+ 사용자 인증 정보 만들기` > `OAuth 클라이언트 ID`
   - 애플리케이션 유형: `Android`
   - 이름: `LokiDog Android`
   - 패키지 이름: `dev.loki.dog.androidApp`
   - **SHA-1 인증서 지문:** (위에서 확인한 값 붙여넣기)
   - 만들기

   **Debug와 Release 모두 등록:**
   - Debug SHA-1로 하나
   - Release SHA-1로 하나 더 생성

### 3. Web Client ID를 앱에 적용

**방법 1: BuildConfig 사용 (권장)**

`composeApp/build.gradle.kts`에 추가:
```kotlin
buildConfig {
    // Debug build
    buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"YOUR_WEB_CLIENT_ID.apps.googleusercontent.com\"")
}
```

사용:
```kotlin
val googleAuthHelper = AndroidGoogleAuthHelper(
    context = activity,
    webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
)
```

**방법 2: local.properties 사용 (보안)**

`local.properties` (gitignore에 포함):
```properties
google.web.client.id=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

`build.gradle.kts`:
```kotlin
val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
val googleWebClientId = properties.getProperty("google.web.client.id") ?: ""

buildConfig {
    buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
}
```

### 4. Internet 권한 확인

`composeApp/src/androidMain/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 5. Google Play Services 업데이트

- 디바이스에서 Google Play Services가 최신 버전인지 확인
- 설정 > 앱 > Google Play 서비스 > 업데이트

## 테스트

### Debug 빌드 테스트
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### 로그 확인
```bash
adb logcat | grep -E "GoogleAuthHelper|CredentialManager|GetCredential"
```

## 일반적인 오류와 해결

### "INVALID_CLIENT" 오류
- **원인:** SHA-1 인증서가 Cloud Console에 등록되지 않음
- **해결:** 위 2-4-b 단계 재확인

### "API_DISABLED" 오류
- **원인:** Google Identity Services API가 비활성화됨
- **해결:**
  1. Cloud Console > `API 및 서비스` > `라이브러리`
  2. "Google Identity Services API" 검색
  3. 사용 설정

### "NoCredentialException" 오류
- **원인:** Google Play Services 미설치 또는 구버전
- **해결:** 디바이스에서 Google Play Services 업데이트

### "Activity context required" 오류
- **원인:** Application Context가 전달됨
- **해결:** Activity Context 전달 확인

## 체크리스트

- [ ] Debug SHA-1 확인 및 복사
- [ ] Release SHA-1 확인 및 복사 (배포용)
- [ ] Google Cloud Console 프로젝트 생성
- [ ] OAuth 동의 화면 설정
- [ ] Web Client ID 생성 및 복사
- [ ] Android Client ID 생성 (Debug SHA-1)
- [ ] Android Client ID 생성 (Release SHA-1)
- [ ] Web Client ID를 BuildConfig에 추가
- [ ] 앱 빌드 및 테스트
- [ ] 로그인 테스트 성공 확인

## 참고 문서

- [Google Identity Services - Android](https://developers.google.com/identity/android-credential-manager)
- [Credential Manager Guide](https://developer.android.com/training/sign-in/credential-manager)
- [OAuth 2.0 for Mobile Apps](https://developers.google.com/identity/protocols/oauth2/native-app)