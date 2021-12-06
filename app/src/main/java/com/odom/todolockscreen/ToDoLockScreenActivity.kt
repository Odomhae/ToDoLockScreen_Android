package com.odom.todolockscreen


import android.app.KeyguardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.adfit.common.util.v
import com.odom.todolockscreen.adapter.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_to_do_locksceen.*
import kotlinx.android.synthetic.main.activity_to_do_locksceen.view.*
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

        val listPref = PreferenceSettings(this).listData
        // 리스트 비었으면
        if(listPref.size == 0){
            finish()

        }else{
            for (value in listPref){
                lockScreenItems.add(value)
            }

            recyclerView.adapter = RecyclerViewAdapter(this, listPref)

            // 간격 30
            val spaceDecoration = VerticalSpaceItemDecoration(30)
            recyclerView.addItemDecoration(spaceDecoration)

            initView()
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

    interface ItemDragListener{
        fun onItemMove(from_position:Int, to_position:Int)
    }

    // 뷰 초기화
    fun initView() {

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 배경색
        val backgroundColor = PreferenceSettings(this).backgroundColor
        when(backgroundColor){
            0 -> {
                lockScreenBackground.setBackgroundColor(getColor(R.color.colorWhite))
                // 삳태바도 같은 색으로 api 21 이상
                window.statusBarColor = getColor(R.color.colorWhite)
                //상태바 글씨 보이게
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            1 -> {
                lockScreenBackground.setBackgroundColor(getColor(R.color.colorGray))
                window.statusBarColor = getColor(R.color.colorGray)
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

                recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                RecyclerViewAdapter(this@ToDoLockScreenActivity, lockScreenItems).swap(viewHolder.adapterPosition, target.adapterPosition)
                PreferenceSettings(this@ToDoLockScreenActivity).listData = lockScreenItems
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
                    (recyclerView.adapter as RecyclerViewAdapter).deleteList(viewHolder.adapterPosition)

                    lockScreenItems.removeAt(viewHolder.layoutPosition)
                    PreferenceSettings(this@ToDoLockScreenActivity).listData = lockScreenItems

                    mAlertDialog.dismiss()

                    if(lockScreenItems.size == 0){
                        finn = true

                        //알림 & 화면 종료
                        val builder = AlertDialog.Builder(this@ToDoLockScreenActivity)

                        builder.setTitle(R.string.exit_app_message)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(R.string.nothing_left_message)
                            .setPositiveButton(R.string.ok) { dialog, id ->
                                finBt = true
                                finish()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, id ->
                                finish()
                            }

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

}