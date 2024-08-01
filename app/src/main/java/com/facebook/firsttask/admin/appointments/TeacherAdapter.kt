package com.facebook.firsttask.admin.appointments

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.ItemGetallappWithteacheratrributesBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherAdapter(private val appointments: List<TeacherAppointmentData>,
                     private  val context: Context,
                     private val fragmentManager: FragmentManager,
                     private val listener: OnTeacherSwappedListener // Callback listener
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {


    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    inner class TeacherViewHolder(val binding: ItemGetallappWithteacheratrributesBinding) : RecyclerView.ViewHolder(binding.root) {
        // You can access views directly via binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val binding = ItemGetallappWithteacheratrributesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val appointment = appointments[position]

        // Assuming you want to display the first timeslot data
        val timeslot = appointment.timeslots.firstOrNull()

        holder.binding.dateTextView.text = formatDate(appointment.ptmDate)
        holder.binding.teacherNameTextView.text = appointment.teacherName
        holder.binding.locationTextview.text = timeslot?.location ?: "N/A"
        holder.binding.appointmentstextview.text = appointment.totalAppts.toString()

        holder.binding.editButton.setOnClickListener {
            // Handle button click, e.g., open an edit dialog
            showPopupMenu(holder.binding.editButton, position)

        }
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.teacher_popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.swapTeacher -> {
                    handleSwapTeacher(position)
                    true
                }
                R.id.viewAll -> {
                    handleViewAll(position)
                    true
                }
                else -> false
            }
        }

        popupMenu.setForceShowIcon(true)
        popupMenu.show()
    }
    private fun handleSwapTeacher(position: Int) {
            val appointment = appointments[position]
            val ptmid = appointment.ptmId
            val teacherId = appointment.teacherId

            val dialogFragment = SwapTeacherDialogFragment.newInstance(ptmid, teacherId,listener)
            dialogFragment.show(fragmentManager, "SwapTeacherDialog")

    }

    private fun handleViewAll(position: Int) {
        val appointment = appointments[position]
        val timeslot = appointment.timeslots.firstOrNull()

        val teacherName = appointment.teacherName
        val location = timeslot?.location ?: "N/A"
        val date = formatDate(appointment.ptmDate)
        val studentName = timeslot?.childName ?: "N/A"
        val startTime = timeslot?.startTime ?: "N/A"
        val endTime = timeslot?.endTime ?: "N/A"

        val dialogFragment = ViewAllAppointmentsFragment.newInstance(
            teacherName, location, date, studentName, startTime, endTime
        )
        dialogFragment.show(fragmentManager, "ViewAllAppointmentsDialog")
    }



    private fun formatDate(dateString: String): String {
        return try {
            val date = inputDateFormat.parse(dateString)
            outputDateFormat.format(date)
        } catch (e: ParseException) {
            Log.e("AppointmentAdapter", "Date parsing error: $dateString", e)
            dateString
        }
    }



    override fun getItemCount(): Int {
        return appointments.size
    }
}
