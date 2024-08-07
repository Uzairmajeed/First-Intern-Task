package com.facebook.firsttask.admin.user_management

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentAddChildDialogBinding
import kotlinx.coroutines.launch

class AddChildDialogFragment : DialogFragment() {

    private var _binding: FragmentAddChildDialogBinding? = null
    private val binding get() = _binding!!

    private var parentId: Int? = null
    private var parentFirstName: String? = null
    private var parentLastName: String? = null

    private lateinit var networkForUserManagement: NetworkForUserManagement
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        preferencesManager = PreferencesManager(requireContext())
        networkForUserManagement = preferencesManager.getAuthToken()?.let { NetworkForUserManagement(it, requireContext()) }!!

        _binding = FragmentAddChildDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            parentId = it.getInt("parentId")
            parentFirstName = it.getString("parentFirstName")
            parentLastName = it.getString("parentLastName")
        }

        // Update UI with received data
        binding.parentNameView.text = "${parentFirstName.orEmpty()} ${parentLastName.orEmpty()}"


        // Fetch and display wings and groups in spinners
        fetchWings()
        fetchGroups()

        // Set listener for wingSpinner to fetch classes based on selected wing
        binding.wingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedWing = parent.getItemAtPosition(position) as String
                fetchClassesForWing(selectedWing)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected
            }
        }



        binding.saveButton.setOnClickListener {

            dismiss()
        }
    }

    private fun fetchWings() {
        lifecycleScope.launch {
            try {
                val wingNames = networkForUserManagement.getAllWingNames()
                val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, wingNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.wingSpinner.adapter = adapter
            } catch (e: Exception) {
                // Handle exceptions (e.g., network error)
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error fetching wing names", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchGroups() {
        lifecycleScope.launch {
            val groups = networkForUserManagement.getAllGroups()
            val groupNames = groups.map { it.groupName }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, groupNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.groupSpinner.adapter = adapter
        }
    }

    private fun fetchClassesForWing(wingName: String) {
        lifecycleScope.launch {
            try {
                val classes = networkForUserManagement.getAllClasses(wingName)
                val classNames = classes.map { it.className }

                // Check if there are any classes for the selected wing
                if (classNames.isNotEmpty()) {
                    val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, classNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.classSpinner.adapter = adapter
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., network error)
                e.printStackTrace()
                val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, listOf("No Class Name"))
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.classSpinner.adapter = adapter
            }
        }
    }


    override fun onStart() {
        super.onStart()
        getDialog()?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(parentId: Int, parentFirstName: String?, parentLastName: String?): AddChildDialogFragment {
            val fragment = AddChildDialogFragment()
            val args = Bundle().apply {
                putInt("parentId", parentId)
                putString("parentFirstName", parentFirstName)
                putString("parentLastName", parentLastName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
