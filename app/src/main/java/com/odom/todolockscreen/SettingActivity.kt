package com.odom.todolockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.odom.todolockscreen.databinding.ActivitySettingBinding
import java.lang.String
import kotlin.Boolean
import kotlin.Exception


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

        private fun isReceiverEnabled(context: Context): Boolean {
            val preferences = context.getSharedPreferences("LOCK", MODE_PRIVATE)
            return preferences.getBoolean("isEnable", false)
        }

        fun enableReceiver(context: Context) {
            val preferences = context.getSharedPreferences("LOCK", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("isEnable", true)
            editor.apply()

            if (!isReceiverRegistered) {
                val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
                context.registerReceiver(receiver1, filter)
                isReceiverRegistered = true
            }
        }

        private fun disableReceiver(context: Context) {
            val preferences = context.getSharedPreferences("LOCK", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("isEnable", false)
            editor.apply()

            if (isReceiverRegistered) {
                try {
                    context.unregisterReceiver(receiver1)
                    isReceiverRegistered = false // Update the flag
                } catch (e: Exception) {
                    Log.d("====ttt error", e.message!!)
                }
            }
        }

    }

    private lateinit var binding: ActivitySettingBinding // 자동 생성된 바인딩 클래스


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contentView: View = this.findViewById(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(contentView, object : OnApplyWindowInsetsListener {
            override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
                val innerPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                v.setPadding(0, innerPadding.top, 0, innerPadding.bottom)

                return insets
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = false
            //   insetsController.isAppearanceLightNavigationBars = isLightStatusBars
        }

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        window.decorView.systemUiVisibility = 0

        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, SettingPreferencesFragment()).commit()

        binding.closeImage.setOnClickListener {
            finish()
        }
    }

    class SettingPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: kotlin.String?) {
            setPreferencesFromResource(R.xml.pref, rootKey)

            val switchPreference = findPreference<SwitchPreferenceCompat>("useLockScreen")
            val textColorPreference = findPreference<androidx.preference.ListPreference>("textColorCategory")
            val listColorPreference = findPreference<androidx.preference.ListPreference>("listColorCategory")
            val backGroundColorPreference = findPreference<androidx.preference.ListPreference>("backgroundColorCategory")

            if (switchPreference?.isChecked!!) {
                if(isReceiverEnabled(requireContext())) {
                    switchPreference.isChecked = true
                    enableReceiver(requireContext())
                }

            }
            textColorPreference?.summary = textColorPreference?.entries?.get(PreferenceSettings(requireContext()).textColor)
            listColorPreference?.summary = listColorPreference?.entries?.get(PreferenceSettings(requireContext()).listColor)
            backGroundColorPreference?.summary = backGroundColorPreference?.entries?.get(PreferenceSettings(requireContext()).backgroundColor)

            // 사용여부
            switchPreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    enableReceiver(requireContext())
                } else {
                    disableReceiver(requireContext())
                }
                true
            }

            // 글자색
            textColorPreference?.setOnPreferenceChangeListener { _, newValue ->
                textColorPreference.summary = newValue.toString()

                val index = textColorPreference.findIndexOfValue(newValue.toString())
                PreferenceSettings(requireContext()).textColor = index

                true
            }

            // 각 리스트 색
            listColorPreference?.setOnPreferenceChangeListener { _, newValue ->
                listColorPreference.summary = newValue.toString()

                val index = listColorPreference.findIndexOfValue(newValue.toString())
                PreferenceSettings(requireContext()).listColor = index

                true
            }

            // 배경색
            backGroundColorPreference?.setOnPreferenceChangeListener { _, newValue ->
                backGroundColorPreference.summary = newValue.toString()

                val index = backGroundColorPreference.findIndexOfValue(newValue.toString())
                PreferenceSettings(requireContext()).backgroundColor = index

                true
            }

        }

    }

}
