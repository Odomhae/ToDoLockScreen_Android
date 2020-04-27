package com.odom.todolockscreen

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Toast
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.KeyEvent


class MainActivity : AppCompatActivity() {

    // 빈 데이터 리스트 생성.
    val items = ArrayList<String>()

    //  하나의? 여려개 선택도 가능한게 나은데 // 이 가능한  adapter 설정
    val adapter by lazy {  ArrayAdapter(this, android.R.layout.select_dialog_item, items) } // simple_list_item_multiple_choice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = resources.getColor(R.color.colorGray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

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