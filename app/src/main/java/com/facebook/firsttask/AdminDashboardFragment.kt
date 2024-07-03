package com.facebook.firsttask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.facebook.firsttask.databinding.FragmentAdminDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class AdminDashboardFragment : Fragment(), CustomCalendarView.MonthChangeListener {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var monthYearTextView: TextView
    private lateinit var customCalendarView: CustomCalendarView // Add this




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using View Binding
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerLayout = binding.drawerLayout
        val navigationView = binding.navView
        monthYearTextView = binding.monthYearTextView
        val openDrawerButton: ImageButton = binding.openDrawerButton
        val prevMonthButton: ImageButton = binding.prevMonthButton
        val nextMonthButton: ImageButton = binding.nextMonthButton
        // Initialize CustomCalendarView
        customCalendarView = view.findViewById(R.id.customCalendarView)
        // Set initial month and year
        val initialCalendar = Calendar.getInstance()
        updateMonthYearText(initialCalendar)

        // Set listener for month change in CustomCalendarView
        customCalendarView.setMonthChangeListener(this)


        // Open the drawer when the button is clicked
        openDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.addNewPtmButton.setOnClickListener {

        }

        // Handle previous month button click
        prevMonthButton.setOnClickListener {
            customCalendarView.showPreviousMonth()

        }

        // Handle next month button click
        nextMonthButton.setOnClickListener {
            customCalendarView.showNextMonth()
        }

        // Handle navigation view item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // Handle Dashboard click
                }
                R.id.nav_ptm_management -> {
                    // Handle Payment Management click
                }
                R.id.nav_appointments -> {
                    // Handle Appointments click
                }
                R.id.nav_user_management -> {
                    // Handle User Management click
                }
                R.id.nav_class_management -> {
                    // Handle Class Management click
                }
                R.id.nav_subject_management -> {
                    // Handle Subject Management click
                }
                R.id.nav_notifications -> {
                    // Handle Notifications click
                }
                R.id.nav_settings -> {
                    // Handle Settings click
                }
                R.id.nav_change_password -> {
                    // Handle Change Password click
                }
                R.id.nav_logout -> {
                    logoutUser()
                }
            }

            // Close the drawer after handling the click
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }



    }
    override fun onMonthChange(calendar: Calendar) {
        updateMonthYearText(calendar)
    }

    private fun updateMonthYearText(calendar: Calendar) {
        val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        monthYearTextView.text = monthYearFormat.format(calendar.time)
    }

    private fun logoutUser() {
        // Clear login state
        val sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedInAsAdmin", false)
            apply()
        }

        // Navigate back to MainActivity
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}