package com.facebook.firsttask.admin.appointments


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.databinding.FragmentViewAllAppointmentsBinding

class ViewAllAppointmentsFragment : DialogFragment() {

    private var _binding: FragmentViewAllAppointmentsBinding? = null
    private val binding get() = _binding!!

    private var teacherName: String? = null
    private var location: String? = null
    private var date: String? = null
    private var studentName: String? = null
    private var startTime: String? = null
    private var endTime: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewAllAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        teacherName = arguments?.getString("teacherName")
        location = arguments?.getString("location")
        date = arguments?.getString("date")
        studentName = arguments?.getString("studentName")
        startTime = arguments?.getString("startTime")
        endTime = arguments?.getString("endTime")

        // Set values to the views
        binding.teacherNameTextView.text = teacherName
        binding.locationTextView.text = location
        binding.dateTextView.text = date
        binding.studentNameTextView.text = studentName
        binding.TimeTextView.text = "${startTime  + "-" + endTime}"
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
        fun newInstance(
            teacherName: String, location: String, date: String,
            studentName: String, startTime: String, endTime: String
        ): ViewAllAppointmentsFragment {
            val fragment = ViewAllAppointmentsFragment()
            val args = Bundle().apply {
                putString("teacherName", teacherName)
                putString("location", location)
                putString("date", date)
                putString("studentName", studentName)
                putString("startTime", startTime)
                putString("endTime", endTime)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
