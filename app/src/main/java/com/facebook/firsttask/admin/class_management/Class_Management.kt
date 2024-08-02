package com.facebook.firsttask.admin.class_management

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.R
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class Class_Management : Fragment(),OnUpdatedCLassNameListner {

    private lateinit var networkForClassManage: NetworkForClassManagement
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter: ClassAdapterForClassManage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(requireContext())
        val authToken = preferencesManager.getAuthToken()
        networkForClassManage = authToken?.let { NetworkForClassManagement(it, requireContext()) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class__management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewForGetClassManagment)
        // Use GridLayoutManager with 2 columns
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        tabLayout = view.findViewById(R.id.tabLayout)
        loadWingsData()
    }

    private fun loadWingsData() {
        lifecycleScope.launch {
            try {
                val response = networkForClassManage.getAllWingNames()

                // Log the entire response
                Log.d("WingsData", "Response: $response")

                // Optionally, you can also log specific details
                response?.forEach { wing ->
                    Log.d("WingsData", "Wing ID: ${wing.wingId}, Wing Name: ${wing.wingName}")
                }

                // Set up the tabs based on the response
                response?.let { wings ->
                    for (wing in wings) {
                        val tab = tabLayout.newTab()
                        tab.text = wing.wingName

                        // Ensure that tab text is fully visible
                        tabLayout.addTab(tab)
                    }

                    // Handle tab selection
                    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab?) {
                            tab?.let {
                                val wingName = it.text.toString()
                                Log.d("TabSelected", wingName)

                                lifecycleScope.launch {
                                    val classes = networkForClassManage.getAllClassesForManagement(wingName)
                                    Log.d("ClassesResponse", "Classes for $wingName: $classes")

                                    if (classes.isNullOrEmpty()) {
                                        classAdapter = ClassAdapterForClassManage(emptyList(),childFragmentManager,this@Class_Management)
                                        recyclerView.adapter = classAdapter
                                    } else {
                                        // Update the RecyclerView with the new data
                                        classAdapter = ClassAdapterForClassManage(classes,childFragmentManager,this@Class_Management)
                                        recyclerView.adapter = classAdapter
                                    }
                                }
                            }
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {
                            // Handle tab unselected if needed
                        }

                        override fun onTabReselected(tab: TabLayout.Tab?) {
                            // Handle tab reselected if needed
                        }
                    })

                    // Set initial tab selection
                    if (wings.isNotEmpty()) {
                        tabLayout.getTabAt(5)?.select()
                    }

                }

            } catch (e: Exception) {
                Log.e("WingsDataError", "Error loading wings data", e)
            }
        }
    }

    override fun onUpdatedClass() {
        loadWingsData()
    }

}
