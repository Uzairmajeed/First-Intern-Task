package com.facebook.firsttask.admin.PTMCreation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.FragmentPtmCreationBinding
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
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

        binding.nextButton.setOnClickListener {
            val onclickPtmCreation = OnclickPtmCreation()
            // Assuming you are calling this from within a Fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, onclickPtmCreation) // R.id.fragment_container is the ID of the container layout where fragments are placed
                .addToBackStack(null) // This adds the transaction to the back stack, allowing users to navigate back
                .commit()
        }

        getAllWings()
    }

    private  fun getAllWings(){

        val sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)
        val schoolWings = authToken?.let { it1 -> GetAllSchoolWings(it1) }
        lifecycleScope.launch {
            val response = schoolWings?.getFromServer()
            response?.let { parseAndDisplayWings(it) }
        }
    }

    private fun parseAndDisplayWings(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val dataArray: JSONArray = jsonObject.getJSONArray("data")
            val checkboxContainer = binding.checkboxContainer

            // Clear any existing checkboxes
            checkboxContainer.removeAllViews()

            val checkBoxList = mutableListOf<CheckBox>()

            for (i in 0 until dataArray.length()) {
                val wingObject = dataArray.getJSONObject(i)
                val wingName = wingObject.getString("wingName")

                val checkBox = CheckBox(context).apply {
                    text = wingName
                    id = View.generateViewId()
                }

                checkboxContainer.addView(checkBox)
                checkBoxList.add(checkBox)
            }

            // Set up the main checkbox to check/uncheck all other checkboxes
            val checkBoxMain = binding.checkBoxMain
            checkBoxMain.setOnCheckedChangeListener { _, isChecked ->
                checkBoxList.forEach { it.isChecked = isChecked }
            }

        } catch (e: Exception) {
            e.printStackTrace()
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