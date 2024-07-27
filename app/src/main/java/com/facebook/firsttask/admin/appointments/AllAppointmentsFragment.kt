package com.facebook.firsttask.admin.appointments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.FragmentAllAppointmentsBinding
import kotlinx.coroutines.launch
import java.util.*

class AllAppointmentsFragment : Fragment() {

    private var _binding: FragmentAllAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkForAppointments: NetworkForAppointments
    private lateinit var preferencesManager: PreferencesManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllAppointmentsBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())

        networkForAppointments = preferencesManager.getAuthToken()?.let { NetworkForAppointments(it, requireContext()) }!!

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        setupStatusSpinner()
        fetchAppointments()

    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.etPtmDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        binding.btnCalendar.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun setupStatusSpinner() {
        val statusOptions = resources.getStringArray(R.array.status_options)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectStatus.adapter = adapter
    }

    private fun fetchAppointments() {
        lifecycleScope.launch {
            try {
                val appointments = networkForAppointments.getAllAppointments()
                // Assuming you have a list of `AppointmentData` from the response
                val adapter = AppointmentAdapter(appointments)
                binding.reclerviewofallappointments.layoutManager = LinearLayoutManager(requireContext())
                binding.reclerviewofallappointments.adapter = adapter
            } catch (e: Exception) {
                Log.e("AllAppointmentsFragment", "Failed to fetch appointments: ${e.message}", e)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
