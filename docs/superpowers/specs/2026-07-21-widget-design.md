# 홈화면 위젯 설계 문서

**날짜:** 2026-07-21  
**대상:** ToDoLockScreen Android 앱 홈화면 위젯  
**방식:** 표준 AppWidget + RemoteViewsService (스크롤 가능한 리스트형)

---

## 1. 아키텍처

```
TodoWidgetProvider  →  AppWidgetManager.updateAppWidget()
  (AppWidgetProvider)
        │
        └─ RemoteViews (widget_todo.xml)
              └─ ListView (widgetListView)
                    └─ TodoWidgetService (RemoteViewsService)
                          └─ TodoWidgetFactory (RemoteViewsFactory)
                                └─ PreferenceSettings → 할일 데이터 + 색상
```

### 컴포넌트 역할

| 컴포넌트 | 역할 |
|---|---|
| `TodoWidgetProvider` | 위젯 생명주기 (`onUpdate`, `onEnabled`, `onDisabled`) |
| `TodoWidgetService` | `TodoWidgetFactory` 인스턴스 생성 |
| `TodoWidgetFactory` | 리스트 어댑터 역할, PreferenceSettings에서 데이터·색상 읽어 RemoteViews 반환 |

---

## 2. 위젯 레이아웃 & 외관

### 기본 크기
- 4×4 셀 (minWidth 250dp, minHeight 250dp)
- 사용자가 홈화면에서 크기 조절 가능 (`resizeMode="horizontal|vertical"`)

### `widget_todo.xml` (위젯 루트)
```
LinearLayout (vertical)
  ├─ LinearLayout (header, 48dp)
  │    ├─ TextView "Stuffing list" (오렌지 배경, 흰 텍스트)
  │    └─ → 탭 시 MainActivity 오픈
  └─ ListView (id: widgetListView)
       └─ 스크롤 가능한 할일 목록
```
- 루트 배경: `PreferenceSettings.backgroundColor` 색상 적용
- 헤더: `colorPrimary` (#f37021) 배경, 흰 텍스트

### `widget_todo_item.xml` (각 항목 행)
```
LinearLayout (horizontal, height 48dp)
  ├─ View (4dp 폭, 오렌지 세로선)
  └─ TextView (할일 텍스트, paddingStart 12dp)
```
- 배경색: `PreferenceSettings.listColor` 적용
- 텍스트색: `PreferenceSettings.textColor` 적용

**RemoteViews 제약**: `MaterialCardView` 미지원 → 색상은 `setInt(viewId, "setBackgroundColor", color)`로 직접 적용

---

## 3. 데이터 갱신 메커니즘

### 갱신 시점 (주기적 갱신 없음)

| 이벤트 | 갱신 방법 |
|---|---|
| 위젯 홈화면 추가 | `onUpdate()` 자동 호출 |
| 항목 추가 (`addList()`) | `notifyAppWidgetViewDataChanged()` 호출 |
| 항목 수정 (`showBox()` 저장) | `notifyAppWidgetViewDataChanged()` 호출 |
| 항목 삭제 (`showBox()` 삭제) | `notifyAppWidgetViewDataChanged()` 호출 |
| 색상 변경 (`ColorPickerPreference`) | `notifyAppWidgetViewDataChanged()` 호출 |

### 갱신 흐름
```
사용자: 위젯 탭 → MainActivity 열림
사용자: 항목 추가/수정/삭제
       └─ notifyAppWidgetViewDataChanged() 즉시 호출
사용자: 홈으로 돌아옴 → 위젯이 이미 최신 상태
```

### 헬퍼 함수 (MainActivity, ColorPickerPreference 공통 사용)
```kotlin
fun notifyWidget(context: Context) {
    val manager = AppWidgetManager.getInstance(context)
    val ids = manager.getAppWidgetIds(
        ComponentName(context, TodoWidgetProvider::class.java)
    )
    manager.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
}
```

---

## 4. 항목 탭 인터랙션

- **헤더 탭**: `MainActivity` 오픈
- **항목 탭**: `MainActivity` 오픈 (`setPendingIntentTemplate` + `setOnClickFillInIntent` 패턴)

---

## 5. AndroidManifest.xml 추가 사항

```xml
<!-- AppWidgetProvider -->
<receiver android:name=".TodoWidgetProvider"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/todo_widget_info" />
</receiver>

<!-- RemoteViewsService -->
<service android:name=".TodoWidgetService"
    android:permission="android.permission.BIND_REMOTEVIEWS"
    android:exported="false" />
```

---

## 6. 신규/변경 파일 목록

| 파일 | 종류 | 내용 |
|---|---|---|
| `TodoWidgetProvider.kt` | 신규 | AppWidgetProvider |
| `TodoWidgetService.kt` | 신규 | RemoteViewsService |
| `TodoWidgetFactory.kt` | 신규 | RemoteViewsFactory |
| `res/layout/widget_todo.xml` | 신규 | 위젯 루트 레이아웃 |
| `res/layout/widget_todo_item.xml` | 신규 | 항목 행 레이아웃 |
| `res/xml/todo_widget_info.xml` | 신규 | AppWidgetProviderInfo |
| `AndroidManifest.xml` | 수정 | receiver + service 등록 |
| `MainActivity.kt` | 수정 | addList/showBox에 notifyWidget() 추가 |
| `ColorPickerPreference.kt` | 수정 | saveIndex()에 notifyWidget() 추가 |

---

## 7. 의존성

추가 의존성 없음. 모두 Android SDK 기본 API 사용.
