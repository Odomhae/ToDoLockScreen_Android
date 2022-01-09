package com.odom.todolockscreen

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder


class LockScreenService :Service(){

    // 화면꺼질때 브로드케스트 msg 수신하는 리시버
    var receiver : ScreenOffReceiver? = null

    private val ANDROID_CHANNEL_ID = "com.odom.todolockscreen"
    private val NOTIFICATION_ID = 9999

    override fun onCreate() {
        super.onCreate()

        // null인 경우만 실행
        if(receiver == null){
            receiver = ScreenOffReceiver()
            val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
            registerReceiver(receiver, filter)
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(intent != null){
            if(intent.action == null){
                // 서비스가 최초 실행이 아닌경우
                // 리시버 null이면 새로 생성하고 등록함
                if(receiver == null){
                    receiver = ScreenOffReceiver()
                    val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                    registerReceiver(receiver, filter)
                }
            }
        }

        // 안드로이드 오레오 버전부터 백그라운드 제약이 잇어 포그라운드 서비스 실행
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Notification(상단알림) 채널 설정
            val chan = NotificationChannel(ANDROID_CHANNEL_ID, "MyService", NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            // Notification 서비스 객체 가져옴
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            // Notification 알림 객체 설정
            val builder = Notification.Builder(this, ANDROID_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)

            // 클릭시 메인 엑티비티로 이동
            val intentToMain = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intentToMain, 0)
            // 클릭시 설정 엑티비티로 이동
            val intentToSetting = Intent(this, SettingActivity::class.java)
            val pendingIntent2 = PendingIntent.getActivity(this, 0, intentToSetting, 0)

            builder.setContentIntent(pendingIntent)
            builder.addAction(android.R.drawable.ic_menu_view, resources.getString(R.string.view_app), pendingIntent)
            builder.addAction(android.R.drawable.ic_menu_view, resources.getString(R.string.setting_app), pendingIntent2)

            val notification = builder.build()

            // Notification 알림과 함께 포그라운드 서비스 시작
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_REDELIVER_INTENT
    }


    override fun onDestroy() {
        super.onDestroy()
        if(receiver !=null)
            unregisterReceiver(receiver)

    }

    // 이놈 필수
    override fun onBind(intent: Intent?): IBinder? {
        return null

    }

}