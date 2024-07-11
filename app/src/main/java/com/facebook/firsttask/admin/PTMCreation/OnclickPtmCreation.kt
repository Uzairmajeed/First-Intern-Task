package com.facebook.firsttask.admin.PTMCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.FragmentOnclickPtmCreationBinding


class OnclickPtmCreation : Fragment() {
    private var _binding: FragmentOnclickPtmCreationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnclickPtmCreationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val classList = listOf("1B Sec2", "2A Sec2", "2A Sec3") // Replace with your actual data

        val adapter = ClassAdapter(classList)
        binding.classRecyclerView.adapter = adapter
        binding.classRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}