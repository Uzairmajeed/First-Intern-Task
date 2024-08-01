package com.facebook.firsttask.admin.appointments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentSwapTeacherDialogBinding
import kotlinx.coroutines.launch


class SwapTeacherDialogFragment : DialogFragment() {

    private var _binding: FragmentSwapTeacherDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkForAppointments: NetworkForAppointments
    private lateinit var preferencesManager: PreferencesManager

    private var teachersList: List<TeacherDataForSwap> = listOf() // List to hold teachers data

    // Define callback interface
    private var listener: OnTeacherSwappedListener? = null


    // Arguments to pass data to the fragment
    private var ptmid: Int? = null
    private var teacherId: Int? = null



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create dialog here
        return super.onCreateDialog(savedInstanceState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())

        networkForAppointments = preferencesManager.getAuthToken()?.let { NetworkForAppointments(it, requireContext()) }!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSwapTeacherDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views and set up listeners
        // Use ptmid and teacherId as needed
        // Retrieve arguments
        ptmid = arguments?.getInt("ptmid")
        teacherId = arguments?.getInt("teacherId")

        // Fetch teacher options and set them in the Spinner
        lifecycleScope.launch {
            try {
                val teachersResponse = networkForAppointments.getAllTeachersForSwap(ptmid!!, teacherId!!)
                teachersList = teachersResponse

                val teacherNames = teachersList.map { it.teacherName }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, teacherNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.selectTeacher.adapter = adapter
            } catch (e: Exception) {
                Log.e("SwapTeacherDialogFragment", "Error fetching teachers", e)
            }
        }

        binding.swapButton.setOnClickListener {
            val selectedTeacherName = binding.selectTeacher.selectedItem as? String
            val selectedTeacher = teachersList.find { it.teacherName == selectedTeacherName }

            if (selectedTeacher != null) {
                val newTeacherId = selectedTeacher.teacherId

                lifecycleScope.launch {
                    try {
                        networkForAppointments.swapTeacher(teacherId!!, newTeacherId, ptmid!!)
                        listener?.onTeacherSwapped() // Notify the listener
                        dismiss()
                    } catch (e: Exception) {
                        Log.e("SwapTeacherDialogFragment", "Error swapping teacher", e)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please select a teacher", Toast.LENGTH_SHORT).show()
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
        fun newInstance(ptmid: Int, teacherId: Int, listener: OnTeacherSwappedListener): SwapTeacherDialogFragment {
            val fragment = SwapTeacherDialogFragment()
            val args = Bundle().apply {
                putInt("ptmid", ptmid)
                putInt("teacherId", teacherId)
            }
            fragment.arguments = args
            fragment.listener = listener // Pass the listener

            return fragment
        }
    }
}
