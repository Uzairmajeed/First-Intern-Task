package com.facebook.firsttask.admin.user_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.R
import com.facebook.firsttask.admin.appointments.NetworkForAppointments
import com.facebook.firsttask.databinding.FragmentParentsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParentsFragment : Fragment() {

    private var _binding: FragmentParentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GetALLParentsAdapter

    private lateinit var networkForUserManagement: NetworkForUserManagement
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        preferencesManager = PreferencesManager(requireContext())

        networkForUserManagement = preferencesManager.getAuthToken()?.let { NetworkForUserManagement(it, requireContext()) }!!

        _binding = FragmentParentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reclerviewofallparents.layoutManager = LinearLayoutManager(requireContext())

        fetchParents()
    }

    private fun fetchParents() {
        CoroutineScope(Dispatchers.IO).launch {
            val parentData = networkForUserManagement.getAllParents() // Call your API method

            withContext(Dispatchers.Main) {
                adapter = GetALLParentsAdapter(parentData)
                binding.reclerviewofallparents.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}