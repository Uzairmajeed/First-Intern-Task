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
import com.facebook.firsttask.databinding.FragmentAddParentDialogBinding
import kotlinx.coroutines.launch

class AddParentDialogFragment : DialogFragment() {

    private var _binding: FragmentAddParentDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkForUserManagement: NetworkForUserManagement
    private lateinit var preferencesManager: PreferencesManager

    private var allGroups: List<GroupDataAll>? = null
    private var allClasses: List<ClassData>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        preferencesManager = PreferencesManager(requireContext())
        networkForUserManagement = preferencesManager.getAuthToken()?.let { NetworkForUserManagement(it, requireContext()) }!!

        _binding = FragmentAddParentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            addParent()
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

    private fun addParent() {
        // Collect data from the EditText fields
        val fatherFirstName = binding.firstnamefather.text.toString().trim()
        val fatherLastName = binding.lastnamefather.text.toString().trim()
        val fatherEmail = binding.fatheremail.text.toString().trim()

        val motherFirstName = binding.motherfirstname.text.toString().trim()
        val motherLastName = binding.motherlastname.text.toString().trim()
        val motherEmail = binding.motheremail.text.toString().trim()

        val childFirstName = binding.firstnameEditText.text.toString().trim()
        val childLastName = binding.lastnameEditText.text.toString().trim()
        val childEmail = binding.emailEditText.text.toString().trim()

        // Get selected values from spinners
        val selectedWing = binding.wingSpinner.selectedItem as? String ?: ""
        val selectedClass = binding.classSpinner.selectedItem as? String ?: ""
        val selectedGroups = (binding.groupSpinner.adapter as? CustomSpinnerAdapter)?.getSelectedItems()

        // Find IDs for selected wing, class, and groups
        val classId = getClassIdByName(selectedClass)
        val groupIds = selectedGroups?.mapNotNull { getGroupIdByName(it) }

        // Validate required fields
        if (groupIds != null) {
            if (fatherFirstName.isEmpty() || fatherLastName.isEmpty() || fatherEmail.isEmpty() ||
                motherFirstName.isEmpty() || motherLastName.isEmpty() || motherEmail.isEmpty() ||
                childFirstName.isEmpty() || childLastName.isEmpty() || childEmail.isEmpty() ||
                selectedWing.isEmpty() || classId == null || groupIds.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields and select necessary options", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Create the ChildRequest object if classId and groupIds are not null
        val childRequest = if (classId != null && groupIds != null) {
            ChildRequest(
                firstName = childFirstName,
                lastName = childLastName,
                email = childEmail,
                wing = selectedWing,
                parentId = 0, // Or set to the appropriate parent ID if available
                classId = classId,
                groups = groupIds
            )
        } else {
            null
        }

        // Create the list of ChildRequest objects, filter out null values
        val childrensList = childRequest?.let { listOf(it) } ?: emptyList()

        // Call addNewParent in the network class
        lifecycleScope.launch {
            try {
                val success = networkForUserManagement.addNewParent(
                    fatherFirstName,
                    fatherLastName,
                    fatherEmail,
                    motherFirstName,
                    motherLastName,
                    motherEmail,
                    childrensList
                )

                if (success) {
                    Toast.makeText(requireContext(), "Parent added successfully", Toast.LENGTH_SHORT).show()
                    dismiss() // Dismiss the dialog if successful
                } else {
                    Toast.makeText(requireContext(), "Failed to add parent", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error occurred while adding parent", Toast.LENGTH_SHORT).show()
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        getDialog()?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance(): AddParentDialogFragment {
            return AddParentDialogFragment()
        }
    }
}
