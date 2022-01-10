package com.odom.todolockscreen

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class PreferenceSettings(context: Context) {

    private val prefs : SharedPreferences by lazy {
        context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
    }

    // 할일 배열
    var listData : ArrayList<String>

        // string -> JSONArray -> ArrayList
        get() {
            val listString = prefs.getString("listData", null)
            val stuffList = ArrayList<String>()

            try {
                val jsonArray =  JSONArray(listString)
                for (i in 0 until jsonArray.length()) {
                    val stuff = jsonArray.optString(i) // getString
                    stuffList.add(stuff)
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

            return stuffList
        }

        set(value) = prefs.edit().putString("listData", value.toString()).apply()


    // 글자색
    var textColor : Int
        get() = prefs.getInt("textColor", 0)
        set(value) = prefs.edit().putInt("textColor", value).apply()

    // 각 홀더 배경색
    var listColor : Int
        get() = prefs.getInt("listColor", 0)
        set(value) = prefs.edit().putInt("listColor", value).apply()

    // 전체 배경색
    var backgroundColor : Int
        get() = prefs.getInt("backgroundColor", 0)
        set(value) = prefs.edit().putInt("backgroundColor", value).apply()

}