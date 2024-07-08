package com.facebook.firsttask.admin.Ptmcreation

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.FragmentPtmCreationBinding
import java.util.Calendar

class PtmCreationFragment : Fragment() {

    private var _binding: FragmentPtmCreationBinding? = null
    private val binding get() = _binding!!
    private lateinit var timeSelectionAdapter: TimeSelectionAdapter
    private val timeSelections = mutableListOf(TimeSelection("", "")) // Initialize with one item

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPtmCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        timeSelectionAdapter = TimeSelectionAdapter(timeSelections)
        binding.timeSelectionRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.timeSelectionRecyclerView.adapter = timeSelectionAdapter

        // Handle add button click
        binding.addTimeButton.setOnClickListener {
            addTimeSelection()
        }



        val startTimeSpinner = binding.starttimeSpinner
        val endTimeSpinner = binding.endtimespinnerSpinner

        // Time options
        val timeOptions = arrayOf(
            "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM",
            "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
            "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM",
            "12:00 AM"
        )

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adaptertime: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            timeOptions
        )

// Specify the layout to use when the list of choices appears
        adaptertime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the spinners
        startTimeSpinner.adapter = adaptertime
        endTimeSpinner.adapter = adaptertime


        val spinner = binding.durationSpinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adaptermeeting: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meeting_duration_array,
            android.R.layout.simple_spinner_item
        )

// Specify the layout to use when the list of choices appears
        adaptermeeting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the spinner
        spinner.adapter = adaptermeeting

        binding.btnCalendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                    binding.etPtmDate.setText(selectedDate)
                },
                year, month, day
            )

            datePickerDialog.show()
        }

    }

    private fun addTimeSelection() {
        timeSelections.add(TimeSelection("", ""))
        timeSelectionAdapter.notifyDataSetChanged()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}