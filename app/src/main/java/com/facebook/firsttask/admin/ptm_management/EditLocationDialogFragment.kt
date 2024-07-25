package com.facebook.firsttask.admin.ptm_management

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.R
import com.facebook.firsttask.admin.dashboard.PTMCreation.nextpage.Location
import com.facebook.firsttask.admin.dashboard.PTMCreation.nextpage.NetworkOperations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditLocationDialogFragment : DialogFragment() {

    private lateinit var networkForPtmManagement: NetworkForPtmManagement

    private lateinit var teacherName: String
    private lateinit var ptmId: String
    private lateinit var teacherID: String

    private var locations: List<Location> = emptyList()
    private lateinit var locationSpinner: Spinner
    private lateinit var preferencesManager: PreferencesManager


    companion object {
        private const val ARG_TEACHER_NAME = "teacher_name"
        private const val ARG_TEACHER_ID = "teacher_id"
        private const val ARG_PTM_ID = "ptm_id"

        fun newInstance(
            teacherName: String?,
            teacherAttId: Int,
            ptmId: String
        ): EditLocationDialogFragment {
            val fragment = EditLocationDialogFragment()
            val args = Bundle()
            args.putString(ARG_TEACHER_NAME, teacherName)
            args.putString(ARG_TEACHER_ID, teacherAttId.toString())
            args.putString(ARG_PTM_ID, ptmId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        val authToken = preferencesManager.getAuthToken()
        networkForPtmManagement = authToken?.let { NetworkForPtmManagement(it, requireContext()) }!!

        teacherName = arguments?.getString(ARG_TEACHER_NAME) ?: ""
        teacherID = arguments?.getString(ARG_TEACHER_ID) ?: ""
        ptmId = arguments?.getString(ARG_PTM_ID) ?: ""

        // Log the received data
        Log.d("EditLocationDialog", "Teacher Name: $teacherName")
        Log.d("EditLocationDialog", "Teacher ID: $teacherID")
        Log.d("EditLocationDialog", "PTM ID: $ptmId")

        fetchLocations()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val teacherNameTextView: TextView = view.findViewById(R.id.teacherNameTextView)
        locationSpinner = view.findViewById(R.id.locationSpinner)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        teacherNameTextView.text = teacherName

        // Initialize the spinner with an empty adapter
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf<String>())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter

        saveButton.setOnClickListener {
            val selectedLocationName = locationSpinner.selectedItem.toString()
            val selectedLocation = locations.find { it.locationName == selectedLocationName }

            selectedLocation?.let { location ->
                lifecycleScope.launch {
                    try {
                        networkForPtmManagement.updateLocationForTeacher(
                            teacherID.toInt(),
                            location.locationId,
                            ptmId.toInt()
                        )
                        dismiss()
                    } catch (e: Exception) {
                        Log.e("UpdateLocationError", "Error updating location", e)
                        showToast("Error updating location: ${e.message}")
                    }
                }
            }
        }
    }

    private fun fetchLocations() {
        val authToken = preferencesManager.getAuthToken()
        val getAllLocations = authToken?.let { NetworkOperations(it, requireContext()) }
        lifecycleScope.launch {
            try {
                locations = getAllLocations?.getAllLocations() ?: emptyList()
                updateLocationSpinner()
            } catch (e: Exception) {
                Log.e("FetchLocationsError", "Error fetching locations", e)
                showToast("Error fetching locations: ${e.message}")
            }
        }
    }

    private fun updateLocationSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations.map { it.locationName })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
