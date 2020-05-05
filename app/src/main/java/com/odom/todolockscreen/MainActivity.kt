package com.odom.todolockscreen

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.provider.Settings
import android.webkit.PermissionRequest
import androidx.core.content.ContextCompat.startActivity


class MainActivity : AppCompatActivity() {

    // 빈 데이터 리스트 생성.
    val items = ArrayList<String>()

    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100

    //  하나의? 여려개 선택도 가능한게 나은데 // 이 가능한  adapter 설정
    val adapter by lazy {  ArrayAdapter(this, android.R.layout.select_dialog_item, items) }

    private val requiredPermissions = arrayOf(
         Manifest.permission.SYSTEM_ALERT_WINDOW)

    private var permissionCodes = arrayOf(
        multiplePermissionsCode

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = resources.getColor(R.color.colorGray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (!Settings.canDrawOverlays(this)) {
            // You don't have permission
            Log.d("rnjsgks", "권한 ㄴㄴ")
            checkPermission4()
        }

        // 다른 거 위에 뜨는게 허용 안되있으면
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val myIntent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//            myIntent.data = Uri.parse("package:$packageName")
//            startActivity(myIntent)
//            // 뜨고 허용했을때 앱 실행되고
//        }
        // 허용되있으면 그냥 바로 메인뜨고..


        // 설정
        settingButton.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }


        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_NONE

        listView.setOnItemClickListener { parent, view, position, id ->
            Log.d("TAG", "클릭")

            showBox(items, position)
            setStringArrayPref("listData", items)
        }

        // 엔터눌러 입력
        editText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                addList()
            }

            true
        }

        //할일 추가
        addListButton.setOnClickListener {
            val count = adapter.count
            Log.d("갯수 : ", count.toString())
            addList()
        }

        // 이전 목록있으면 새로고침 전에도 넣어주시고
        val listPref =  getStringArrayPref("listData")
        if(listPref.size > 0){
            for (value in listPref)
                items.add(value)
        }

        // 당겨서 새로고침
        pullToRefresh.setOnRefreshListener {
            //삭제된게 반영된 배열을 불러와야하니까
            // 기존에 있던 거 없애고
            items.clear()
            adapter.notifyDataSetChanged()
            //  다시 채움
            val listPref2 =  getStringArrayPref("listData")
            if(listPref2.size > 0){
                for (value in listPref2)
                    items.add(value)
            }

            adapter.notifyDataSetChanged()
            // 선택 초기화
            listView.clearChoices()
            pullToRefresh.isRefreshing = false
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission4()
            } else {
                // Do as per your logic
                Log.d("아씨바", "권한없으")
            }

        }
    }


    fun checkPermission4() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
                startActivityForResult(intent, 123)
            }
        }
    }


    fun checkPermission3(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정"
                        ) { dialogInterface, i ->
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:" + packageName)
                        startActivity(intent)
                        }
                        .setPositiveButton("확인"
                        ) { dialogInterface, i -> finish() }
                        .setCancelable(false)
                        .create()
                        .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= 23) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d("Tag11", "Permission: "+permissions[0]+ "was "+grantResults[0])
                //resume tasks needing this permission
            }else{
                Log.d("Tag22", "Permission: "+permissions[0]+ "was "+grantResults[0])
                AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("저장소 권한이 22거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                    .setNeutralButton("설정"
                    ) { dialogInterface, i ->
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                    .setPositiveButton("확인"
                    ) { dialogInterface, i -> finish() }
                    .setCancelable(false)
                    .create()
                    .show()
            }
        }
    }


    // 알림 박스에서 항목 수정 .. 필요한 기능인가?
    fun showBox(list :ArrayList<String>, position :Int) {
        Log.d("TAG", "show box")
        // val dia = AlertDialog.Builder(this)
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.input_box, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val bt1 = mDialogView.findViewById(R.id.btdone) as Button
        val bt2 = mDialogView.findViewById(R.id.btdelete) as Button
        val editText = mDialogView.findViewById(R.id.txtinput) as EditText
        editText.setText(list[position])

        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        bt1.setOnClickListener {
            list[position] = editText.text.toString()
            setStringArrayPref("listData", list)
            adapter.notifyDataSetChanged()
            //dismiss dialog
            mAlertDialog.dismiss()
        }

        // 삭제
        bt2.setOnClickListener {
            Log.d("TAG", "삭제버튼")

            //알림 & 화면 종료
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle(R.string.ask_delete_item)
                //.setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        list.removeAt(position)
                        setStringArrayPref("listData", list)
                        adapter.notifyDataSetChanged()
                        mAlertDialog.dismiss()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        mAlertDialog.dismiss()
                    })

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


    // 할일 추가버튼 함수
    private fun addList(){

        if(editText.text.isEmpty()){
            Toast.makeText(applicationContext, R.string.empty_input_message, Toast.LENGTH_SHORT).show()
        }
        // 빈 입력 아니면 추가
        else{
            // 텍스트 추가
            items.add(editText.text.toString())
            // 배열로 저장
            setStringArrayPref("listData", items)
            editText.setText("")
            adapter.notifyDataSetChanged()
        }
    }

    // JSON 배열로 저장
    fun setStringArrayPref(key: String, values: ArrayList<String>) {
        val prefs = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val a = JSONArray()
        for (i in 0 until values.size) {
            a.put(values[i])
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.apply()
    }

    // 저장된 배열 받아옴
    fun getStringArrayPref(key: String): ArrayList<String> {
        val prefs = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val json = prefs.getString(key, null)
        val urls = ArrayList<String>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return urls
    }
}