package com.zachkirlew.applications.waxwanderer.friends

import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.design.widget.TabLayout
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.OnQueryTextListener
import com.zachkirlew.applications.waxwanderer.friends.requests.RequestsFragment
import com.zachkirlew.applications.waxwanderer.friends.search.FriendsSearchFragment


class FriendsTabFragment : Fragment(), OnQueryTextListener {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_friends_tab, container, false)

        activity?.title = "Friends"

        viewPager = view.findViewById(R.id.viewpager) as ViewPager
        setupViewPager(viewPager)

        val tabs = view.findViewById(R.id.tabs) as TabLayout
        tabs.setupWithViewPager(viewPager)

        return view
    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = Adapter(childFragmentManager)
        adapter.addFragment(FriendsFragment(), "All")
        adapter.addFragment(RequestsFragment(), "Requests")
        adapter.addFragment(FriendsSearchFragment(), "Search")

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
    }

    override fun onQueryTextSubmit(searchText: String?) {
        val frag = childFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.currentItem)
        if(frag is FriendsSearchFragment){
            if(searchText?.isNotEmpty()!!)
                frag.handleSearch(searchText)
        }
    }

    override fun onQueryTextChange(searchText: String?) {
    }

    internal class Adapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }
}