package com.facebook.firsttask.admin.user_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentEditChildDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditChildDialogFragment : DialogFragment() {

    private var _binding: FragmentEditChildDialogBinding? = null
    private val binding get() = _binding!!

    private var childFirstName: String? = null
    private var childLastName: String? = null
    private var parentName: String? = null
    private var parentId: Int? = null

    private lateinit var networkForUserManagement: NetworkForUserManagement
    private lateinit var preferencesManager: PreferencesManager

    // Define callback interface
    private var listener: OnMakeChanges? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            childFirstName = it.getString("CHILD_FIRST_NAME")
            childLastName = it.getString("CHILD_LAST_NAME")
            parentName = it.getString("PARENT_NAME")
            parentId = it.getInt("PARENT_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        preferencesManager = PreferencesManager(requireContext())
        networkForUserManagement = preferencesManager.getAuthToken()?.let { NetworkForUserManagement(it, requireContext()) }!!

        _binding = FragmentEditChildDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.parentNameTextView.text = "Parent Name : ${parentName} "
        binding.firstNameEditText.setText(childFirstName)
        binding.lastNameEditText.setText(childLastName)
        // Use parentName and parentId as needed

        binding.saveButton.setOnClickListener {
            // Handle save logic

            val updatedFirstName = binding.firstNameEditText.text.toString()
            val updatedLastName = binding.lastNameEditText.text.toString()


            CoroutineScope(Dispatchers.IO).launch {
                val success = networkForUserManagement.updateChild(
                    parentId,
                    updatedFirstName,
                    updatedLastName
                )
                if (success) {
                    withContext(Dispatchers.Main) {
                        listener?.onChange() // Notify listener about the change
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getDialog()?.getWindow()?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(
            childFirstName: String,
            childLastName: String,
            parentName: String,
            parentId: Int,
            childDataDialoglistner: OnMakeChanges
        ) :EditChildDialogFragment {
            val fragment = EditChildDialogFragment()
               val  args = Bundle().apply {
                    putString("CHILD_FIRST_NAME", childFirstName)
                    putString("CHILD_LAST_NAME", childLastName)
                    putString("PARENT_NAME", parentName)
                    putInt("PARENT_ID", parentId)
                }
            fragment.arguments=args
            fragment.listener = childDataDialoglistner
            return fragment
        }
    }
}
