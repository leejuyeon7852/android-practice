# AndroidPractice

안드로이드 개발 연습 프로젝트 — SNS 형태의 앱을 RoomDB 기반으로 구현.

## 프로젝트 개요

- **패키지**: `com.juyeon.androidpractice`
- **언어**: Kotlin
- **UI**: Jetpack Compose
- **minSdk**: 24 / **compileSdk**: 37 / **targetSdk**: 36
- **DB**: Room 2.8.1 (KSP2)
- **빌드**: AGP 9.1.1 / Kotlin 2.1.21

## 구현된 기능

- 회원가입 / 로그인 / 자동 로그인 (SharedPreferences)
- 게시글 작성 · 수정 · 삭제 (이미지 첨부 포함)
- 댓글 · 대댓글 작성 · 삭제 / 좋아요 / 스크랩
- 팔로우 / 언팔로우
- 알림 (좋아요 · 댓글 · 팔로우) — RoomDB + Android 시스템 알림
- 홈 팔로잉 피드
- 게시글 / 유저 검색
- 프로필 이미지 · 닉네임 수정
- 유저 프로필 페이지 (타인)
- 알림 클릭 → 해당 게시글 이동

## 폴더 구조

```
app/src/main/java/com/juyeon/androidpractice/
├── data/
│   ├── db/
│   │   ├── dao/        # PostDao, UserDao, CommentDao, LikeDao, ScrapDao,
│   │   │               # CommentLikeDao, AppNotificationDao, FollowDao
│   │   ├── entity/     # Post, User, Comment, Like, Scrap, CommentLike,
│   │   │               # AppNotification, Follow
│   │   └── AppDatabase.kt   # version = 5, fallbackToDestructiveMigration
│   ├── notification/   # NotificationHelper (시스템 알림 채널)
│   └── repository/     # PostRepository, AuthRepository (interface + impl)
├── model/              # NotificationItem, BannerItem, CardItem
├── ui/
│   ├── theme/          # Color, Type, Theme, GradientBackground
│   ├── component/      # BottomNavigationBar (BadgedBox 뱃지)
│   ├── auth/           # LoginScreen, SignupScreen, AuthViewModel
│   ├── home/           # HomeScreen (팔로잉 피드), HomeViewModel
│   ├── post/           # PostDetailScreen, PostDetailViewModel
│   ├── profile/        # ProfileScreen, ProfileViewModel,
│   │                   # UserProfileScreen, UserProfileViewModel
│   ├── search/         # SearchScreen (게시글/유저 탭), SearchViewModel
│   ├── write/          # WriteScreen (작성+수정), WriteViewModel
│   └── alarm/          # AlarmScreen, AlarmViewModel
└── MainActivity.kt     # 단일 Activity, 상태 기반 라우팅 (NavHost 없음)
```

## 아키텍처 규칙

- **라우팅**: NavHost 없이 `selectedPostId`, `editPost`, `selectedUserId` 등 `remember` 상태로 화면 전환
- **ViewModel**: Context 필요 시 `AndroidViewModel`, 아닐 시 `ViewModel`
- **상태**: `StateFlow` + `collectAsStateWithLifecycle()` 또는 `mutableStateOf`
- **코루틴**: `viewModelScope` 안에서만 실행
- **이미지 URI**: 갤러리 선택 시 `takePersistableUriPermission` (try-catch 포함)
- **DB 스키마 변경**: 개발 중 `fallbackToDestructiveMigration()` 사용

## DB 주의사항

- `AppDatabase.version`을 올릴 때마다 에뮬레이터 앱 데이터 초기화됨 (destructive migration)
- 나중에 FastAPI 백엔드로 교체 예정 — Repository 계층만 바꾸면 됨

## 실행 방법

```
./gradlew installDebug
```
