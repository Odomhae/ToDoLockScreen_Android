# UI 리디자인 설계 문서

**날짜:** 2026-07-21  
**대상:** ToDoLockScreen Android 앱 전체 UI  
**방향:** Material Design 3 (MaterialComponents) + 오렌지 포인트 컬러 유지

---

## 1. 테마 & 색상

### 변경 사항
- 기존: `Theme.AppCompat.Light.DarkActionBar`
- 변경: `Theme.MaterialComponents.Light.NoActionBar`

### 색상 팔레트

| 이름 | 값 | 용도 |
|---|---|---|
| `colorPrimary` | `#f37021` | 버튼, 입력창 활성 테두리, 스위치, FAB |
| `colorPrimaryDark` | `#c85a15` | 상태바 배경 |
| `colorOnPrimary` | `#ffffff` | 오렌지 배경 위 텍스트/아이콘 |
| `colorSurface` | `#FFFBF8` | 카드, 다이얼로그 배경 |
| `colorBackground` | `#FFFBF8` | 앱 전체 배경 |
| `colorAccent` | `#f37021` | 기존 Accent 유지 |

### 적용 파일
- `app/src/main/res/values/colors.xml` — 위 색상 추가
- `app/src/main/res/values/styles.xml` — 테마 교체, `colorPrimary` / `colorPrimaryDark` / `colorSurface` 적용

---

## 2. 메인 화면 (MainActivity / activity_main.xml)

### 레이아웃 구조 (유지)
```
ConstraintLayout
  └─ LinearLayout (광고 배너) ← 변경 없음
  └─ LinearLayout (입력 행)
       ├─ TextInputLayout > TextInputEditText   ← EditText 교체
       ├─ MaterialButton (추가)                 ← ImageView 교체
       └─ MaterialButton icon-only (설정)       ← ImageView 교체
  └─ SwipeRefreshLayout
       └─ RecyclerView                          ← ListView 교체
```

### 세부 스펙

**입력창 (`TextInputLayout`)**
- style: `Widget.MaterialComponents.TextInputLayout.OutlinedBox`
- 모서리: 8dp
- hint: 기존 `@string/enter_here` 유지
- 활성 테두리색: `colorPrimary` (오렌지) — 자동 적용

**추가 버튼 (`MaterialButton`)**
- icon: `@drawable/ic_add_black_24dp` (tint white)
- backgroundTint: `@color/colorPrimary`
- 모서리: 8dp
- 크기: 48dp × 48dp

**설정 버튼 (`MaterialButton` icon-only)**
- style: `Widget.MaterialComponents.Button.OutlinedButton.Icon`
- icon: `@drawable/ic_settings_black_24dp`
- strokeColor: transparent (테두리 없음)
- 리플 효과 자동 적용

**RecyclerView**
- 기존 ListView 대체
- adapter: 기존 `ArrayAdapter` → 새 `TodoAdapter` (ViewHolder 패턴, `item_todo.xml` 사용)
- SwipeRefreshLayout 색상: `colorPrimary`

---

## 3. 할일 카드 아이템 (새 파일: item_todo.xml)

```
MaterialCardView (elevation 3dp, cornerRadius 12dp, cardBackgroundColor #FFFBF8)
  └─ LinearLayout (horizontal)
       ├─ View (width 4dp, height match_parent, background colorPrimary)  ← 왼쪽 오렌지 강조선
       └─ TextView (18sp, paddingStart 16dp, paddingTop/Bottom 14dp)
```

### 세부 스펙
- 카드 간 간격: `RecyclerView` item decoration으로 8dp
- 클릭 리플: `MaterialCardView`의 `foreground` 자동 리플
- 배경색: `#FFFBF8` (웜화이트)
- 왼쪽 강조선: `#f37021` (오렌지), 4dp 폭
- 텍스트 색: `#212121` (진한 회색)
- 텍스트 크기: 18sp

### Adapter 변경
- 기존 `ArrayAdapter<String>` → 새 `TodoAdapter : RecyclerView.Adapter`
- ViewHolder: `item_todo.xml` inflate
- 클릭 리스너: 기존 `showBox()` 동일 로직 유지
- MainActivity에서 `binding.listView` → `binding.recyclerView`로 교체

---

## 4. 설정 화면 (SettingActivity / activity_setting.xml)

### 헤더 변경
- 배경: `#f37021` (오렌지)
- 텍스트 "앱 설정": 흰색, 24sp
- 닫기 아이콘: 흰색 tint (`android:tint="@color/colorWhite"`)
- 상단 여백(marginTop) 제거 → 화면 꽉 차게

### PreferenceFragment 배경
- 배경: `#FFFBF8` (웜화이트)

---

## 5. 다이얼로그 (input_box.xml / ask_box.xml)

### 스타일
- `AlertDialog.Builder` → `MaterialAlertDialogBuilder` 교체 (MainActivity.kt)
- 다이얼로그 자체: 자동으로 둥근 모서리, 웜화이트 배경

### input_box.xml
- `EditText` → `TextInputLayout > TextInputEditText` (OutlinedBox, 오렌지 활성 테두리)
- **저장 버튼**: `MaterialButton`, backgroundTint 오렌지
- **삭제 버튼**: `MaterialButton` OutlinedButton, strokeColor 오렌지, 텍스트 오렌지

### ask_box.xml
- 메시지 텍스트: 색상 `#212121` (기존 빨강에서 변경, 덜 공격적)
- **취소 버튼**: OutlinedButton (오렌지 테두리)
- **삭제 버튼**: Filled MaterialButton (오렌지 배경)

---

## 6. 잠금화면 (ToDoLockScreenActivity / activity_to_do_locksceen.xml)

- 기존 RecyclerView + 색상 선택 기능 **그대로 유지**
- 변경 없음 (사용자가 색상을 직접 선택하는 기능이 핵심이므로)

---

## 변경 파일 목록

| 파일 | 변경 내용 |
|---|---|
| `values/colors.xml` | colorPrimaryDark, colorSurface, colorBackground 추가 |
| `values/styles.xml` | MaterialComponents 테마로 교체 |
| `layout/activity_main.xml` | TextInputLayout, MaterialButton, RecyclerView 적용 |
| `layout/item_todo.xml` | 신규 생성 (MaterialCardView 할일 카드) |
| `layout/activity_setting.xml` | 헤더 오렌지 배경 적용 |
| `layout/input_box.xml` | TextInputLayout, MaterialButton 적용 |
| `layout/ask_box.xml` | MaterialButton 적용 |
| `MainActivity.kt` | ListView→RecyclerView, TodoAdapter, MaterialAlertDialogBuilder |
| `build.gradle (app)` | `com.google.android.material:material` 의존성 확인 |

---

## 의존성

현재 `app/build.gradle`에 Material 라이브러리가 **없으므로 반드시 추가** 필요:

```groovy
// build.gradle (app) dependencies 블록에 추가
implementation 'com.google.android.material:material:1.12.0'
```

추가 후 `appcompat` 버전도 Material3와 호환되는 버전으로 올리는 것을 권장:
```groovy
implementation 'androidx.appcompat:appcompat:1.7.0'  // 기존 1.2.0 → 1.7.0
```
