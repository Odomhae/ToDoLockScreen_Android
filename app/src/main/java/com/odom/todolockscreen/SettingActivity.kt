package com.odom.todolockscreen

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    val fragment = MyPreferenceFragment()
    // 광고
    lateinit var mAdView : AdView
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density
            val adWidthPixels = outMetrics.widthPixels.toFloat()
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        window.statusBarColor = resources.getColor(R.color.colorGray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()

        closeImage.setOnClickListener {
            finish()
        }
    }

    class MyPreferenceFragment : PreferenceFragment(){

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // 환경설정 리소스 파일
            // xml 폴더의 pref 파일
            addPreferencesFromResource(R.xml.pref)

            // 글자색
            val textColorCategoryPref = findPreference("textColorCategory") as ListPreference
            textColorCategoryPref.summary = textColorCategoryPref.entries[PreferenceSettings(activity).textColor]
            textColorCategoryPref.setOnPreferenceChangeListener { preference, newValue ->

                val index = textColorCategoryPref.findIndexOfValue(newValue.toString())
                PreferenceSettings(activity).textColor = index
                textColorCategoryPref.summary = textColorCategoryPref.entries[index]

                true
            }

            // 각 리스트 색
            val listColorCategoryPref = findPreference("listColorCategory") as ListPreference
            listColorCategoryPref.summary = listColorCategoryPref.entries[PreferenceSettings(activity).listColor]
            listColorCategoryPref.setOnPreferenceChangeListener { preference, newValue ->

                val index = listColorCategoryPref.findIndexOfValue(newValue.toString())
                PreferenceSettings(activity).listColor = index
                listColorCategoryPref.summary = listColorCategoryPref.entries[index]

                true
            }

            // 배경색
            val backgroundColorCategoryPref = findPreference("backgroundColorCategory") as ListPreference
            backgroundColorCategoryPref.summary = backgroundColorCategoryPref.entries[PreferenceSettings(activity).backgroundColor]
            backgroundColorCategoryPref.setOnPreferenceChangeListener { preference, newValue ->

                val index = backgroundColorCategoryPref.findIndexOfValue(newValue.toString())
                PreferenceSettings(activity).backgroundColor = index
                backgroundColorCategoryPref.summary = backgroundColorCategoryPref.entries[index]

                true
            }


            // 잠금화면 사용 스위치 객체 사용
            // useLockScreen키로 찾음
            val useLockScreenPref = findPreference("useLockScreen") as SwitchPreference

            useLockScreenPref.setOnPreferenceClickListener {
                when{
                    // 퀴즈 잠금화면 사용이 체크된 경우 lockScreenService 실행
                    useLockScreenPref.isChecked ->{
                        Log.d("앱 사용여부", "체크됨")

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            activity.startForegroundService(Intent(activity, LockScreenService::class.java))
                        }else{
                            activity.startService(Intent(activity, LockScreenService::class.java))
                        }
                    }
                    // 사용 체크 안됬으면 서비스 중단
                    else -> activity.stopService(Intent(activity, LockScreenService::class.java))
                }

                true
            }

            // 앱이 시작됬을대 이미 퀴즈잠금화면 사용이 체크되어있으면 서비스 실행
            if(useLockScreenPref.isChecked){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    activity.startForegroundService(Intent(activity, LockScreenService::class.java))
                }else{
                    activity.startService(Intent(activity, LockScreenService::class.java))
                }

            }

        }

    }
}
