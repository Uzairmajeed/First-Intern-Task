package com.facebook.firsttask.admin

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
import com.facebook.firsttask.MainActivity
import com.facebook.firsttask.R
import com.facebook.firsttask.databinding.FragmentAdminDashboardBinding
import java.util.*

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var calendarGrid: ViewGroup
    private lateinit var monthYearTextView: TextView

    private var currentMonth: Int = 0
    private var currentYear: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize calendar to current month
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH)
        currentYear = calendar.get(Calendar.YEAR)
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
        calendarGrid = binding.calendarGrid
        monthYearTextView = binding.monthYearTextView
        val openDrawerButton: ImageButton = binding.openDrawerButton
        val prevMonthButton: ImageButton = binding.prevMonthButton
        val nextMonthButton: ImageButton = binding.nextMonthButton

        // Set initial month and year in the TextView
        updateMonthYearText(currentMonth, currentYear)

        // Open the drawer when the button is clicked
        openDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle previous month button click
        prevMonthButton.setOnClickListener {
            if (currentMonth == Calendar.JANUARY) {
                currentMonth = Calendar.DECEMBER
                currentYear--
            } else {
                currentMonth--
            }
            updateCalendar()
        }

        // Handle next month button click
        nextMonthButton.setOnClickListener {
            if (currentMonth == Calendar.DECEMBER) {
                currentMonth = Calendar.JANUARY
                currentYear++
            } else {
                currentMonth++
            }
            updateCalendar()
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


        // Set up the initial calendar view
        setupCalendar()
    }

    private fun setupCalendar() {
        // Clear existing views in the calendar grid
        calendarGrid.removeAllViews()

        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Add day labels
        for (day in daysOfWeek) {
            val textView = TextView(requireContext()).apply {
                text = day
                textSize = 18f
                setPadding(20, 20, 20, 20)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            calendarGrid.addView(textView)
        }

        // Update the calendar grid with current month
        updateCalendar()
    }

    private fun updateCalendar() {
        // Clear existing date views
        calendarGrid.removeViews(7, calendarGrid.childCount - 7)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // Add empty spaces for days before the 1st day of the month
        for (i in 0 until firstDayOfWeek) {
            val emptyTextView = TextView(requireContext()).apply {
                text = ""
                setPadding(8, 8, 8, 8)
            }
            calendarGrid.addView(emptyTextView)
        }

        // Add date numbers
        for (day in 1..daysInMonth) {
            val textView = TextView(requireContext()).apply {
                text = day.toString()
                textSize = 18f
                setPadding(10, 10, 10, 10)
                textAlignment = View.TEXT_ALIGNMENT_CENTER

                // Highlight today's date
                if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                    currentMonth == Calendar.getInstance().get(Calendar.MONTH) &&
                    currentYear == Calendar.getInstance().get(Calendar.YEAR)) {
                    // You can set any custom background or foreground color to highlight the current day
                    setBackgroundResource(R.drawable.current_day_background)
                }

            }
            calendarGrid.addView(textView)
        }

        // Update month and year display
        updateMonthYearText(currentMonth, currentYear)
    }

    private fun updateMonthYearText(month: Int, year: Int) {
        val monthYear = "${getMonthName(month)} $year"
        monthYearTextView.text = monthYear
    }

    private fun getMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
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
