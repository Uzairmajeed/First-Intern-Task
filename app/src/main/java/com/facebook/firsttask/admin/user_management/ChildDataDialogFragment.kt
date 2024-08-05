package com.facebook.firsttask.admin.user_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.facebook.firsttask.databinding.FragmentChildDataDialogBinding

class ChildDataDialogFragment : DialogFragment() {

    private var _binding: FragmentChildDataDialogBinding? = null
    private val binding get() = _binding!!

    private var parentName: String? = null
    private var childData: List<ChildData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parentName = it.getString("PARENT_NAME")
            childData = it.getParcelableArrayList("CHILD_DATA")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChildDataDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up RecyclerView or other UI elements with childData
        val adapter = ChildDataAdapter(childData ?: emptyList(),parentName,childFragmentManager)
        binding.reclerviewofchilddata.adapter = adapter
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
        fun newInstance(parentName: String, childData: List<ChildData>) =
            ChildDataDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("PARENT_NAME", parentName)
                    putParcelableArrayList("CHILD_DATA", ArrayList(childData))
                }
            }
    }
}
