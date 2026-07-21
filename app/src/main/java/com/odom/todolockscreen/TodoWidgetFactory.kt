package com.odom.todolockscreen

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class TodoWidgetFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private var items = ArrayList<String>()
    private var textColor = 0xFFFFFFFF.toInt()
    private var itemBgColor = 0xFF000000.toInt()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    private fun loadData() {
        val pref = PreferenceSettings(context)
        items = pref.listData
        textColor = ColorPickerPreference.COLOR_CATEGORY[pref.textColor]
        itemBgColor = ColorPickerPreference.LIST_COLOR_CATEGORY[pref.listColor]
    }

    override fun onDestroy() {}

    override fun getCount() = items.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= items.size) return RemoteViews(context.packageName, R.layout.widget_todo_item)

        val rv = RemoteViews(context.packageName, R.layout.widget_todo_item)
        rv.setTextViewText(R.id.widgetItemText, items[position])
        rv.setTextColor(R.id.widgetItemText, textColor)
        rv.setInt(R.id.widgetItemRoot, "setBackgroundColor", itemBgColor)

        rv.setOnClickFillInIntent(R.id.widgetItemRoot, Intent())

        return rv
    }

    override fun getLoadingView() = null
    override fun getViewTypeCount() = 1
    override fun getItemId(position: Int) = position.toLong()
    override fun hasStableIds() = true
}
