package com.beloushkin.tinderclone.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.beloushkin.tinderclone.R
import com.beloushkin.tinderclone.fragments.MatchesFragment
import com.beloushkin.tinderclone.fragments.ProfileFragment
import com.beloushkin.tinderclone.fragments.SwipeFragment
import com.beloushkin.tinderclone.views.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_tinder.*


class TinderActivity : BaseActivity() {

    lateinit var profileFragment: ProfileFragment
    lateinit var swipeFragment: SwipeFragment
    lateinit var matchesFragment: MatchesFragment

    lateinit var profileTab: TabLayout.Tab
    lateinit var swipeTab: TabLayout.Tab
    lateinit var matchesTab: TabLayout.Tab

    override fun makeToast(message: String) {
        super.makeToast(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tinder)

        profileTab = navigationTabs.newTab()
        swipeTab = navigationTabs.newTab()
        matchesTab = navigationTabs.newTab()

        profileTab.icon = ContextCompat.getDrawable(this, R.drawable.tab_profile)
        swipeTab.icon = ContextCompat.getDrawable(this, R.drawable.tab_swipe)
        matchesTab.icon = ContextCompat.getDrawable(this, R.drawable.tab_matches)

        navigationTabs.addTab(profileTab)
        navigationTabs.addTab(swipeTab)
        navigationTabs.addTab(matchesTab)

        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab) {
                    profileTab -> {
                        if (!::profileFragment.isInitialized) profileFragment = ProfileFragment()
                        replaceFragment(profileFragment)
                    }
                    swipeTab -> {
                        if (!::swipeFragment.isInitialized) swipeFragment = SwipeFragment()
                        replaceFragment(swipeFragment)
                    }
                    matchesTab -> {
                        if (!::matchesFragment.isInitialized) matchesFragment = MatchesFragment()
                        replaceFragment(matchesFragment)
                    }
                }
            }
        })

        profileTab.select()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, TinderActivity::class.java)
    }
}
