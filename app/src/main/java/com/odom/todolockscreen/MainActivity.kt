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



class MainActivity : AppCompatActivity() {

    // 빈 데이터 리스트 생성.
    val items = ArrayList<String>()
    //  하나의? 여려개 선택도 가능한게 나은데 // 이 가능한  adapter 설정
    val adapter by lazy {  ArrayAdapter(this, android.R.layout.select_dialog_multichoice, items) } // simple_list_item_multiple_choice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 설정
        settingButton.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }


        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        listView.setOnItemClickListener { parent, view, position, id ->
            Log.d("TAG", "클릭")
            Log.d("TAG", parent.toString())
            Log.d("TAG", view.toString())
            Log.d("TAG", position.toString())
            Log.d("TAG", id.toString())

            showBox(items, position)
            setStringArrayPref("listData", items)
        }

        //할일 추가
        addListButton.setOnClickListener {
            val count = adapter.count
            Log.d("갯수 : ", count.toString())

            addList()
        }

        // 선택 삭제
        deleteListButton.setOnClickListener {
            //선택된 아이템들
            val checkedItems = listView.checkedItemPositions

            for (i in adapter.count - 1 downTo 0) {
                if (checkedItems.get(i)) {
                    Log.d("선택된 아이템 삭제", i.toString())
                    items.removeAt(i)
                }
            }
            adapter.notifyDataSetChanged()

            // 선택 초기화
            listView.clearChoices()
            // 배열로 저장
            setStringArrayPref("listData", items)

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
            .setTitle("수정하기")

        var txtMessage = mDialogView.findViewById(R.id.txtmessage) as TextView
        txtMessage.text = "Update item"
        txtMessage.setTextColor(Color.parseColor("#ff2222"))

        val bt = mDialogView.findViewById(R.id.btdone) as Button
        bt.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))

        val editText = mDialogView.findViewById(R.id.txtinput) as EditText
        editText.setText(list[position])

        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        bt.setOnClickListener {
            list[position] = editText.text.toString()
            setStringArrayPref("listData", list)
            adapter.notifyDataSetChanged()
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }


    // 할일 추가버튼 함수
    private fun addList(){

        if(editText.text.isEmpty()){
            Toast.makeText(applicationContext, "비었음다", Toast.LENGTH_SHORT).show()
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