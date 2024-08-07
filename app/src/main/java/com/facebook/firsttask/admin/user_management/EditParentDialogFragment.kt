package com.facebook.firsttask.admin.user_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentEditParentDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditParentDialogFragment : DialogFragment() {

    private var _binding: FragmentEditParentDialogBinding? = null
    private val binding get() = _binding!!

    private var parentId: Int? = null
    private var firstName1: String? = null
    private var lastName1: String? = null
    private var firstName2: String? = null
    private var lastName2: String? = null

    private lateinit var networkForUserManagement: NetworkForUserManagement
    private lateinit var preferencesManager: PreferencesManager

    // Define callback interface
    private var listener: OnMakeChanges? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        preferencesManager = PreferencesManager(requireContext())
        networkForUserManagement = preferencesManager.getAuthToken()?.let { NetworkForUserManagement(it, requireContext()) }!!

        _binding = FragmentEditParentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        arguments?.let {
            parentId = it.getInt("parentId")
            firstName1 = it.getString("firstName1")
            lastName1 = it.getString("lastName1")
            firstName2 = it.getString("firstName2")
            lastName2 = it.getString("lastName2")
        }


        binding.fathernameview.text = "${firstName1} ${lastName1}"
        binding.mothernameview.text = "${firstName2} ${lastName2}"

        // Initialize views with current data
        binding.firstName1EditText.setText(firstName1)
        binding.lastName1EditText.setText(lastName1)
        binding.firstName2EditText.setText(firstName2)
        binding.lastName2EditText.setText(lastName2)

        binding.saveButton.setOnClickListener {
            // Handle save action here
            val fatherFirstName = binding.firstName1EditText.text.toString()
            val fatherLastName = binding.lastName1EditText.text.toString()
            val motherFirstName = binding.firstName2EditText.text.toString()
            val motherLastName = binding.lastName2EditText.text.toString()


            CoroutineScope(Dispatchers.IO).launch {
                val success = networkForUserManagement.updateParent(
                    parentId,
                    fatherFirstName,
                    fatherLastName,
                    motherFirstName,
                    motherLastName,
                )
                if (success) {
                    withContext(Dispatchers.Main) {
                        listener?.onChange()
                        dismiss()
                    }
                }
            }
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance(
            parentId: Int,
            firstName1: String?,
            lastName1: String?,
            firstName2: String?,
            lastName2: String?,
            listner: OnMakeChanges
        ): EditParentDialogFragment {
            val fragment = EditParentDialogFragment()
            val args = Bundle().apply {
                putInt("parentId", parentId)
                putString("firstName1", firstName1)
                putString("lastName1", lastName1)
                putString("firstName2", firstName2)
                putString("lastName2", lastName2)
            }
            fragment.arguments = args
            fragment.listener = listner
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
