package com.odom.todolockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.odom.todolockscreen.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {

    companion object {
        private var isReceiverRegistered = false

        private val receiver1: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Intent.ACTION_SCREEN_ON == intent.action) {
                    if (isReceiverEnabled(context)) {
                        // Start the lock screen activity
                        val lockIntent = Intent(context, ToDoLockScreenActivity::class.java)
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(lockIntent)
                    }
                }
            }
        }

        fun isReceiverEnabled(context: Context): Boolean {
            val preferences = context.getSharedPreferences("LOCK", MODE_PRIVATE)
            return preferences.getBoolean("isEnable", false)
        }

        fun enableReceiver(context: Context) {
            val preferences = context.getSharedPreferences("LOCK", MODE_PRIVATE)
            preferences.edit().putBoolean("isEnable", true).apply()

            if (!isReceiverRegistered) {
                val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
                context.registerReceiver(receiver1, filter)
                isReceiverRegistered = true
            }

            val serviceIntent = Intent(context, LockScreenService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }

        private fun disableReceiver(context: Context) {
            val preferences = context.getSharedPreferences("LOCK", MODE_PRIVATE)
            preferences.edit().putBoolean("isEnable", false).apply()

            if (isReceiverRegistered) {
                try {
                    context.unregisterReceiver(receiver1)
                    isReceiverRegistered = false
                } catch (e: Exception) {
                    Log.d("====ttt error", e.message!!)
                }
            }

            context.stopService(Intent(context, LockScreenService::class.java))
        }

    }

    private lateinit var binding: ActivitySettingBinding // 자동 생성된 바인딩 클래스


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contentView: View = this.findViewById(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(contentView) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.statusBarScrim.layoutParams = binding.statusBarScrim.layoutParams.also { it.height = bars.top }
            v.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.colorPrimaryDark)

        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, SettingPreferencesFragment()).commit()

        binding.closeImage.setOnClickListener {
            finish()
        }
    }

    class SettingPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: kotlin.String?) {
            setPreferencesFromResource(R.xml.pref, rootKey)

            val switchPreference = findPreference<SwitchPreferenceCompat>("useLockScreen")

            if (switchPreference?.isChecked == true && isReceiverEnabled(requireContext())) {
                enableReceiver(requireContext())
            }

            switchPreference?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) enableReceiver(requireContext())
                else disableReceiver(requireContext())
                true
            }
        }
    }

}
