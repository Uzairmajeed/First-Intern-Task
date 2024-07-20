package com.facebook.firsttask.admin.PTMCreation.nextpage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.admin.PTMCreation.TimeSelection
import com.facebook.firsttask.databinding.FragmentOnclickPtmCreationBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OnclickPtmCreation : Fragment() {
    private var _binding: FragmentOnclickPtmCreationBinding? = null
    private val binding get() = _binding!!
    private lateinit var classAdapter: ClassAdapter

    private  var classList = mutableListOf<ClassData>()
    private var teacherList = mutableListOf<TeacherData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnclickPtmCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        // Retrieve selected wing names from arguments
        val selectedWingNames = arguments?.getStringArrayList("selectedWingNames")
        val selectedWingIds = arguments?.getStringArrayList("selectedWingIds")
        val selectedDuration = arguments?.getString("selectedDuration")
        val selectedStartTime = arguments?.getString("selectedStartTime")
        val selectedEndTime = arguments?.getString("selectedEndTime")
        val ptmDate = arguments?.getString("ptmDate")
        val isOnlineChecked = arguments?.getBoolean("isOnlineChecked", false)
        val isOfflineChecked = arguments?.getBoolean("isOfflineChecked", false)
        // Retrieve time selections from arguments
        val timeSelections = arguments?.getParcelableArrayList<TimeSelection>("timeSelections")

        // Log the retrieved time selections
        timeSelections?.forEachIndexed { index, timeSelection ->
            Log.d("OnclickPtmCreation", "Time Selection $index:")
            Log.d("OnclickPtmCreation", "  Start Time: ${timeSelection.startTime}")
            Log.d("OnclickPtmCreation", "  End Time: ${timeSelection.endTime}")
        }

        Log.d("OnclickPtmCreation", "Selected Wing Names: $selectedWingNames")
        Log.d("OnclickPtmCreation", "Selected Wing Ids: $selectedWingIds")
        Log.d("OnclickPtmCreation", "Selected Duration: $selectedDuration")
        Log.d("OnclickPtmCreation", "Selected Start Time: $selectedStartTime")
        Log.d("OnclickPtmCreation", "Selected End Time: $selectedEndTime")
        Log.d("OnclickPtmCreation", "PTM Date: $ptmDate")
        Log.d("OnclickPtmCreation", "Online Checked: $isOnlineChecked")
        Log.d("OnclickPtmCreation", "Offline Checked: $isOfflineChecked")

        // Convert selected start and end time to Date objects
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val startDate = sdf.parse(selectedStartTime!!)
        val endDate = sdf.parse(selectedEndTime!!)

        // Calculate the list of times based on duration
        val timeList = generateTimeSlots(startDate, endDate, selectedDuration!!)

        // Log the generated time list
        Log.d("GeneratedTimeList", timeList.toString())


        selectedWingNames?.let { wings ->
            authToken?.let { token ->
                lifecycleScope.launch {
                    val recyclerView = binding.classRecyclerView
                    val getAllTeacherNames = NetworkOperations(token, requireContext())
                    val response = getAllTeacherNames.getFromServer(wings)
                    Log.d("Teacher&ClassNamesResponse", response ?: "No response")

                    // Fetch locations
                    val locationList = getAllTeacherNames.getAllLocations()

                    if (response != null) {
                        val (classList, teacherList) = parseResponse(response)

                        if (locationList != null && classList.isNotEmpty() && teacherList.isNotEmpty()) {
                            classAdapter = ClassAdapter(recyclerView,classList, teacherList, timeList, locationList)
                            recyclerView.adapter = classAdapter
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        } else {
                            showToast("No data for selected wings.")
                        }
                    } else {
                        showToast("No data for selected wings.")
                    }
                }
            }
        }

        // Button click listeners
        binding.backButton.setOnClickListener {
            // Handle back button action
            requireActivity().onBackPressed()
        }

        binding.createPtmButton.setOnClickListener {

            val selectedItems = classAdapter.getSelectedItems()

            if (selectedItems.isNullOrEmpty()) {
                Toast.makeText(context, "Select Class,Teacher & FreezSlots", Toast.LENGTH_SHORT).show()
            } else {
                val selectedItemsWithIds = mutableListOf<SelectedItemWithIds>()

                for (item in selectedItems) {
                    val classId = classList.find { it.className == item.className }?.classId ?: -1
                    val teacherId = teacherList.find { it.teacherName == item.teacherName }?.teacherId ?: -1
                    val locationId = teacherList.find { it.teacherName == item.teacherName }?.locationId ?: -1
                    // Extract startTime and endTime from selectedTimes
                    val startTime = item.selectedTimes.firstOrNull() ?: ""
                    val endTime = item.selectedTimes.lastOrNull() ?: ""

                    if (classId != -1 && teacherId != -1 && locationId != -1) {
                        selectedItemsWithIds.add(
                            SelectedItemWithIds(
                                classId = classId,
                                teacherId = teacherId,
                                locationId = locationId,
                                startTime = startTime,
                                endTime = endTime,
                                selectedTimes = item.selectedTimes
                            )
                        )
                    } else {
                        Log.e("Error", "Matching ID not found for class: ${item.className}, teacher: ${item.teacherName}, location: ${item.location}")
                    }
                }
                // Log the selected items with IDs
                for (item in selectedItemsWithIds) {
                    Log.d("SelectedItemsWithIds", "ClassId: ${item.classId}, TeacherId: ${item.teacherId}, LocationId: ${item.locationId}, Times: ${item.selectedTimes}")
                }

                // Call the network function
                lifecycleScope.launch {
                    try {
                        if (authToken != null) {
                            NetworkOperations(authToken,requireContext()).createPTM(
                                selectedItemsWithIds,
                                selectedWingIds,
                                selectedDuration,
                                selectedStartTime,
                                selectedEndTime,
                                ptmDate,
                                isOnlineChecked,
                                isOfflineChecked,
                                timeSelections
                            )
                        }
                        // Handle success (e.g., show a success message or navigate to another screen)
                    } catch (e: Exception) {
                        // Handle error (e.g., show an error message)
                    }
                }

                
            }
        }

    }

    private fun parseResponse(response: String): Pair<List<ClassData>, List<TeacherData>> {
         classList = mutableListOf()
         teacherList = mutableListOf()

        val jsonObject = JSONObject(response)
        val dataArray = if (jsonObject.has("data")) jsonObject.getJSONArray("data") else null

        if (dataArray != null) {
            for (i in 0 until dataArray.length()) {
                val classObject = dataArray.getJSONObject(i)
                val classId = classObject.getInt("classId")
                val className = classObject.getString("className")
                classList.add(ClassData(classId, className))

                val teacherArray = classObject.getJSONArray("teacherVm")
                for (j in 0 until teacherArray.length()) {
                    val teacherObject = teacherArray.getJSONObject(j)
                    val teacherId = teacherObject.getInt("teacherId")
                    val teacherName = teacherObject.getString("teacherName")
                    val locationId = teacherObject.getInt("locationId")
                    val teacherLocation = teacherObject.optString("teacherLocation", "")
                    teacherList.add(TeacherData(teacherId, teacherName, locationId, teacherLocation))
                }
            }
        }
        // Log classList and teacherList
        Log.d("ParsedResponse", "Class List: $classList")
        Log.d("ParsedResponse", "Teacher List: $teacherList")

        return Pair(classList, teacherList)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun generateTimeSlots(startDate: Date, endDate: Date, durationString: String): List<String> {
        // Parse duration string to get minutes
        val duration = parseDuration(durationString)

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeList = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        // Add start time
        timeList.add(sdf.format(startDate))

        // Calculate time slots until end time
        while (calendar.time.before(endDate)) {
            calendar.add(Calendar.MINUTE, duration)
            if (calendar.time.before(endDate) || calendar.time == endDate) {
                timeList.add(sdf.format(calendar.time))
            }
        }

        // Convert all times to uppercase AM/PM
        return timeList.map { time ->
            time.replace("am", "AM").replace("pm", "PM")
        }
    }


    private fun parseDuration(durationString: String): Int {
        // Extract numeric part from the duration string and parse it
        val durationInMinutes = durationString.split(" ")[0].toInt()
        return durationInMinutes
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

