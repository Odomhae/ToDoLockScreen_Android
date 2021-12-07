package com.odom.todolockscreen.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.odom.todolockscreen.PreferenceSettings
import com.odom.todolockscreen.R
import com.odom.todolockscreen.ToDoLockScreenActivity
import kotlinx.android.synthetic.main.activity_to_do_locksceen.view.*
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter(private val context: Context, private var datas: ArrayList<String>)
    : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(), ToDoLockScreenActivity.ItemDragListener {

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var textField : TextView = view.recyclerview_text
    }

    override fun getItemCount() = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_to_do_locksceen, parent, false)

        val textColor = PreferenceSettings(context).textColor
        val itemHolderColor = PreferenceSettings(context).listColor
        // 글자색
        when(textColor){
            0 -> view.recyclerview_text.setTextColor(Color.parseColor("#ffffff"))
            1 -> view.recyclerview_text.setTextColor(Color.parseColor("#DCDCDC"))
            2 -> view.recyclerview_text.setTextColor(Color.parseColor("#000000"))
            3 -> view.recyclerview_text.setTextColor(Color.parseColor("#FF0023"))
            4 -> view.recyclerview_text.setTextColor(Color.parseColor("#b80f0a"))
            5 -> view.recyclerview_text.setTextColor(Color.parseColor("#FA8072"))
            6 -> view.recyclerview_text.setTextColor(Color.parseColor("#F2DFD2"))
            7 -> view.recyclerview_text.setTextColor(Color.parseColor("#f37021"))
            8 -> view.recyclerview_text.setTextColor(Color.parseColor("#7c4700"))
            9 -> view.recyclerview_text.setTextColor(Color.parseColor("#432711"))
            10 -> view.recyclerview_text.setTextColor(Color.parseColor("#2C40DC"))
            11 -> view.recyclerview_text.setTextColor(Color.parseColor("#ff89d3fb"))
            12 -> view.recyclerview_text.setTextColor(Color.parseColor("#1CAE4C"))
            13 -> view.recyclerview_text.setTextColor(Color.parseColor("#52D017"))
            14 -> view.recyclerview_text.setTextColor(Color.parseColor("#69e0a5"))
            15 -> view.recyclerview_text.setTextColor(Color.parseColor("#e8f321"))
            16 -> view.recyclerview_text.setTextColor(Color.parseColor("#f987c5"))
            17 -> view.recyclerview_text.setTextColor(Color.parseColor("#cc99ff"))
            18 -> view.recyclerview_text.setTextColor(Color.parseColor("#FF00FF"))
            19 -> view.recyclerview_text.setTextColor(Color.parseColor("#8b00ff"))
        }

        // 아이템 색
        // 리스트 둥글게
        // TODO: 2021-12-07  
        when(itemHolderColor){
           0 -> view.setBackgroundResource(R.drawable.shape_item_black)
           1 -> view.setBackgroundResource(R.drawable.shape_item_gray)
           2 -> view.setBackgroundResource(R.drawable.shape_item_white)
           3 -> view.setBackgroundResource(R.drawable.shape_item_red)
           4 -> view.setBackgroundResource(R.drawable.shape_item_crimson)
           5 -> view.setBackgroundResource(R.drawable.shape_item_salmon)
           6 -> view.setBackgroundResource(R.drawable.shape_item_beige)
           7 -> view.setBackgroundResource(R.drawable.shape_item_orange)
           8 -> view.setBackgroundResource(R.drawable.shape_item_brown)
           9 -> view.setBackgroundResource(R.drawable.shape_item_walnut)
           10 -> view.setBackgroundResource(R.drawable.shape_item_blue)
           11 -> view.setBackgroundResource(R.drawable.shape_item_malibu)
           12 -> view.setBackgroundResource(R.drawable.shape_item_green)
           13 -> view.setBackgroundResource(R.drawable.shape_item_yellowgreen)
           14 -> view.setBackgroundResource(R.drawable.shape_item_mint)
           15 -> view.setBackgroundResource(R.drawable.shape_item_yellow)
           16 -> view.setBackgroundResource(R.drawable.shape_item_pink)
           17 -> view.setBackgroundResource(R.drawable.shape_item_violet)
           18 -> view.setBackgroundResource(R.drawable.shape_item_magenta)
           19 -> view.setBackgroundResource(R.drawable.shape_item_purple)
       }

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textField.text = datas[position]
        holder.itemView.isSelected = true

        // 폭 설정
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 120
        holder.itemView.requestLayout()
    }

    // 순서변경
    fun swap(firstPosition :Int, secondPosition : Int) {

        Collections.swap(datas, firstPosition, secondPosition)
        notifyItemMoved(firstPosition, secondPosition)
    }

    // 삭제
    fun deleteList(position: Int){
        datas.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onItemMove(from_position: Int, to_position: Int) {
        val data = datas[from_position]
        datas.remove(datas[from_position])
        datas.add(to_position, data)
        notifyItemMoved(from_position, to_position)
    }
}