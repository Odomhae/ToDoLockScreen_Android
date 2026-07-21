# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## App Overview

**Stuffing List (할꺼)** — An Android to-do app that displays tasks on the lock screen. Published on Google Play (`com.odom.todolockscreen`). minSdk 23, targetSdk 35, Kotlin, View-based UI (no Compose).

## Build Commands

All builds are done through Android Studio or Gradle. There is no standalone CLI test runner — the project uses default AndroidJUnit4 stubs with no meaningful tests written.

```bash
# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK
./gradlew assembleRelease

# Run lint
./gradlew lint

# Clean
./gradlew clean
```

## Architecture

Single-module app with no ViewModel/LiveData. All state is read from/written to `PreferenceSettings` (SharedPreferences key: `"SETTINGS"`) directly from Activities and custom views.

### Core Components

| File | Role |
|---|---|
| `MainActivity` | Main to-do list screen. RecyclerView via `TodoMainAdapter`. Handles add/edit/delete and triggers widget updates. |
| `ToDoLockScreenActivity` | Lock screen overlay shown on `ACTION_SCREEN_ON`. RecyclerView via `RecyclerViewAdapter` (lock-screen adapter). Supports swipe-to-delete and drag-to-reorder. |
| `SettingActivity` | Settings screen. Uses `PreferenceFragmentCompat` with `pref.xml`. Contains inner `SettingPreferencesFragment`. |
| `LockScreenService` | Foreground service that keeps the app alive. |
| `ScreenOffReceiver` | BroadcastReceiver for screen-off events. |
| `PreferenceSettings` | Single source of truth for all persisted data: `listData` (JSON array of strings), `textColor`, `listColor`, `backgroundColor`, `colorChangeCount`. |

### Widget System

| File | Role |
|---|---|
| `TodoWidgetProvider` | `AppWidgetProvider`. Call `TodoWidgetProvider.notifyWidget(context)` whenever data or colors change — this must be done manually after every mutation in `MainActivity` and `ColorPickerPreference`. |
| `TodoWidgetService` | `RemoteViewsService` that creates `TodoWidgetFactory` instances. |
| `TodoWidgetFactory` | Reads data and colors from `PreferenceSettings` in `onDataSetChanged()`. |

### Color System

Colors are stored as integer indices in `PreferenceSettings`, not as color values directly.

- `textColor` and `backgroundColor` index into `ColorPickerPreference.COLOR_CATEGORY` (White=0, Gray=1, Black=2, …)
- `listColor` indexes into `ColorPickerPreference.LIST_COLOR_CATEGORY` (Black=0, Gray=1, White=2, …)

`ColorPickerPreference` is a custom `Preference` subclass replacing `ListPreference`. It shows a color grid dialog and renders the selected color as a dot widget on the preference row. It also tracks `colorChangeCount` and shows an interstitial ad every 5 changes.

When the `RecyclerViewAdapter` (lock screen) or `TodoWidgetFactory` needs a color value, it uses these arrays directly. The `ToDoLockScreenActivity` uses a manual `when` switch over the index for background/status bar colors.

## Key Data Flow

```
User action in MainActivity
  → PreferenceSettings.listData = ...
  → todoAdapter.notifyItem...()
  → TodoWidgetProvider.notifyWidget(context)   ← must always accompany data changes
```

Color changes in SettingActivity:
```
ColorPickerPreference.saveIndex()
  → PreferenceSettings.textColor / listColor / backgroundColor
  → TodoWidgetProvider.notifyWidget(context)
  → InterstitialAd every 5 changes (TEST_fullscreen_ad_unit_id)
```

## AdMob

- Banner in `MainActivity` (top of screen) and in the exit confirmation dialog (preloaded as `exitAdView` in `onCreate`).
- Interstitial triggered from `ColorPickerPreference` every 5 color changes.
- String resources: `TEST_banner_ad_unit_id`, `TEST_fullscreen_ad_unit_id`, `REAL_banner_ad_unit_id`, `REAL_admob_app_id`.
- Currently `AndroidManifest.xml` uses `TEST_admob_app_id` and layouts reference `TEST_banner_ad_unit_id`.

## Theme & Styling

- Theme: `Theme.MaterialComponents.Light.NoActionBar`
- Primary color: `#f37021` (orange), `colorPrimaryDark`: `#c85a15`
- Surface/background: `#FFFBF8` (warm white)
- Dialog buttons use `MaterialAlertDialogBuilder` throughout.
- Widget layouts (`widget_todo.xml`, `widget_todo_item.xml`) must use only `RemoteViews`-compatible views — no `MaterialCardView`.

## Lock Screen Mechanism

`ToDoLockScreenActivity` is launched by `SettingActivity`'s companion `BroadcastReceiver` on `ACTION_SCREEN_ON`. The receiver is registered/unregistered dynamically (not in manifest) based on the `useLockScreen` preference. The activity uses `setShowWhenLocked(true)` / `FLAG_SHOW_WHEN_LOCKED` to appear over the keyguard.
