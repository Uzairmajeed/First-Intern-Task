package com.facebook.firsttask.admin.PTMCreation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.R
import com.facebook.firsttask.admin.PTMCreation.nextpage.OnclickPtmCreation
import com.facebook.firsttask.databinding.FragmentPtmCreationBinding
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PtmCreationFragment : Fragment() {

    private var _binding: FragmentPtmCreationBinding? = null
    private val binding get() = _binding!!
    // Define a list to hold time selections
    private val timeSelections = mutableListOf<TimeSelection>()
    private lateinit var timeSelectionAdapter: TimeSelectionAdapter
    //private val timeSelections = mutableListOf(TimeSelection("", "")) // Initialize with one item

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
            val todayYear = calendar.get(Calendar.YEAR)
            val todayMonth = calendar.get(Calendar.MONTH)
            val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Convert selected date to Calendar instance
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    // Compare selected date with today's date
                    if (selectedCalendar.before(calendar)) {
                        Toast.makeText(requireContext(), "Please select a future date.", Toast.LENGTH_SHORT).show()
                    } else {
                        val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                        binding.etPtmDate.setText(selectedDate)
                    }
                },
                todayYear, todayMonth, todayDay
            )

            // Set minimum date in DatePickerDialog to today's date
            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            datePickerDialog.show()
        }


        binding.nextButton.setOnClickListener {
            // Get selected wing names
            val selectedWingNames = getSelectedWingNames()

            // Get selected spinner values
            val selectedDuration = binding.durationSpinner.selectedItem?.toString()
            val selectedStartTime = binding.starttimeSpinner.selectedItem?.toString()
            val selectedEndTime = binding.endtimespinnerSpinner.selectedItem?.toString()

            // Get the text from the EditText
            val ptmDate = binding.etPtmDate.text.toString()

            // Get the states of the CheckBoxes
            val isOnlineChecked = binding.onlineCheckBox.isChecked
            val isOfflineChecked = binding.offlineCheckBox.isChecked

            // Check if at least one checkbox is checked
            if (!isOnlineChecked && !isOfflineChecked) {
                Toast.makeText(requireContext(), "Please select at least one checkbox.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Exit early if no checkbox is selected
            }

            // Check if all required fields are filled
            when {
                ptmDate.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please select a PTM date.", Toast.LENGTH_SHORT).show()
                }
                selectedDuration.isNullOrEmpty() || selectedDuration == "Select Duration" -> {
                    Toast.makeText(requireContext(), "Please select a duration.", Toast.LENGTH_SHORT).show()
                }
                selectedStartTime.isNullOrEmpty() || selectedStartTime == "Select Start Time" -> {
                    Toast.makeText(requireContext(), "Please select a start time.", Toast.LENGTH_SHORT).show()
                }
                selectedEndTime.isNullOrEmpty() || selectedEndTime == "Select End Time" -> {
                    Toast.makeText(requireContext(), "Please select an end time.", Toast.LENGTH_SHORT).show()
                }
                selectedWingNames.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please select at least one wing.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Log the selected spinner values and additional data
                    val logMessage = "Selected Wing Names: $selectedWingNames\n" +
                            "Selected Duration: $selectedDuration\n" +
                            "Selected Start Time: $selectedStartTime\n" +
                            "Selected End Time: $selectedEndTime\n" +
                            "PTM Date: $ptmDate\n" +
                            "Online Checked: $isOnlineChecked\n" +
                            "Offline Checked: $isOfflineChecked"
                    Log.d("PtmCreationFragment", logMessage)

                    // Collect data from TimeSelectionAdapter
                    val timeSelectionData = timeSelectionAdapter.getTimeSelectionData()


                    // Create a bundle to pass the selected data
                    val bundle = Bundle().apply {
                        putStringArrayList("selectedWingNames", ArrayList(selectedWingNames))
                        putString("selectedDuration", selectedDuration)
                        putString("selectedStartTime", selectedStartTime)
                        putString("selectedEndTime", selectedEndTime)
                        putString("ptmDate", ptmDate)
                        putBoolean("isOnlineChecked", isOnlineChecked)
                        putBoolean("isOfflineChecked", isOfflineChecked)
                        putParcelableArrayList("timeSelections", ArrayList<Parcelable>(timeSelectionData))
                    }

                    // Create an instance of the next fragment
                    val onclickPtmCreation = OnclickPtmCreation()
                    onclickPtmCreation.arguments = bundle

                    // Replace the current fragment with the next fragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, onclickPtmCreation)
                        .addToBackStack(null)
                        .commit()
                }
            }
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

    private fun getSelectedWingNames(): List<String> {
        val selectedWingNames = mutableListOf<String>()
        for (i in 0 until binding.checkboxContainer.childCount) {
            val checkBox = binding.checkboxContainer.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                selectedWingNames.add(checkBox.text.toString())
            }
        }
        return selectedWingNames
    }

    private fun addTimeSelection() {
        timeSelections.add(TimeSelection("null", "null"))
        timeSelectionAdapter.notifyDataSetChanged()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}