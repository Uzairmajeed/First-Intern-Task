package com.facebook.firsttask.admin.PTMCreation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.databinding.FragmentOnclickPtmCreationBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

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

        // Retrieve selected wing names from arguments
        val selectedWingNames = arguments?.getStringArrayList("selectedWingNames")

        selectedWingNames?.let { wings ->
            authToken?.let { token ->
                lifecycleScope.launch {
                    val getAllTeacherNames = GetAllTeacherNames(token, requireContext())
                    val response = getAllTeacherNames.getFromServer(wings)
                    Log.d("Teacher&ClassNamesResponse", response ?: "No response")

                    if (response != null) {
                        val (classList, teacherList) = parseResponse(response)

                        if (classList.isNotEmpty() && teacherList.isNotEmpty()) {
                            val adapter = ClassAdapter(classList, teacherList)
                            binding.classRecyclerView.adapter = adapter
                            binding.classRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        } else {
                            showToast("No data for selected wings.")
                        }
                    } else {
                        showToast("No data for selected wings.")
                    }
                }
            }
        }

        // Button click listeners
        binding.backButton.setOnClickListener {
            // Handle back button action
            requireActivity().onBackPressed()
        }

        binding.createPtmButton.setOnClickListener {
            // Handle create PTM button action
            // Implement your functionality here
        }

    }

    private fun parseResponse(response: String): Pair<List<String>, List<String>> {
        val classList = mutableListOf<String>()
        val teacherList = mutableListOf<String>()

        val jsonObject = JSONObject(response)
        val dataArray = if (jsonObject.has("data")) jsonObject.getJSONArray("data") else null

        if (dataArray != null) {
            for (i in 0 until dataArray.length()) {
                val classObject = dataArray.getJSONObject(i)
                val className = classObject.getString("className")
                classList.add(className)

                val teacherArray = classObject.getJSONArray("teacherVm")
                for (j in 0 until teacherArray.length()) {
                    val teacherObject = teacherArray.getJSONObject(j)
                    val teacherName = teacherObject.getString("teacherName")
                    teacherList.add(teacherName)
                }
            }
        }

        return Pair(classList, teacherList)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
