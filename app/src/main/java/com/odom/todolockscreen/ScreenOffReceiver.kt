package com.odom.todolockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("TAG", "화면꺼짐")

//                // 화면꺼지면 locker 액티비티 실행
//                val intent = Intent(context, ToDoLockScreenActivity::class.java)
//
//                //새로운 액티비티로 실행
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                // 기존 액티비티 스택 제거
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//                // 액티비티 실행
//                context?.startActivity(intent)

            }

            Intent.ACTION_SCREEN_ON -> {
                Log.d("TAG", "화면켜짐")

                val lockIntent = Intent(context, ToDoLockScreenActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    // 사용자 액션으로 인한 화면 켜짐 방지
                    addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    // 애니메이션 제거
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }

                // 액티비티 실행
                context?.startActivity(lockIntent)


            }
        }

    }
}