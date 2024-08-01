package com.facebook.firsttask.admin.appointments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentTeacherBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TeacherFragment : Fragment(), OnTeacherSwappedListener {

    private lateinit var networkForAppointments: NetworkForAppointments
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherAdapter
    private var _binding: FragmentTeacherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())

        networkForAppointments = preferencesManager.getAuthToken()?.let { NetworkForAppointments(it, requireContext()) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePicker()

        fetchData()

        binding.buttonApply.setOnClickListener {

            applyFilters()

        }

        binding.buttonReset.setOnClickListener {
            resetFilters()
        }
    }

    private fun fetchData() {
        recyclerView = binding.reclerviewofallAppointmentsWithteacherDetails// Make sure you have a RecyclerView in your fragment_teacher.xml
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Use Coroutine to call the network method
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Fetch the list of TeacherAppointmentData directly
                val appointments = networkForAppointments.getAllAppointmentsWithTeacherDetails()

                // Initialize and set the adapter
                adapter = TeacherAdapter(appointments,requireContext(),childFragmentManager,this@TeacherFragment)
                recyclerView.adapter = adapter


                Log.d("TeacherAppointments", "Appointments: $appointments")
            } catch (e: Exception) {
                Log.e("TeacherFragment", "Error fetching appointments", e)
            }
        }
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


    private fun applyFilters() {
            val date = binding.etPtmDate.text.toString().takeIf { it.isNotEmpty() }?.let { formatDateToApiFormat(it) }
            val teacherName = binding.searchEditText.text.toString()

            // Check if all parameters are null or empty
            if (date.isNullOrEmpty() && teacherName.isEmpty()) {
                Toast.makeText(requireContext(), "Give data for searching", Toast.LENGTH_SHORT)
                    .show()
                Log.d("AllAppointmentsFragment", "No search data provided.")
                return
            }

        lifecycleScope.launch {
            try {
                val appointments = networkForAppointments.getAllAppointmentsWithTeacherDetailsWithParameters(
                    date,
                    teacherName
                )

                Log.d("AllTeacherDetails", "Appointments received: ${appointments.size}")

                if (appointments.isEmpty()) {
                    Toast.makeText(requireContext(), "No matching data found", Toast.LENGTH_SHORT).show()
                    Log.d("AllTeacherDetails", "No matching data found.")
                }


                val adapter = TeacherAdapter(appointments,requireContext(),childFragmentManager,this@TeacherFragment)
                binding.reclerviewofallAppointmentsWithteacherDetails.layoutManager = LinearLayoutManager(requireContext())
                binding.reclerviewofallAppointmentsWithteacherDetails.adapter = adapter


            } catch (e: Exception) {
                Log.e("AllTeacherDetails", "Failed to apply filters: ${e.message}", e)
                Toast.makeText(requireContext(), "No Match Found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun formatDateToApiFormat(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val dateObj = inputFormat.parse(date)
            outputFormat.format(dateObj ?: Date())
        } catch (e: Exception) {
            Log.e("AllAppointmentsFragment", "Date formatting error: ${e.message}", e)
            ""
        }
    }


    private fun resetFilters() {
        binding.etPtmDate.text.clear() // Clear date input
        binding.searchEditText.text.clear() // Clear child name input
        // Fetch all appointments
        fetchData()
    }


   override fun onDestroyView() {
     super.onDestroyView()
     _binding = null
   }

    override fun onTeacherSwapped() {
        fetchData() // Refresh the data when a teacher swap occurs
    }
}
