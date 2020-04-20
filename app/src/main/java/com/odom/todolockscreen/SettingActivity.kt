package com.odom.todolockscreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.util.Log
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    val fragment = MyPreferenceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

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
