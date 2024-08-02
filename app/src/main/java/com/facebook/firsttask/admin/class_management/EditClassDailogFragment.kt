package com.facebook.firsttask.admin.class_management

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentEditClassDialogBinding
import kotlinx.coroutines.launch

class EditClassDialogFragment : DialogFragment() {

    private var _binding: FragmentEditClassDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkForClassManage: NetworkForClassManagement
    private lateinit var preferencesManager: PreferencesManager

    private var className: String? = null
    private var sectionName: String? = null
    private var classId: Int? = null

    private var listner: OnUpdatedCLassNameListner? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())

        networkForClassManage = preferencesManager.getAuthToken()?.let {
            NetworkForClassManagement(it, requireContext())
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditClassDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        className = arguments?.getString("className")
        sectionName = arguments?.getString("sectionName")
        classId = arguments?.getInt("classId")

        // Use the data as needed

        binding.classNameEditText.setText(className)
        binding.sectionNameEditText.setText(sectionName)

        binding.updateButton.setOnClickListener {

            val newid = classId
            val newClassName = binding.classNameEditText.text.toString()
            val newSectionName = binding.sectionNameEditText.text.toString()

            lifecycleScope.launch {
                try {
                    networkForClassManage.updateClassName(newid!!, newClassName!!, newSectionName!!)
                    listner?.onUpdatedClass()
                    dismiss()
                } catch (e: Exception) {
                    Log.e("EditClassDialogFragment", "Error updating classname", e)
                }
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
            className: String,
            sectionName: String,
            classId: Int,
            listner: OnUpdatedCLassNameListner
        ): EditClassDialogFragment {
            val fragment = EditClassDialogFragment()
            val args = Bundle().apply {
                putString("className", className)
                putString("sectionName", sectionName)
                putInt("classId", classId)
            }
            fragment.arguments = args
            fragment.listner = listner
            return fragment
        }
    }
}
