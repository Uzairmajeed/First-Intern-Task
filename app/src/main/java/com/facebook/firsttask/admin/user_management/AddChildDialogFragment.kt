package com.facebook.firsttask.admin.user_management

import android.R
import android.os.Bundle
import android.util.Log
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

    private var listner : OnMakeChanges? = null

    private var allGroups: List<GroupDataAll>? = null
    private var allClasses: List<ClassData>? = null

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
            addChildren()
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
            try {
                val groups = networkForUserManagement.getAllGroups()
                val groupNames = groups.map { it.groupName }

                // Cache the fetched groups data
                allGroups = groups

                // Initialize a set for selected items, it should be empty initially
                val selectedItems = mutableSetOf<String>()

                Log.d("SelectedGroups", selectedItems.toString())

                // Use the custom adapter
                val adapter = CustomSpinnerAdapter(requireContext(), groupNames, selectedItems)
                binding.groupSpinner.adapter = adapter

                // Set listener to handle item selection
                binding.groupSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        // Handle item selection
                        val selectedItem = adapter.getItem(position) ?: return

                        // Only update selection if item is not already selected
                        if (position >= 0) {
                            adapter.toggleSelection(selectedItem)

                            // Optionally, you can handle other actions here if needed
                            val selectedItemsList = selectedItems.toList()
                            Log.d("SelectedGroups", selectedItemsList.toString())
                            // Use selectedItemsList as needed
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                // Ensure no default item is selected
                binding.groupSpinner.setSelection(-1) // This should ensure no item is selected by default

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error fetching groups", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchClassesForWing(wingName: String) {
        lifecycleScope.launch {
            try {
                val classes = networkForUserManagement.getAllClasses(wingName)
                val classNames = classes.map { it.className }

                // Cache the fetched classes data
                allClasses = classes

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

    private fun getGroupIdByName(groupName: String): Int? {
        return allGroups?.find { it.groupName == groupName }?.id
    }

    private fun getClassIdByName(className: String): Int? {
        return allClasses?.find { it.className == className }?.classId
    }


    private fun addChildren() {
        val firstName = binding.firstnameEditText.text.toString()
        val lastName = binding.lastnameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val selectedWing = binding.wingSpinner.selectedItem?.toString()
        val selectedClass = binding.classSpinner.selectedItem?.toString()
        val selectedGroups = (binding.groupSpinner.adapter as? CustomSpinnerAdapter)?.getSelectedItems()

        // Check if any of the required fields are empty or null
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || selectedWing.isNullOrEmpty() || selectedClass.isNullOrEmpty() || selectedGroups.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val classId = getClassIdByName(selectedClass)
        val groupIds = selectedGroups.mapNotNull { getGroupIdByName(it) }

        lifecycleScope.launch {
            try {
                val response = networkForUserManagement.addChildrenToParent(
                    parentId = parentId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    wing = selectedWing,
                    className = classId,
                    groups = groupIds
                )
                if (response) {
                    Toast.makeText(requireContext(), "Child added successfully", Toast.LENGTH_SHORT).show()
                    listner?.onChange()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to add child", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error adding child", Toast.LENGTH_SHORT).show()
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
        fun newInstance(
            parentId: Int,
            parentFirstName: String?,
            parentLastName: String?,
            listner: OnMakeChanges
        ): AddChildDialogFragment {
            val fragment = AddChildDialogFragment()
            val args = Bundle().apply {
                putInt("parentId", parentId)
                putString("parentFirstName", parentFirstName)
                putString("parentLastName", parentLastName)
            }
            fragment.arguments = args
            fragment.listner = listner
            return fragment
        }
    }
}
