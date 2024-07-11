package com.facebook.firsttask.admin.PTMCreation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.facebook.firsttask.databinding.FragmentOnclickPtmCreationBinding
import kotlinx.coroutines.launch

class OnclickPtmCreation : Fragment() {
    private var _binding: FragmentOnclickPtmCreationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnclickPtmCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        val classList = listOf("1B Sec2", "2A Sec2", "2A Sec3") // Replace with your actual data
        val teacherList = listOf("Mak-doom Sir", "Anwar Sir", "Another Teacher")

        val adapter = ClassAdapter(classList,teacherList)
        binding.classRecyclerView.adapter = adapter
        binding.classRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Retrieve selected wing names from arguments
        val selectedWingNames = arguments?.getStringArrayList("selectedWingNames")
        selectedWingNames?.let { wings ->
            // Log the selected wing names
            wings.forEach { wingName ->
                Log.d("OnclickPtmCreation", "Selected Wing: $wingName")
            }

            // Make the API call to fetch teacher names
            authToken?.let { token ->
                lifecycleScope.launch {
                    val getAllTeacherNames = GetAllTeacherNames(token)
                    val response = getAllTeacherNames.getFromServer(wings)
                    Log.d("Teacher&ClassNamesResponse", response ?: "No response")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}