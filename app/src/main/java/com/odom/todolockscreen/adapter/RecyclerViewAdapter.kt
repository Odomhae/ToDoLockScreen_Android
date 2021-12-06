package com.odom.todolockscreen.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textField.text = datas[position]

        val textColor = PreferenceSettings(context).textColor
        val itemHolderColor = PreferenceSettings(context).listColor

        // 글자색
        when(textColor){
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
        // TODO: 2021-10-31
        // 리스트 둥글게
        holder.itemView.setBackgroundResource(R.drawable.shape_item)
        when(itemHolderColor){
            0 -> {
                // holder.itemView.setBackgroundResource(R.color.colorBlack)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#000000"))
            }
            1 -> {
                // holder.itemView.setBackgroundResource(R.color.colorGray)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#DCDCDC"))
            }
            2 -> {
                // holder.itemView.setBackgroundResource(R.color.colorWhite)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#ffffff"))
            }
            3 -> {
                //   holder.itemView.setBackgroundResource(R.color.colorRed)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#FF0023"))
            }
            4 -> {
                //   holder.itemView.setBackgroundResource(R.color.colorCrimson)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#b80f0a"))
            }
            5 -> {
                // holder.itemView.setBackgroundResource(R.color.colorSalmon)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#FA8072"))
            }
            6 -> {
                //   holder.itemView.setBackgroundResource(R.color.colorBeige)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#F2DFD2"))
            }
            7 -> {
                //    holder.itemView.setBackgroundResource(R.color.colorOrange)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#f37021"))
            }
            8 -> {
                //    holder.itemView.setBackgroundResource(R.color.colorBrown)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#7c4700"))
            }
            9 -> {
                //    holder.itemView.setBackgroundResource(R.color.colorWalnut)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#432711"))
            }
            10 -> {
                //    holder.itemView.setBackgroundResource(R.color.colorBlue)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#2C40DC"))
            }
            11-> {
                //     holder.itemView.setBackgroundResource(R.color.colorMalibu)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#ff89d3fb"))
            }
            12 -> {
                //    holder.itemView.setBackgroundResource(R.color.colorGreen)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#1CAE4C"))
            }
            13 -> {
                //     holder.itemView.setBackgroundResource(R.color.colorYellowGreen)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#52D017"))
            }
            14 -> {
                //    holder.itemView.setBackgroundResource(R.color.colorMint)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#69e0a5"))
            }
            15 -> {
                //     holder.itemView.setBackgroundResource(R.color.colorYellow)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#e8f321"))
            }
            16 -> {
                //   holder.itemView.setBackgroundResource(R.color.colorPink)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#f987c5"))
            }
            17 -> {
                //     holder.itemView.setBackgroundResource(R.color.colorViolet)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#cc99ff"))
            }
            18 -> {
                //   holder.itemView.setBackgroundResource(R.color.colorMagenta)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#FF00FF"))
            }
            19 -> {
                //     holder.itemView.setBackgroundResource(R.color.colorPurple)
                val drawable = holder.itemView.background as GradientDrawable
                drawable.setColor(Color.parseColor("#8b00ff"))
            }

            else -> holder.itemView.setBackgroundResource(R.drawable.shape_item_view_default)
        }

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