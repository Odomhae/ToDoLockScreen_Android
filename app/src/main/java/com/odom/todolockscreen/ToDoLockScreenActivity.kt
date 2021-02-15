package com.odom.todolockscreen


import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_to_do_locksceen.*
import kotlinx.android.synthetic.main.activity_to_do_locksceen.view.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class ToDoLockScreenActivity : AppCompatActivity() {

    // 빈 데이터 리스트 생성.
    val lockScreenItems = ArrayList<String>()

    // 앱 종료 여부 판단
    var finn = false
    var finBt = false

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
        swipeToFinish()

        val listPref =  getStringArrayPref("listData")
        // 리스트 비었으면
        if(listPref.size == 0){
            Log.d("TAG", "할일 없음")
            finish()
        }
        else{
            for (value in listPref)
                lockScreenItems.add(value)

            recyclerView.adapter = MyAdapter(listPref)

            // 간격 30
            val spaceDecoration = VerticalSpaceItemDecoration(30)
            recyclerView.addItemDecoration(spaceDecoration)

            initView()
            val textColor = getInt("textColor")
            val listColor = getInt("listColor")
            Log.d("글자색 전달", textColor.toString())
            Log.d("아이템색 전달 ", listColor.toString())

            (recyclerView.adapter as MyAdapter).getInts(textColor, listColor)

        }

    }

    // 밀어서 잠금해제
    private fun swipeToFinish(){

        var startX = 0
        var startY = 0

        var endX = 0
        var endY = 0
        lockScreenBackground.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    // 초기값
                    startX =  event.x.toInt()
                    startY =  event.y.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    // 이동 값
                    endX = event.x.toInt()
                    endY = event.y.toInt()
                }

                // 이동 끝내고 조건 맞으면 헤제
                else -> {
                    if( ((endX- startX)*(endX - startX)) + ((endY - startY)*(endY- startY)) >= 80000 )
                        finish()
                }
            }
            true
        }

    }

    // 설정값 가져오기
    fun getInt( key : String) : Int{
        val prefs = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        return prefs.getInt(key, 0)
    }

    interface ItemDragListener{
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
        fun onItemMove(from_position:Int, to_position:Int)
    }

    // 뷰 초기화
    fun initView() {

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 배경색
        val backgroundColor = getInt("backgroundColor")
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
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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

        // ItemTouchHelper 구현 (SDK Version 22부터 사용 가능)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags: Int  = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags :Int = ItemTouchHelper.START or ItemTouchHelper.END
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }

            // 위치 swap
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Log.d("TAG", "위치 바꿉시다2")

                recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                MyAdapter(lockScreenItems).swap(viewHolder.adapterPosition, target.adapterPosition)
                setStringArrayPref("listData", lockScreenItems)
                return true
            }

            //밀어서 삭제
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Adapter에 아이템 삭제 요청
                val mDialogView = LayoutInflater.from(this@ToDoLockScreenActivity).inflate(R.layout.ask_box, null)
                //AlertDialogBuilder
                val mBuilder = AlertDialog.Builder(this@ToDoLockScreenActivity)
                    .setView(mDialogView)

                val mAlertDialog = mBuilder.show()

                val noBt = mDialogView.findViewById(R.id.noButton) as Button
                noBt.setOnClickListener {
                    initView()
                    mAlertDialog.dismiss()
                }

                val yesBt = mDialogView.findViewById(R.id.yesButton) as Button
                yesBt.setOnClickListener {
                    //잠금화면에서 지우고
                    (recyclerView.adapter as MyAdapter).deleteList(viewHolder.adapterPosition)

                    lockScreenItems.removeAt(viewHolder.layoutPosition)
                    setStringArrayPref("listData", lockScreenItems)

                    mAlertDialog.dismiss()

                    if(lockScreenItems.size == 0){
                        finn = true

                        //알림 & 화면 종료
                        val builder = AlertDialog.Builder(this@ToDoLockScreenActivity)

                        builder.setTitle(R.string.exit_app_message)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(R.string.nothing_left_message)
                            .setPositiveButton(R.string.ok,
                                DialogInterface.OnClickListener { dialog, id ->
                                    finBt = true
                                    finish()
                                })
                            .setNegativeButton(R.string.cancel,
                                DialogInterface.OnClickListener { dialog, id ->
                                    finish()
                                })

                        val alertDialog = builder.create()
                        alertDialog.show()
                    }

                }

            }
        }).apply {

            // ItemTouchHelper에 RecyclerView 설정
            attachToRecyclerView(recyclerView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 할일 없고 앱 종료 버튼 눌렸으면
        // 앱 종료
        if (finn && finBt){
            Log.d("TAG","앱 프로세스 종료")
            exitProcess(0)
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
            }

            outRect.bottom = verticalSpaceHeight
        }
    }

    class MyAdapter(private var datas: ArrayList<String>) : RecyclerView.Adapter<MyViewHolder>(), ItemDragListener{

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_to_do_locksceen, parent, false)
            Log.d("TAG" , "onCreateViewHolder")

            return MyViewHolder(view)
        }

        // 글자, 아이템 색
        var holderTextColor = 1
        var holderItemColor = 1

        override fun getItemCount(): Int {
            return datas.size
        }

        // 설정값 넣어주고
        fun getInts(a :Int, b :Int){
            holderTextColor = a
            holderItemColor = b
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

            Log.d("삭제샹", position.toString())
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textField.text = datas[position]

            // 글자색
            when(holderTextColor){
                0-> holder.textField.setTextColor(Color.parseColor("#ffffff"))
                1-> holder.textField.setTextColor(Color.parseColor("#DCDCDC"))
                2-> holder.textField.setTextColor(Color.parseColor("#000000"))
                3-> holder.textField.setTextColor(Color.parseColor("#FF0023"))
                4-> holder.textField.setTextColor(Color.parseColor("#b80f0a"))
                5-> holder.textField.setTextColor(Color.parseColor("#FA8072"))
                6-> holder.textField.setTextColor(Color.parseColor("#F2DFD2"))
                7-> holder.textField.setTextColor(Color.parseColor("#f37021"))
                8-> holder.textField.setTextColor(Color.parseColor("#7c4700"))
                9-> holder.textField.setTextColor(Color.parseColor("#432711"))
                10-> holder.textField.setTextColor(Color.parseColor("#2C40DC"))
                11-> holder.textField.setTextColor(Color.parseColor("#ff89d3fb"))
                12-> holder.textField.setTextColor(Color.parseColor("#1CAE4C"))
                13-> holder.textField.setTextColor(Color.parseColor("#52D017"))
                14-> holder.textField.setTextColor(Color.parseColor("#69e0a5"))
                15-> holder.textField.setTextColor(Color.parseColor("#e8f321"))
                16-> holder.textField.setTextColor(Color.parseColor("#f987c5"))
                17-> holder.textField.setTextColor(Color.parseColor("#cc99ff"))
                18-> holder.textField.setTextColor(Color.parseColor("#FF00FF"))
                19-> holder.textField.setTextColor(Color.parseColor("#8b00ff"))
            }

            // 각 아이템 색
            when(holderItemColor){
                0 -> holder.itemView.setBackgroundResource(R.color.colorBlack)
                1 -> holder.itemView.setBackgroundResource(R.color.colorGray)
                2 -> holder.itemView.setBackgroundResource(R.color.colorWhite)
                3 -> holder.itemView.setBackgroundResource(R.color.colorRed)
                4 -> holder.itemView.setBackgroundResource(R.color.colorCrimson)
                5 -> holder.itemView.setBackgroundResource(R.color.colorSalmon)
                6 -> holder.itemView.setBackgroundResource(R.color.colorBeige)
                7 -> holder.itemView.setBackgroundResource(R.color.colorOrange)
                8 -> holder.itemView.setBackgroundResource(R.color.colorBrown)
                9 -> holder.itemView.setBackgroundResource(R.color.colorWalnut)
                10 -> holder.itemView.setBackgroundResource(R.color.colorBlue)
                11-> holder.itemView.setBackgroundResource(R.color.colorMalibu)
                12 -> holder.itemView.setBackgroundResource(R.color.colorGreen)
                13 -> holder.itemView.setBackgroundResource(R.color.colorYellowGreen)
                14 -> holder.itemView.setBackgroundResource(R.color.colorMint)
                15 -> holder.itemView.setBackgroundResource(R.color.colorYellow)
                16 -> holder.itemView.setBackgroundResource(R.color.colorPink)
                17 -> holder.itemView.setBackgroundResource(R.color.colorViolet)
                18 -> holder.itemView.setBackgroundResource(R.color.colorMagenta)
                19 -> holder.itemView.setBackgroundResource(R.color.colorPurple)

                else -> holder.itemView.setBackgroundResource(R.drawable.item_view)
            }

            holder.itemView.isSelected = true

            // 폭 설정
            val layoutParams = holder.itemView.layoutParams
            layoutParams.height = 120
            holder.itemView.requestLayout()
        }

        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            TODO("Not yet implemented")
        }

        override fun onItemMove(from_position: Int, to_position: Int) {
            Log.d("TAG", "위치변경")
            val s = datas.get(from_position)
            datas.remove(datas[from_position])
            datas.add(to_position, s)
            notifyItemMoved(from_position, to_position)
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