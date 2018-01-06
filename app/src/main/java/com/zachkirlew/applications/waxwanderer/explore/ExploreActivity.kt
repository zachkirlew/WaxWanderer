package com.zachkirlew.applications.waxwanderer.explore

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.favourites.FavouriteFragment
import com.zachkirlew.applications.waxwanderer.login.LoginActivity
import com.zachkirlew.applications.waxwanderer.settings.SettingsFragment
import com.zachkirlew.applications.waxwanderer.util.ActivityUtils


class ExploreActivity : AppCompatActivity() {

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

        var exploreFrag: ExploreFragment? = supportFragmentManager.findFragmentById(R.id.content) as ExploreFragment?

        if (exploreFrag == null) {

            exploreFrag = ExploreFragment()
            ActivityUtils.addFragmentToActivity(
                    supportFragmentManager, exploreFrag, R.id.content)
        }

        navigationView.menu.getItem(0).isChecked = true
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

            uncheckAllMenuItems(navigationView)

            when (menuItem.itemId) {

                R.id.log_out_navigation_menu_item ->{

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

                R.id.explore_navigation_menu_item ->{
                    ActivityUtils.changeFragment(
                            supportFragmentManager, ExploreFragment(), R.id.content)
                }

                R.id.favourites_navigation_menu_item ->{
                    ActivityUtils.changeFragment(
                            supportFragmentManager, FavouriteFragment(), R.id.content)
                }

                R.id.match_navigation_menu_item ->{
                    ActivityUtils.changeFragment(
                            supportFragmentManager, SettingsFragment(), R.id.content)
                }
            }

            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

    private fun uncheckAllMenuItems(navigationView: NavigationView) {
        val menu = navigationView.menu
        (0 until menu.size())
                .map { menu.getItem(it) }
                .forEach {
                    if (it.hasSubMenu()) {
                        val subMenu = it.subMenu
                        (0 until subMenu.size())
                                .map { subMenu.getItem(it) }
                                .forEach { it.isChecked = false }
                    } else {
                        it.isChecked = false
                    }
                }
    }

}