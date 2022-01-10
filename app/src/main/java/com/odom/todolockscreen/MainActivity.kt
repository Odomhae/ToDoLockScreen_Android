package com.odom.todolockscreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    // 빈 데이터 리스트 생성.
    val items = ArrayList<String>()
    //퍼미션 응답 처리 코드
    private val PermissionsCode = 100
    //  하나의? 여려개 선택도 가능한게 나은데 // 이 가능한  adapter 설정
    val adapter by lazy {  ArrayAdapter(this, android.R.layout.select_dialog_item, items) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = resources.getColor(R.color.colorGray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 확인해보고
        // 다른 거 위에 뜨는게 허용 안되있으면 띄우고
        // 허용되있으면 바로 실행
        checkPermission()

        // 설정
        settingButton.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }


        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_NONE

        listView.setOnItemClickListener { parent, view, position, id ->
            showBox(items, position)
            PreferenceSettings(this).listData = items
        }

        //할일 추가
        addListButton.setOnClickListener { addList() }

        // 이전 목록있으면 새로고침 전에도 넣어주시고
        val listPref = PreferenceSettings(this).listData
        if(listPref.size > 0){
            for (value in listPref)
                items.add(value)
        }

        // 당겨서 새로고침
        pullToRefresh.setOnRefreshListener {
            items.clear()
            adapter.notifyDataSetChanged()
            //  다시 채움
            val listPref2 = PreferenceSettings(this).listData
            if(listPref2.size > 0){
                for (value in listPref2)
                    items.add(value)
            }

            adapter.notifyDataSetChanged()
            pullToRefresh.isRefreshing = false
        }

        editText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addList()
                return@OnEditorActionListener true
            }

            false
        })

    }


    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, PermissionsCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PermissionsCode) {
            if (Settings.canDrawOverlays(this)) {
                Log.d("TAG", "권한 설정됨")
                val toast = Toast.makeText(applicationContext, R.string.permission_set_message, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP,  Gravity.CENTER, 550)
                    toast.show()
                onResume()
            }else{
                Log.d("TAG", "권한 거절됨")
                finish()
                val toast = Toast.makeText(applicationContext, R.string.permission_denied_message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP, Gravity.CENTER, 550)
                toast.show()
            }
        }
    }


    // 알림 박스에서 항목 수정 .. 필요한 기능인가?
    private fun showBox(list :ArrayList<String>, position :Int) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.input_box, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val btSave : Button = mDialogView.findViewById(R.id.btSave)
        val btDelete : Button = mDialogView.findViewById(R.id.btDelete)
        val inputEditText : EditText = mDialogView.findViewById(R.id.txtinput)
        inputEditText.setText(list[position])

        //show dialog
        val mAlertDialog = mBuilder.show()
        // 완료
        btSave.setOnClickListener {
            list[position] = inputEditText.text.toString()
            PreferenceSettings(this).listData = list
            adapter.notifyDataSetChanged()

            //dismiss dialog
            mAlertDialog.dismiss()
        }

        // 삭제
        btDelete.setOnClickListener {

            //알림 & 화면 종료
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle(R.string.ask_delete_item)
                .setPositiveButton(R.string.ok) { dialog, id ->
                    list.removeAt(position)
                    PreferenceSettings(this).listData = list
                    adapter.notifyDataSetChanged()

                    mAlertDialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    mAlertDialog.dismiss()
                }

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
            PreferenceSettings(this).listData = items

            editText.setText("")
            adapter.notifyDataSetChanged()
        }
    }

}