# AndroidPractice

초보 안드로이드 개발자를 위한 기능 연습 프로젝트입니다.

## 프로젝트 개요

- **패키지**: `com.juyeon.androidpractice`
- **언어**: Kotlin
- **UI**: Jetpack Compose
- **minSdk**: 24 / **compileSdk**: 37 / **targetSdk**: 36

## 연습 주제

1. **그라데이션** — Brush.linearGradient / radialGradient 등 Compose에서 그라데이션 적용
2. **Screen & Compose 방식** — NavHost를 이용한 화면 전환, Composable 구조 설계
3. **MVVM** — ViewModel + StateFlow(or LiveData) + Repository 패턴
4. **Coroutine** — viewModelScope, Flow, suspend 함수 활용
5. **알람 & 소리** — AlarmManager, MediaPlayer / SoundPool
6. **UI 동적 모션** — AnimatedVisibility, animateFloatAsState, Transition API 등 Compose 애니메이션
7. **이미지 처리** — Coil로 URL 이미지 로딩, 갤러리/카메라 연동, 이미지 크롭
8. **Mock 데이터 & 임시 모델** — 백엔드 API 완성 전에 Data class + FakeRepository로 UI 개발하는 패턴

## 폴더 구조 (목표)

```
app/src/main/java/com/juyeon/androidpractice/
├── ui/
│   ├── theme/          # Color, Type, Theme (기본 생성됨)
│   ├── component/      # 재사용 컴포넌트 (GradientBackground, BottomNavigationBar 등)
│   ├── home/           # 홈 화면
│   ├── profile/        # 마이페이지 화면
│   ├── search/         # 탐색 화면
│   ├── write/          # 글쓰기 화면
│   ├── alarm/          # 알림/알람 화면
│   └── motion/         # 애니메이션 연습
├── repository/         # Repository (Real + Fake)
├── model/              # Data class (임시 모델)
└── MainActivity.kt
```

## 의존성 추가 가이드

각 주제 구현 시 `app/build.gradle.kts`에 추가가 필요한 라이브러리:

```kotlin
// MVVM + Coroutine
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.kotlinx.coroutines.android)

// Navigation (Screen 전환)
implementation(libs.androidx.navigation.compose)

// 이미지 로딩
implementation(libs.coil.compose)

// 알람 (AlarmManager는 SDK 기본 포함, 권한만 추가)
// AndroidManifest.xml에 추가:
// <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
// <uses-permission android:name="android.permission.VIBRATE" />
```

## 개발 규칙

- 각 연습 주제는 독립적인 패키지/폴더로 분리한다.
- ViewModel은 `AndroidViewModel` 대신 `ViewModel`을 기본으로 사용하고, Context가 필요한 경우에만 `AndroidViewModel`을 사용한다.
- Compose 화면 단위는 `Screen` suffix (예: `GradientScreen`), 재사용 컴포넌트는 자유롭게 명명한다.
- 상태는 ViewModel의 `StateFlow<UiState>`로 관리하고, Composable에서 `collectAsStateWithLifecycle()`로 수집한다.
- 코루틴은 `viewModelScope` 안에서만 실행한다.

## 실행 방법

Android Studio에서 `Run 'app'` 또는:

```
./gradlew installDebug
```
