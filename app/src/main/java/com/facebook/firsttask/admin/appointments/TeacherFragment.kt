package com.facebook.firsttask.admin.appointments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.FragmentTeacherBinding
import kotlinx.coroutines.launch

class TeacherFragment : Fragment() {

    private lateinit var networkForAppointments: NetworkForAppointments
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherAdapter
    private var _binding: FragmentTeacherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())

        networkForAppointments = preferencesManager.getAuthToken()?.let { NetworkForAppointments(it, requireContext()) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.reclerviewofallAppointmentsWithteacherDetails// Make sure you have a RecyclerView in your fragment_teacher.xml
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Use Coroutine to call the network method
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Fetch the list of TeacherAppointmentData directly
                val appointments = networkForAppointments.getAllAppointmentsWithTeacherDetails()

                // Initialize and set the adapter
                adapter = TeacherAdapter(appointments)
                recyclerView.adapter = adapter


                Log.d("TeacherAppointments", "Appointments: $appointments")
            } catch (e: Exception) {
                Log.e("TeacherFragment", "Error fetching appointments", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
