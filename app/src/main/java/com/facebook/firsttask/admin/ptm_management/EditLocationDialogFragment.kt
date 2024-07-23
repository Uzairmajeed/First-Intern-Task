package com.facebook.firsttask.admin.ptm_management

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.facebook.firsttask.R
import com.facebook.firsttask.admin.dashboard.PTMCreation.nextpage.NetworkOperations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditLocationDialogFragment : DialogFragment() {

    private lateinit var teacherName: String
    private var locations: List<String> = emptyList()
    private lateinit var locationSpinner: Spinner

    companion object {
        private const val ARG_TEACHER_NAME = "teacher_name"

        fun newInstance(teacherName: String): EditLocationDialogFragment {
            val fragment = EditLocationDialogFragment()
            val args = Bundle()
            args.putString(ARG_TEACHER_NAME, teacherName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherName = arguments?.getString(ARG_TEACHER_NAME) ?: ""

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
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter

        saveButton.setOnClickListener {
            val selectedLocation = locationSpinner.selectedItem.toString()
            // Handle saving the selected location here
            dismiss()
        }
    }

    private fun fetchLocations() {
        val sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        val getAllLocations = authToken?.let { NetworkOperations(it, requireContext()) }
        lifecycleScope.launch(Dispatchers.Main) {
            locations = getAllLocations?.getAllLocations() ?: emptyList()
            updateLocationSpinner()
        }
    }

    private fun updateLocationSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter
    }
}
