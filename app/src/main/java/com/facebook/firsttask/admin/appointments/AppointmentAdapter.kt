package com.facebook.firsttask.admin.appointments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.databinding.ItemGetallappointmentsBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentAdapter(private val appointments: List<AppointmentData>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemGetallappointmentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class AppointmentViewHolder(private val binding: ItemGetallappointmentsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: AppointmentData) {
            binding.dateTextView.text = formatDate(appointment.ptmDate)
            binding.meetingtimetextView.text = "${appointment.startTime} ${appointment.endTime}"
            binding.childnameTextview.text = appointment.childName
            binding.classNametextview.text = appointment.className
            binding.wingNametextview.text = appointment.wingName
            binding.teacherNametextview.text = appointment.teacherName
            binding.statusTextView.text = appointment.status
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
    }
}
