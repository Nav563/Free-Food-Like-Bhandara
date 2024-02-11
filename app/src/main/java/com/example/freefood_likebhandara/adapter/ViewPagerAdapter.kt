package com.example.freefood_likebhandara.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.freefood_likebhandara.fragment.DailyBhandaraFragment
import com.example.freefood_likebhandara.fragment.RecentBhandaraFragment


class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecentBhandaraFragment()
            1 -> DailyBhandaraFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}