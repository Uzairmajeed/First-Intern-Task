package com.facebook.firsttask.admin.user_management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.databinding.ItemGetallparentsBinding

class GetALLParentsAdapter(
    private var parents: List<ParentData>,
    private val fragmentManager: FragmentManager,
    private val listner: OnMakeChanges
) : RecyclerView.Adapter<GetALLParentsAdapter.ParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val binding = ItemGetallparentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parent = parents[position]
        with(holder.binding) {
            idView.text = parent.id.toString()
            parentView.text = "${parent.firstName}${parent.lastName}\n${parent.firstName2}${parent.lastName2}"
            emailView.text =  "${parent.email}\n${parent.email2}"
            childrenView.text = parent.childrens.size.toString()

            dropdownButton.setOnClickListener {
                val dialogFragment = ChildDataDialogFragment.newInstance(
                    "${parent.firstName} ${parent.lastName}",
                    parent.childrens,parent.id,listner
                )
                dialogFragment.show(fragmentManager, "ChildDataDialogFragment")
            }

            editparentButton.setOnClickListener {
                val editDialogFragment = EditParentDialogFragment.newInstance(
                    parent.id,
                    parent.firstName,
                    parent.lastName,
                    parent.firstName2,
                    parent.lastName2,
                    listner
                )
                editDialogFragment.show(fragmentManager, "EditParentDialogFragment")
            }


            addchildrenButton.setOnClickListener {
                val addChildDialogFragment = AddChildDialogFragment.newInstance(
                    parent.id,
                    parent.firstName,
                    parent.lastName
                )
                addChildDialogFragment.show(fragmentManager, "AddChildDialogFragment")
            }

        }
    }

    fun updateData(newParents: List<ParentData>) {
        parents = newParents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = parents.size

    class ParentViewHolder(val binding: ItemGetallparentsBinding) : RecyclerView.ViewHolder(binding.root)
}
