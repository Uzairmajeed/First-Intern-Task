package com.facebook.firsttask.admin.user_management

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentParentsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParentsFragment : Fragment(),OnMakeChanges {

    private var _binding: FragmentParentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GetALLParentsAdapter

    private lateinit var networkForUserManagement: NetworkForUserManagement
    private lateinit var preferencesManager: PreferencesManager
    private var parentData: List<ParentData> = emptyList()


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

        // Setup search bar listener
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the list based on the search query
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        val filteredParents = parentData.filter { parent ->
            val fullName = "${parent.firstName} ${parent.lastName}"
            val fullName2 = "${parent.firstName2} ${parent.lastName2}"
            fullName.contains(query, ignoreCase = true) ||
                    fullName2.contains(query, ignoreCase = true)
        }
        adapter.updateData(filteredParents)
    }

    private fun fetchParents() {
        CoroutineScope(Dispatchers.IO).launch {
            parentData = networkForUserManagement.getAllParents() // Call your API method

            withContext(Dispatchers.Main) {
                adapter = GetALLParentsAdapter(parentData,childFragmentManager,this@ParentsFragment)
                binding.reclerviewofallparents.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChange() {
        fetchParents()
    }

}