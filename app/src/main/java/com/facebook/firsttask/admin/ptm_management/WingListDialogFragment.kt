package com.facebook.firsttask.admin.ptm_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.databinding.FragmentWingListDialogBinding

class WingListDialogFragment : DialogFragment() {

    private var _binding: FragmentWingListDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var wingNames: List<String>

    companion object {
        private const val ARG_WING_NAMES = "wing_names"

        fun newInstance(wingNames: List<String>): WingListDialogFragment {
            val fragment = WingListDialogFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_WING_NAMES, ArrayList(wingNames))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWingListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wingNames = arguments?.getStringArrayList(ARG_WING_NAMES) ?: emptyList()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, wingNames)
        binding.wingListView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
