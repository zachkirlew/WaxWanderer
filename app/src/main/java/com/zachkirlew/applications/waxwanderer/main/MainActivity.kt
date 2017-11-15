package com.zachkirlew.applications.waxwanderer.main

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.VinylDataSource
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.explore.ExploreFragment
import com.zachkirlew.applications.waxwanderer.explore.ExplorePresenter
import com.zachkirlew.applications.waxwanderer.util.ActivityUtils


class MainActivity : AppCompatActivity() {

    private val mDrawerLayout: DrawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.setHomeButtonEnabled(true)

        val mDrawerToggle = ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view) as NavigationView

        setupDrawerContent(navigationView)

        val exploreFrag = ExploreFragment()

        ActivityUtils.addFragmentToActivity(
                supportFragmentManager, exploreFrag, R.id.content)

        val explorePresenter = ExplorePresenter(VinylRepository.getInstance(VinylsRemoteSource.instance),exploreFrag)


    }

    public override fun onSaveInstanceState(bundle: Bundle?) {

        super.onSaveInstanceState(bundle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

            }
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

}