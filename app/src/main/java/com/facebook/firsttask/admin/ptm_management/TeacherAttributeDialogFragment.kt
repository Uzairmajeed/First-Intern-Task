package com.facebook.firsttask.admin.ptm_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class TeacherAttributeDialogFragment : DialogFragment() {

    private lateinit var teacherAttributes: List<TeacherAttribute>
    private lateinit var ptmId: String


    companion object {
        private const val ARG_TEACHER_ATTRIBUTES = "teacher_attributes"
        private const val ARG_PTM_ID = "ptm_id"


        fun newInstance(teacherAttributes: List<TeacherAttribute>, ptmId: Int): TeacherAttributeDialogFragment {
            val fragment = TeacherAttributeDialogFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_TEACHER_ATTRIBUTES, ArrayList(teacherAttributes))
                putString(ARG_PTM_ID, ptmId.toString())
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherAttributes = arguments?.getParcelableArrayList(ARG_TEACHER_ATTRIBUTES) ?: emptyList()
        ptmId = arguments?.getString(ARG_PTM_ID) ?: ""

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_attribute_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewTeacherAttributes)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TeacherAttributeAdapter(teacherAttributes,ptmId,childFragmentManager)
    }
}
