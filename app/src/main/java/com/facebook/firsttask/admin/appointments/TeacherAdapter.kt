package com.facebook.firsttask.admin.appointments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.ItemGetallappWithteacheratrributesBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class TeacherAdapter(private val appointments: List<TeacherAppointmentData>) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {


    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

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

        holder.binding.editlocationButton.setOnClickListener {
            // Handle button click, e.g., open an edit dialog
        }
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
