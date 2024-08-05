package com.facebook.firsttask.admin.user_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.firsttask.databinding.FragmentUserManagementBinding
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import com.facebook.firsttask.admin.appointments.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class UserManagement : Fragment() {

    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        val view = binding.root

        setupViewPager()

        return view
    }

    private fun setupViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout
        viewPager.adapter = UserManagementPagerAdapter(requireActivity())
        viewPager.offscreenPageLimit = 2


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Parents"
                1 -> "Teachers"
                else -> null
            }
        }.attach()
    }
}
