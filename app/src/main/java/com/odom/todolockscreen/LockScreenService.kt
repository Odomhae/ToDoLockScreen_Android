package com.odom.todolockscreen

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi


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


    @RequiresApi(Build.VERSION_CODES.S)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(ANDROID_CHANNEL_ID, "MyService", NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val intentToMain = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intentToMain, PendingIntent.FLAG_IMMUTABLE)
            val intentToSetting = Intent(this, SettingActivity::class.java)
            val pendingIntent2 = PendingIntent.getActivity(this, 1, intentToSetting, PendingIntent.FLAG_IMMUTABLE)

            val notification = Notification.Builder(this, ANDROID_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_view, resources.getString(R.string.view_app), pendingIntent)
                .addAction(android.R.drawable.ic_menu_view, resources.getString(R.string.setting_app), pendingIntent2)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
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