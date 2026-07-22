package com.odom.todolockscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.odom.todolockscreen.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val items = ArrayList<String>()

    private lateinit var todoAdapter: TodoMainAdapter
    lateinit var mAdView: AdView
    private var exitAdView: AdView? = null

    private val adSize: AdSize
        get() {
            val adWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bounds = windowManager.currentWindowMetrics.bounds
                (bounds.width() / resources.displayMetrics.density).toInt()
            } else {
                @Suppress("DEPRECATION")
                val outMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getMetrics(outMetrics)
                (outMetrics.widthPixels / outMetrics.density).toInt()
            }
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (Settings.canDrawOverlays(this)) {
            Log.d("TAG", "권한 설정됨")
            val toast = Toast.makeText(applicationContext, R.string.permission_set_message, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, Gravity.CENTER, 550)
            toast.show()
            onResume()
        } else {
            Log.d("TAG", "권한 거절됨")
            finish()
            val toast = Toast.makeText(applicationContext, R.string.permission_denied_message, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, Gravity.CENTER, 550)
            toast.show()
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val contentView: View = this.findViewById(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(contentView) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.statusBarScrim.layoutParams = binding.statusBarScrim.layoutParams.also { it.height = bars.top }
            v.setPadding(0, 0, 0, bars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { showExitDialog() }
        })

        checkPermission()

        binding.settingButton.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        // RecyclerView 설정
        todoAdapter = TodoMainAdapter(items) { position ->
            showBox(items, position)
            PreferenceSettings(this).listData = items
        }
        binding.listView.layoutManager = LinearLayoutManager(this)
        binding.listView.adapter = todoAdapter

        // 이전 목록 복원
        val listPref = PreferenceSettings(this).listData
        if (listPref.size > 0) {
            for (value in listPref) items.add(value)
            todoAdapter.notifyDataSetChanged()
        }

        // 당겨서 새로고침
        binding.pullToRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.pullToRefresh.setOnRefreshListener {
            items.clear()
            val listPref2 = PreferenceSettings(this).listData
            if (listPref2.size > 0) {
                for (value in listPref2) items.add(value)
            }
            todoAdapter.notifyDataSetChanged()
            binding.pullToRefresh.isRefreshing = false
        }

        binding.editText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addList()
                return@OnEditorActionListener true
            }
            false
        })

        binding.addListButton.setOnClickListener { addList() }

        // 배너 광고 초기화
        MobileAds.initialize(this) {}
        mAdView = AdView(this)
        binding.adMobView.addView(mAdView)
        loadBanner()

        // 종료 다이얼로그용 배너 미리 로드
        preloadExitAd()
    }

    private fun loadBanner() {
        mAdView.adUnitId = resources.getString(R.string.TEST_banner_ad_unit_id)
        mAdView.setAdSize(adSize)
        mAdView.loadAd(AdRequest.Builder().build())
    }

    private fun preloadExitAd() {
        exitAdView = AdView(this).apply {
            adUnitId = resources.getString(R.string.TEST_banner_ad_unit_id)
            setAdSize(AdSize.MEDIUM_RECTANGLE)
            loadAd(AdRequest.Builder().build())
        }
    }

    private fun showExitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_exit, null)
        val adContainer = dialogView.findViewById<FrameLayout>(R.id.exitDialogAdContainer)

        // 미리 로드된 배너를 다이얼로그에 삽입
        exitAdView?.let { adView ->
            (adView.parent as? ViewGroup)?.removeView(adView)
            adContainer.addView(adView)
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()
        dialog.show()

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.exitDialogCancel)
            .setOnClickListener { dialog.dismiss() }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.exitDialogConfirm)
            .setOnClickListener {
                dialog.dismiss()
                finish()
            }
    }

    override fun onResume() {
        super.onResume()
        if (SettingActivity.isReceiverEnabled(this)) {
            SettingActivity.enableReceiver(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitAdView?.destroy()
        mAdView.destroy()
    }

    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            overlayPermissionLauncher.launch(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }
    }

    private fun showBox(list: ArrayList<String>, position: Int) {
        val mDialogView = layoutInflater.inflate(R.layout.input_box, null)

        val btSave: com.google.android.material.button.MaterialButton = mDialogView.findViewById(R.id.btSave)
        val btDelete: com.google.android.material.button.MaterialButton = mDialogView.findViewById(R.id.btDelete)
        val inputEditText: com.google.android.material.textfield.TextInputEditText = mDialogView.findViewById(R.id.txtinput)
        inputEditText.setText(list[position])

        val mAlertDialog = MaterialAlertDialogBuilder(this)
            .setView(mDialogView)
            .create()
        mAlertDialog.show()

        btSave.setOnClickListener {
            list[position] = inputEditText.text.toString()
            PreferenceSettings(this).listData = list
            todoAdapter.notifyDataSetChanged()
            mAlertDialog.dismiss()
            TodoWidgetProvider.notifyWidget(this)
        }

        btDelete.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.ask_delete_item)
                .setPositiveButton(R.string.ok) { _, _ ->
                    list.removeAt(position)
                    PreferenceSettings(this).listData = list
                    todoAdapter.notifyItemRemoved(position)
                    todoAdapter.notifyItemRangeChanged(position, list.size)
                    mAlertDialog.dismiss()
                    TodoWidgetProvider.notifyWidget(this)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    mAlertDialog.dismiss()
                }
                .show()
        }
    }

    private fun addList() {
        if (binding.editText.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, R.string.empty_input_message, Toast.LENGTH_SHORT).show()
        } else {
            items.add(binding.editText.text.toString())
            PreferenceSettings(this).listData = items
            binding.editText.setText("")
            todoAdapter.notifyItemInserted(items.size - 1)
            TodoWidgetProvider.notifyWidget(this)
        }
    }
}
