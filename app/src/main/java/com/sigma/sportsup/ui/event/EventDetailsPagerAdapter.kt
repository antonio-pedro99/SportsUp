package com.sigma.sportsup.ui.event

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventDetailsPagerAdapter(activity: FragmentActivity, private val pages:List<Fragment>):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position]

}