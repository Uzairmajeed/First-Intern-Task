// UserManagementPagerAdapter.kt
package com.facebook.firsttask.admin.user_management

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.facebook.firsttask.admin.appointments.AllAppointmentsFragment
import com.facebook.firsttask.admin.appointments.TeacherFragment

class UserManagementPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(
        ParentsFragment(),
        TeachersFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}