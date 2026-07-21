package com.odom.todolockscreen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class TodoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_todo)

            // 배경색 적용
            val bgColor = ColorPickerPreference.COLOR_CATEGORY[PreferenceSettings(context).backgroundColor]
            views.setInt(R.id.widgetRoot, "setBackgroundColor", bgColor)

            // ListView에 RemoteViewsService 연결
            val serviceIntent = Intent(context, TodoWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widgetListView, serviceIntent)
            views.setEmptyView(R.id.widgetListView, R.id.widgetEmptyText)

            // 어디를 눌러도 MainActivity 열기
            val mainIntent = Intent(context, MainActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetRoot, mainPendingIntent)
            views.setOnClickPendingIntent(R.id.widgetHeader, mainPendingIntent)
            views.setPendingIntentTemplate(R.id.widgetListView, mainPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun notifyWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TodoWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                for (id in ids) updateWidget(context, manager, id)
                manager.notifyAppWidgetViewDataChanged(ids, R.id.widgetListView)
            }
        }
    }
}
