package com.facebook.firsttask.admin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
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

    data class HighlightedDateInfo(
        val date: Date,
        val count: Int,
        val status: String
    )

    private val highlightedDates = mutableListOf<HighlightedDateInfo>()

    fun highlightDates(dates: List<Date>, countsAndStatuses: List<Pair<Int, String>>) {
        highlightedDates.clear()
        for (i in dates.indices) {
            val date = dates[i]
            val (count, status) = countsAndStatuses[i]
            val truncatedStatus = status.take(4) // Take first 4 letters of status
            highlightedDates.add(HighlightedDateInfo(date, count, truncatedStatus))
        }
        invalidate() // Redraw the view
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

        // Highlight the specified dates by filling the cell with pink color
        val highlightPaint = Paint().apply {
            color = Color.parseColor("#FFC0CB") // Pink color
            alpha = 128 // Set alpha to 128 for 50% transparency

        }
        val textPaint = Paint().apply {
            color = Color.BLUE
            textSize = 35f
            textAlign = Paint.Align.LEFT
        }
        for (info in highlightedDates) {
            val position = calculateDatePosition(info.date)
            if (position != null) {
                // Calculate the rectangle's boundaries
                val left = (position.x - cellWidth / 2).toFloat()
                val top = (position.y - cellHeight / 2).toFloat()
                val right = (position.x + cellWidth / 2).toFloat()
                val bottom = (position.y + cellHeight / 2).toFloat()

                // Fill cell with pink color
                canvas.drawRect(left, top, right, bottom, highlightPaint)

                // Adjust y positions for count and status
                val countYPosition = (position.y - 40).toFloat() // Move count text up
                val statusYPosition = (position.y + 60).toFloat() // Move status text down

                // Adjust x position for status
                val statusXPosition = (position.x - 40).toFloat() // Move status text to the left


                // Draw count and truncated status within the cell
                canvas.drawText(
                    info.count.toString(),
                    position.x.toFloat(),
                    countYPosition,
                    textPaint
                )
                canvas.drawText(
                    info.status,
                    statusXPosition,
                    statusYPosition,
                    textPaint
                )
            }
        }
    }


    fun showPreviousMonth() {
        calendar.add(Calendar.MONTH, -1)
        invalidate() // Redraw the view
        monthChangeListener?.onMonthChange(calendar) // Notify fragment
    }

    fun showNextMonth() {
        calendar.add(Calendar.MONTH, 1)
        invalidate() // Redraw the view
        monthChangeListener?.onMonthChange(calendar) // Notify fragment
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
        val displayMonth = calendar.get(Calendar.MONTH)
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

        // Reset calendar to current display month after drawing
        calendar.set(Calendar.MONTH, displayMonth)
    }

    private fun calculateDatePosition(date: Date): Point? {
        val tempCalendar = Calendar.getInstance().apply { time = date }
        if (tempCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR) ||
            tempCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)
        ) {
            // Date is not in the current month being displayed, so return null
            return null
        }

        val day = tempCalendar.get(Calendar.DAY_OF_MONTH)

        // Get the dimensions of each cell
        val cellWidth = width / 7
        val cellHeight = height / 8  // Adjusted for 8 rows instead of 6

        // Calculate the day index (0-based)
        val dayIndex = day - 1

        // Calculate the row and column in the grid
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val rowIndex = (firstDayOfWeek + dayIndex) / 7 // Calculate row index correctly
        val colIndex = (firstDayOfWeek + dayIndex) % 7

        // Calculate the center position of the cell
        val x = (colIndex * cellWidth) + (cellWidth / 2)
        val y = (rowIndex * cellHeight) + (cellHeight * 1.5).toInt() // Adjusted y position

        return Point(x, y)
    }

}
