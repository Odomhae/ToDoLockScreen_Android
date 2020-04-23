package com.odom.todolockscreen

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_to_do_locksceen.*
import org.json.JSONArray
import org.json.JSONException
import androidx.recyclerview.widget.ItemTouchHelper

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_to_do_locksceen.view.*
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import java.util.*
import kotlin.collections.ArrayList
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.animation.Animation


class ToDoLockScreenActivity : AppCompatActivity() {

    // 빈 데이터 리스트 생성.
    val lockScreenItems = ArrayList<String>()

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
        // 리스트 비었으면
        if(listPref.size ==0){
            Log.d("TAG", "할일 없음 ")
           // Toast.makeText(applicationContext, "끝 다함", Toast.LENGTH_SHORT).show()
            finish()
        }
        else{
            for (value in listPref)
                lockScreenItems.add(value)

            recyclerView.adapter = MyAdapter(listPref)

//            val resId = R.anim.layout_animation_fall_down
//            val animation = AnimationUtils.loadLayoutAnimation(applicationContext , resId)
//            recyclerView.layoutAnimation = animation

            val context : Context = recyclerView.context
            val controller :LayoutAnimationController =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

            recyclerView.layoutAnimation = controller
            //notifyDataSetChanged()
            recyclerView.scheduleLayoutAnimation()

            recyclerview_text.setTextColor(resources.getColor(R.color.colorCrimson))

            initView()
        }

    }

    // 설정에 따라
    fun getInt( key : String) : Int{
        val prefs = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }


    fun initView(){
        Log.d("TAG", "리사이클 뷰 초기화")

        // 배경색, 글자색
        val backgroundColor = getInt("backgroundColor")
        val textColor = getInt("textColor")

        recyclerview_text.setTextColor(resources.getColor(R.color.colorCrimson))

        when(backgroundColor){
            0 -> {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorWhite))
                // 삳태바도 같은 색으로 api 21 이상
                window.statusBarColor = resources.getColor(R.color.colorWhite)
                //상태바 글씨 보이게
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            1 -> {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorGray))
                window.statusBarColor = resources.getColor(R.color.colorGray)
            }
            2 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorBlack))
                window.statusBarColor = resources.getColor(R.color.colorBlack)
            }
            3 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorRed))
                window.statusBarColor = resources.getColor(R.color.colorRed)
            }
            4 -> {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorCrimson))
                window.statusBarColor = resources.getColor(R.color.colorCrimson)
            }
            5 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorSalmon))
                window.statusBarColor = resources.getColor(R.color.colorSalmon)
            }
            6 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorBeige))
                window.statusBarColor = resources.getColor(R.color.colorBeige)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            7 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorOrange))
                window.statusBarColor = resources.getColor(R.color.colorOrange)
            }
            8 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorBrown))
                window.statusBarColor = resources.getColor(R.color.colorBrown)
            }
            9 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorWalnut))
                window.statusBarColor = resources.getColor(R.color.colorWalnut)
            }
            10 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorBlue))
                window.statusBarColor = resources.getColor(R.color.colorBlue)
            }
            11 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorMalibu))
                window.statusBarColor = resources.getColor(R.color.colorMalibu)
            }
            12 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorGreen))
                window.statusBarColor = resources.getColor(R.color.colorGreen)
            }
            13 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorYellowGreen))
                window.statusBarColor = resources.getColor(R.color.colorYellowGreen)
            }
            14 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorMint))
                window.statusBarColor = resources.getColor(R.color.colorMint)
            }
            15 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorYellow))
                window.statusBarColor = resources.getColor(R.color.colorYellow)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            }
            16 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorPink))
                window.statusBarColor = resources.getColor(R.color.colorPink)
            }
            17 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorViolet))
                window.statusBarColor = resources.getColor(R.color.colorViolet)
            }
            18 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorMagenta))
                window.statusBarColor = resources.getColor(R.color.colorMagenta)
            }
            19 ->  {
                lockScreenBackground.setBackgroundColor(resources.getColor(R.color.colorPurple))
                window.statusBarColor = resources.getColor(R.color.colorPurple)
            }

        }

        // 간격 30
        val spaceDecoration = VerticalSpaceItemDecoration(30)
        recyclerView.addItemDecoration(spaceDecoration)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // ItemTouchHelper 구현 (SDK Version 22부터 사용 가능)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        or ItemTouchHelper.UP or ItemTouchHelper.DOWN
           ) {

            // 위치 swap
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Log.d("TAG", "위치 바꿉시다")
                MyAdapter(lockScreenItems).swap(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun isLongPressDragEnabled(): Boolean {
                Log.d("TAG", "오래누름")
                return false
            }
            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            //밀어서 삭제
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Adapter에 아이템 삭제 요청
                val builder = AlertDialog.Builder(this@ToDoLockScreenActivity)
                builder.setTitle("해당 일을 완료했습니끼?")
                    .setMessage("예를 누르면 리스트에서 삭제됩니다")
                    .setPositiveButton("예",
                        DialogInterface.OnClickListener { dialog, id ->
                            Log.d("TAG", "지워")
                            //잠금화면에서 지우고
                            (recyclerView.adapter as MyAdapter).deleteList(viewHolder.adapterPosition)

                            lockScreenItems.removeAt(viewHolder.layoutPosition)
                            setStringArrayPref("listData", lockScreenItems)

                            if(lockScreenItems.size == 0){
                                Toast.makeText(applicationContext, "끝 다함22", Toast.LENGTH_SHORT).show()
                                // 스위치 끄기
                                Log.d("TAG","스위치 끄기 1")
                                val fragment = SettingActivity.MyPreferenceFragment()
                                fragment.offLockScreen()


                                //화면 종료
                                finish()
                            }

                        })
                    .setNegativeButton("아니요",
                        DialogInterface.OnClickListener { dialog, id ->
                            initView()

                        })

                val alertDialog = builder.create()

                alertDialog.show()
            }
        }).apply {

            // ItemTouchHelper에 RecyclerView 설정
            attachToRecyclerView(recyclerView)
        }
    }



    class pp : PreferenceFragment(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.pref)
        }

        fun off(){
            Log.d("TAG","스위치 끄기 2")
            val useLockScreenPref2 = findPreference("useLockScreen") as SwitchPreference
            if(useLockScreenPref2.isChecked){
                useLockScreenPref2.isChecked = false
            }
        }


    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            // 첫번째면 위에도 여백
            if(parent.getChildAdapterPosition(view) == 0){
                outRect.top = verticalSpaceHeight
                //return
            }

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



        fun swap(firstPosition :Int, secondPosition : Int) {
            Log.d("위치변경 ", firstPosition.toString())
            Log.d("위치변경 ", secondPosition.toString())

            Collections.swap(datas, firstPosition, secondPosition)
            notifyItemMoved(firstPosition, secondPosition)
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

            // 각 아이템 모양
            holder.itemView.setBackgroundResource(R.drawable.item_view)
            holder.itemView.isSelected = true
            
//            holder.itemView.setOnTouchListener{ v, event ->
//               if(event.action == MotionEvent.ACTION_DOWN )
//                   mStartDragListener.requestDrag(holder)
//                else if(event.action == MotionEvent.ACTION_UP)
//                   mStartDragListener.requestDrag(holder)
//
//            }


            // 폭 설정
            val layoutParams = holder.itemView.layoutParams
            layoutParams.height = 120
            holder.itemView.requestLayout()
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var textField = view.recyclerview_text
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