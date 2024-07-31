package com.facebook.firsttask.admin.appointments

import android.R
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.databinding.FragmentSwapTeacherDialogBinding
import org.jetbrains.annotations.Nullable


class SwapTeacherDialogFragment : DialogFragment() {

    private var _binding: FragmentSwapTeacherDialogBinding? = null
    private val binding get() = _binding!!

    // Arguments to pass data to the fragment
    private var ptmid: Int? = null
    private var teacherId: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create dialog here
        return super.onCreateDialog(savedInstanceState)

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
        fun newInstance(ptmid: Int, teacherId: Int): SwapTeacherDialogFragment {
            val fragment = SwapTeacherDialogFragment()
            val args = Bundle().apply {
                putInt("ptmid", ptmid)
                putInt("teacherId", teacherId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
