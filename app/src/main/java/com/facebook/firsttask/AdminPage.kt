package com.facebook.firsttask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.firsttask.AdminDashboardFragment
import com.facebook.firsttask.R

class AdminPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminDashboardFragment())
                .commit()
        }
    }
}
