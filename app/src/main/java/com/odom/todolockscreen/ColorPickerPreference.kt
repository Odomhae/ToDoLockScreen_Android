package com.odom.todolockscreen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ColorPickerPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : Preference(context, attrs) {

    companion object {
        // colorCategory 순서: White, Gray, Black, Red ...
        val COLOR_CATEGORY = intArrayOf(
            0xFFffffff.toInt(),
            0xFFDCDCDC.toInt(),
            0xFF000000.toInt(),
            0xFFFF0023.toInt(),
            0xFFb80f0a.toInt(),
            0xFFFA8072.toInt(),
            0xFFF2DFD2.toInt(),
            0xFFf37021.toInt(),
            0xFF7c4700.toInt(),
            0xFF432711.toInt(),
            0xFF2C40DC.toInt(),
            0xFF89d3fb.toInt(),
            0xFF1CAE4C.toInt(),
            0xFF52D017.toInt(),
            0xFF69e0a5.toInt(),
            0xFFe8f321.toInt(),
            0xFFf987c5.toInt(),
            0xFFcc99ff.toInt(),
            0xFFFF00FF.toInt(),
            0xFF8b00ff.toInt()
        )

        // listColorCategory 순서: Black, Gray, White, Red ...
        val LIST_COLOR_CATEGORY = intArrayOf(
            0xFF000000.toInt(),
            0xFFDCDCDC.toInt(),
            0xFFffffff.toInt(),
            0xFFFF0023.toInt(),
            0xFFb80f0a.toInt(),
            0xFFFA8072.toInt(),
            0xFFF2DFD2.toInt(),
            0xFFf37021.toInt(),
            0xFF7c4700.toInt(),
            0xFF432711.toInt(),
            0xFF2C40DC.toInt(),
            0xFF89d3fb.toInt(),
            0xFF1CAE4C.toInt(),
            0xFF52D017.toInt(),
            0xFF69e0a5.toInt(),
            0xFFe8f321.toInt(),
            0xFFf987c5.toInt(),
            0xFFcc99ff.toInt(),
            0xFFFF00FF.toInt(),
            0xFF8b00ff.toInt()
        )
    }

    init {
        isPersistent = false
        widgetLayoutResource = R.layout.widget_color_dot
    }

    private fun getColors() = when (key) {
        "listColorCategory" -> LIST_COLOR_CATEGORY
        else -> COLOR_CATEGORY
    }

    private fun getCurrentIndex(): Int {
        val pref = PreferenceSettings(context)
        return when (key) {
            "textColorCategory" -> pref.textColor
            "listColorCategory" -> pref.listColor
            "backgroundColorCategory" -> pref.backgroundColor
            else -> 0
        }
    }

    private fun saveIndex(index: Int) {
        val pref = PreferenceSettings(context)
        when (key) {
            "textColorCategory" -> pref.textColor = index
            "listColorCategory" -> pref.listColor = index
            "backgroundColorCategory" -> pref.backgroundColor = index
        }
        TodoWidgetProvider.notifyWidget(context)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val dotView = holder.findViewById(R.id.colorDotWidget) as? View ?: return
        val index = getCurrentIndex()
        val colors = getColors()
        val strokeWidth = dp(2f)
        dotView.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colors[index])
            setStroke(strokeWidth, Color.parseColor("#AAAAAA"))
        }
    }

    override fun onClick() {
        showColorPickerDialog()
    }

    private fun showColorPickerDialog() {
        val colors = getColors()
        val currentIndex = getCurrentIndex()

        val grid = GridLayout(context).apply {
            columnCount = 5
            val pad = dp(16f)
            setPadding(pad, dp(8f), pad, dp(8f))
        }

        var dialog: AlertDialog? = null

        colors.forEachIndexed { index, color ->
            val size = dp(52f)
            val margin = dp(6f)
            val params = GridLayout.LayoutParams().apply {
                width = size
                height = size
                setMargins(margin, margin, margin, margin)
            }

            val isSelected = index == currentIndex
            val strokeWidth = if (isSelected) dp(4f) else dp(1f)
            val strokeColor = if (isSelected) Color.parseColor("#444444") else Color.parseColor("#BBBBBB")

            val circle = View(context).apply {
                layoutParams = params
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke(strokeWidth, strokeColor)
                }
                setOnClickListener {
                    saveIndex(index)
                    notifyChanged()
                    dialog?.dismiss()
                }
            }
            grid.addView(circle)
        }

        dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(grid)
            .show()
    }

    private fun dp(value: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics).toInt()
}
