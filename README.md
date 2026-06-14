# AndroidPractice

Kotlin + Jetpack Compose로 만든 안드로이드 SNS 연습 앱입니다.
백엔드 없이 RoomDB만으로 소셜 기능을 구현했습니다.

---

## 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| Language | Kotlin 2.1.21 |
| UI | Jetpack Compose |
| Architecture | MVVM (ViewModel + StateFlow + Repository) |
| DB | Room 2.8.1 (KSP2) |
| Image | Coil |
| Async | Coroutines + Flow |
| Build | AGP 9.1.1 |

---

## 주요 기능

### 인증
- 회원가입 / 로그인
- 자동 로그인 (SharedPreferences로 유저 ID 저장)
- 로그아웃

### 게시글
- 작성 (텍스트 + 이미지 첨부)
- 수정 / 삭제
- 좋아요 / 스크랩

### 댓글
- 댓글 · 대댓글 작성 / 삭제
- 댓글 좋아요

### 소셜
- 팔로우 / 언팔로우
- 팔로워 · 팔로잉 목록 확인
- 홈 팔로잉 피드 (팔로우한 유저의 게시글만 표시)

### 검색
- 게시글 검색 (제목 · 본문)
- 유저 검색 (닉네임)
- 유저 프로필 페이지로 이동

### 알림
- 좋아요 · 댓글 · 팔로우 시 알림 생성
- 앱 내 알림 목록 + Android 시스템 알림
- 읽음 / 안 읽음 상태 표시
- 알림 클릭 → 해당 게시글로 이동

### 프로필
- 프로필 이미지 변경 (갤러리)
- 닉네임 변경
- 내가 쓴 글 / 댓글 / 스크랩 / 좋아요 탭

---

## 화면 구성

```
로그인 / 회원가입
    └─ 메인 (BottomNav)
        ├─ 홈       — 팔로잉 피드
        ├─ 검색     — 게시글 탭 / 유저 탭
        ├─ 글쓰기   — 작성 / 수정
        ├─ 알림     — 알림 목록 + 알람 설정
        └─ 프로필   — 내 정보 + 탭 목록
```

---

## 프로젝트 구조

```
app/src/main/java/com/juyeon/androidpractice/
├── data/
│   ├── db/entity/       # Room Entity
│   ├── db/dao/          # DAO 인터페이스
│   ├── db/AppDatabase.kt
│   ├── notification/    # 시스템 알림 헬퍼
│   └── repository/      # Repository 인터페이스 + 구현체
├── model/               # UI 모델
├── ui/                  # Screen + ViewModel (기능별 패키지)
└── MainActivity.kt
```

---

## 실행 방법

1. Android Studio에서 프로젝트 열기
2. 에뮬레이터 또는 실제 기기 연결
3. `Run 'app'` 실행

```bash
./gradlew installDebug
```

---

## 향후 계획

- FastAPI 백엔드 연동 (Repository 계층만 교체)
- 실제 이미지 업로드 서버 연동
