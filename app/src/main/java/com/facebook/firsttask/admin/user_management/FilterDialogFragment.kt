package com.facebook.firsttask.admin.user_management

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.databinding.DialogFilterBinding

class FilterDialogFragment : DialogFragment() {

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    // Define a callback interface
    interface FilterListener {
        fun onFilterApplied(isActive: Boolean?, isInactive: Boolean?)
    }

    private var filterListener: FilterListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("Filter Options")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.applyButton.setOnClickListener {
            val isActive = binding.checkboxActive.isChecked
            val isInactive = binding.checkboxInactive.isChecked
            filterListener?.onFilterApplied(isActive, isInactive)
            dismiss()
        }

        binding.clearButton.setOnClickListener {
            filterListener?.onFilterApplied(null, null) // Clear filters
            dismiss()
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

    fun setFilterListener(listener: FilterListener) {
        filterListener = listener
    }
}
