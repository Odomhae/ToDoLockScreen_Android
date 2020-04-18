package com.odom.todolockscreen

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_to_do_locksceen.*
import org.json.JSONArray
import org.json.JSONException
import androidx.recyclerview.widget.ItemTouchHelper

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_to_do_locksceen.view.*
import android.R.attr.bottom
import android.graphics.Rect
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class ToDoLocksceenActivity : AppCompatActivity() {

    // 빈 데이터 리스트 생성.
    val lockScreenItems = ArrayList<String>()
    val adapter by lazy {  ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, lockScreenItems) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 기존 잠금화면보다 먼저 나타나도록
        // 버전별로
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            // 잠금화면에서 보여지게
            setShowWhenLocked(true)
            // 기존 잠금화면 해제
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)

        }else{
            // 잠금화면에서 보여지게
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            // 기존 잠금화면 해제
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        setContentView(R.layout.activity_to_do_locksceen)

        val listPref =  getStringArrayPref("listData")
        for (value in listPref)
            lockScreenItems.add(value)

        recyclerView.adapter = MyAdapter(listPref)

        initView()
    }

    fun initView(){
        Log.d("TAG", "리사이클 뷰 초기화")

        // 구분선 넣기
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, LinearLayoutManager(this).orientation)

        recyclerView.addItemDecoration(dividerItemDecoration)

        // 간격
        val spaceDecoration = VerticalSpaceItemDecoration(20)
        recyclerView.addItemDecoration(spaceDecoration)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ItemTouchHelper 구현 (SDK Version 22부터 사용 가능)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun isLongPressDragEnabled(): Boolean {
                Log.d("TAG", "오래누름")
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                Log.d("TAG", "위치바꿈")
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Adapter에 아이템 삭제 요청
                Log.d("TAG", "지워")
                //잠금화면에서 지우고
                (recyclerView.adapter as MyAdapter).deleteList(viewHolder.adapterPosition)

                // 배열에서도 지우고
                Log.d("TAG", "배열에서도 지워")
                //1번째 4번째는 아니고
                //중간에 2개
                Log.d("TAG",  viewHolder.adapterPosition.toString())
                Log.d("TAG",  viewHolder.layoutPosition.toString())
                Log.d("TAG",  viewHolder.position.toString()) // 모호하다고 deprecated됨
                Log.d("TAG",  viewHolder.oldPosition.toString())

                lockScreenItems.removeAt(viewHolder.layoutPosition)
                setStringArrayPref("listData", lockScreenItems)
            }
        }).apply {

            // ItemTouchHelper에 RecyclerView 설정
            attachToRecyclerView(recyclerView)
        }

    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    class MyAdapter(private var datas: ArrayList<String>) : RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_to_do_locksceen, parent, false)
            Log.d("TAG" , "onCreateViewHolder")
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return datas.size
        }

        // 삭제
        fun deleteList(position: Int){
            datas.removeAt(position)

            Log.d("T삭제샹", position.toString())
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Log.d("TAG" , "onBind")
            holder.textField.text = datas[position]

            // 폭 설정
            val layoutParams = holder.itemView.layoutParams
            layoutParams.height = 100
            holder.itemView.requestLayout()
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var textField = view.chat_title
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
