package com.facebook.firsttask

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class CustomCalendarView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private val calendar = Calendar.getInstance()
    private val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    init {
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 40f
    }

    interface MonthChangeListener {
        fun onMonthChange(calendar: Calendar)
    }

    private var monthChangeListener: MonthChangeListener? = null

    fun setMonthChangeListener(listener: MonthChangeListener) {
        monthChangeListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height

        // Reduce the height of each cell slightly
        val cellWidth = width / 7
        val cellHeight = height / 8

        drawGrid(canvas, cellWidth, cellHeight)
        drawDayNames(canvas, cellWidth, cellHeight)
        drawDates(canvas, cellWidth, cellHeight)
    }
    fun showPreviousMonth() {
        calendar.add(Calendar.MONTH, -1)
        invalidate() // Redraw the view
        monthChangeListener?.onMonthChange(calendar) // Notify fragment
        // Optionally, notify the fragment or activity of month change
        // You can use a callback interface to notify the fragment
    }

    fun showNextMonth() {
        calendar.add(Calendar.MONTH, 1)
        invalidate() // Redraw the view
        monthChangeListener?.onMonthChange(calendar) // Notify fragment
        // Optionally, notify the fragment or activity of month change
        // You can use a callback interface to notify the fragment
    }

    private fun drawGrid(canvas: Canvas, cellWidth: Int, cellHeight: Int) {
        paint.color = Color.BLACK
        paint.strokeWidth = 2f

        for (i in 0..8) {
            // Draw horizontal lines
            canvas.drawLine(0f, (i * cellHeight).toFloat(), width.toFloat(), (i * cellHeight).toFloat(), paint)
        }
        for (j in 0..7) {
            // Draw vertical lines
            canvas.drawLine((j * cellWidth).toFloat(), 0f, (j * cellWidth).toFloat(), height.toFloat(), paint)
        }
    }

    private fun drawDayNames(canvas: Canvas, cellWidth: Int, cellHeight: Int) {
        // Draw grey background for day names row
        paint.color = Color.LTGRAY
        canvas.drawRect(0f, 0f, width.toFloat(), cellHeight.toFloat(), paint)

        paint.color = Color.BLACK
        for (i in dayNames.indices) {
            val x = (i * cellWidth + cellWidth / 2).toFloat()
            val y = (cellHeight / 2).toFloat()

            // Draw day name
            canvas.drawText(dayNames[i], x, y, paint)

            // Draw vertical line after each day name
            if (i < dayNames.size - 1) {
                canvas.drawLine((i + 1) * cellWidth.toFloat(), 0f, (i + 1) * cellWidth.toFloat(), cellHeight.toFloat(), paint)
            }
        }

        // Draw border line for day names row
        paint.color = Color.BLUE
        paint.strokeWidth = 2f
        canvas.drawLine(0f, cellHeight.toFloat(), width.toFloat(), cellHeight.toFloat(), paint)
    }


    private fun drawDates(canvas: Canvas, cellWidth: Int, cellHeight: Int) {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        var day = 1

        for (i in 0 until 6) {
            for (j in 0 until 7) {
                if (i == 0 && j < firstDayOfWeek) {
                    continue
                }
                if (day > maxDays) {
                    break
                }

                val x = (j * cellWidth + cellWidth / 2).toFloat()
                val y = ((i + 1) * cellHeight + cellHeight / 2).toFloat()

                    paint.color = Color.BLACK
                    canvas.drawText(day.toString(), x, y, paint)

                day++
            }
        }
    }
}
