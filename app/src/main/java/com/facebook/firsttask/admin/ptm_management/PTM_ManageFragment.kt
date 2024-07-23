package com.facebook.firsttask.admin.ptm_management

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.FragmentPTMManageBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PTM_ManageFragment : Fragment() {

    private lateinit var networkForPtmManagement: NetworkForPtmManagement
    private var _binding: FragmentPTMManageBinding? = null
    private val binding get() = _binding!!
    private lateinit var ptmAdapter: GetAllPtmForLocation_Adpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)
        networkForPtmManagement = authToken?.let { NetworkForPtmManagement(it,requireContext()) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPTMManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getallotmdatesforlocation.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = networkForPtmManagement.getAllPtmDatesForLocation()
                response?.data?.let { ptmDataList ->
                    withContext(Dispatchers.Main) {
                        ptmAdapter = GetAllPtmForLocation_Adpater(ptmDataList,childFragmentManager)
                        binding.getallotmdatesforlocation.adapter = ptmAdapter
                    }
                } ?: run {
                    Log.e("PTM_ManageFragment", "Response is null or empty")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("PTM_ManageFragment", "Error fetching PTM dates", e)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


