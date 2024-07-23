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

    companion object {
        private const val ARG_TEACHER_ATTRIBUTES = "teacher_attributes"

        fun newInstance(teacherAttributes: List<TeacherAttribute>): TeacherAttributeDialogFragment {
            val fragment = TeacherAttributeDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_TEACHER_ATTRIBUTES, ArrayList(teacherAttributes))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacherAttributes = arguments?.getParcelableArrayList(ARG_TEACHER_ATTRIBUTES) ?: emptyList()
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
        recyclerView.adapter = TeacherAttributeAdapter(teacherAttributes,childFragmentManager)
    }
}
