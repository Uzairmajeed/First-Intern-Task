package com.facebook.firsttask.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.facebook.firsttask.MainActivity
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.R
import com.facebook.firsttask.admin.appointments.Appointments_Fragment
import com.facebook.firsttask.admin.class_management.Class_Management
import com.facebook.firsttask.admin.dashboard.AdminDashboardFragment
import com.facebook.firsttask.admin.ptm_management.PTM_ManageFragment
import com.facebook.firsttask.databinding.ActivityAdminPageBinding

class AdminPage : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPageBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        val navigationView = binding.navView
        val openDrawerButton: ImageButton = binding.openDrawerButton

        preferencesManager = PreferencesManager(this)


        // Open the drawer when the button is clicked
        openDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle navigation view item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // Handle Dashboard click
                    updateToolbarText("Dashboard")
                    //binding.toolbartextview.setText()
                    replaceFragment(AdminDashboardFragment(),R.id.nav_dashboard)
                }
                R.id.nav_ptm_management -> {
                    // Handle Payment Management click
                    updateToolbarText("PTM Management")
                    replaceFragment(PTM_ManageFragment(),R.id.nav_ptm_management)
                }
                R.id.nav_appointments -> {
                    // Handle Payment Management click
                    updateToolbarText("Manage Appointments")
                    replaceFragment(Appointments_Fragment(),R.id.nav_appointments)
                }
                R.id.nav_user_management -> {
                    // Handle User Management click
                }
                R.id.nav_class_management -> {
                    // Handle Class Management click
                    updateToolbarText("Class Management")
                    replaceFragment(Class_Management(),R.id.nav_class_management)
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
        // Set initial fragment
        if (savedInstanceState == null) {
            replaceFragment(AdminDashboardFragment(),R.id.nav_dashboard)
        }
    }

    private fun replaceFragment(fragment: Fragment, menuItemId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

        // Highlight the selected menu item
        binding.navView.setCheckedItem(menuItemId)
    }

    fun updateToolbarText(text: String) {
        binding.toolbartextview.text = text
    }


    private fun logoutUser() {
        // Clear login state
        preferencesManager.setLoggedInAsAdmin(false)

        // Navigate back to MainActivity
        val intent = Intent(this@AdminPage, MainActivity::class.java)
        startActivity(intent)
    }
}
