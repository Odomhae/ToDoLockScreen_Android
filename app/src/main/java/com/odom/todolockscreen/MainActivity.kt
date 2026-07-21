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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.OnApplyWindowInsetsListener
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
    private val PermissionsCode = 100

    private lateinit var todoAdapter: TodoMainAdapter
    lateinit var mAdView: AdView
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
        }

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)

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

        // 배너 광고
        MobileAds.initialize(this) {}
        mAdView = AdView(this)
        binding.adMobView.addView(mAdView)
        loadBanner()
    }

    private fun loadBanner() {
        mAdView.adUnitId = resources.getString(R.string.REAL_banner_ad_unit_id)
        mAdView.setAdSize(adSize)
        mAdView.loadAd(AdRequest.Builder().build())
    }

    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, PermissionsCode)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionsCode) {
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
        }
    }
}
