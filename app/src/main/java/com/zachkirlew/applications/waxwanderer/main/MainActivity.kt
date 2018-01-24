package com.zachkirlew.applications.waxwanderer.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.ExploreFragment
import com.zachkirlew.applications.waxwanderer.explore.OnSearchSubmitted
import com.zachkirlew.applications.waxwanderer.favourites.FavouriteFragment
import com.zachkirlew.applications.waxwanderer.leaderboard.LeaderBoardFragment
import com.zachkirlew.applications.waxwanderer.login.LoginActivity
import com.zachkirlew.applications.waxwanderer.matches.MatchesFragment
import com.zachkirlew.applications.waxwanderer.settings.SettingsFragment
import com.zachkirlew.applications.waxwanderer.match.MatchFragment
import com.zachkirlew.applications.waxwanderer.styles.StylesActivity
import com.zachkirlew.applications.waxwanderer.util.ActivityUtils
import com.zachkirlew.applications.waxwanderer.util.BorderedCircleTransform
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.View {

    private val mDrawerLayout: DrawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    private var showSearchIcon = true

    private lateinit var presenter: MainPresenter

    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)

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

        navigationView = findViewById<NavigationView>(R.id.nav_view) as NavigationView

        setupDrawerContent()

        navigationView.menu.getItem(0).isChecked = true
    }

    override fun startExploreFragment() {
        var exploreFrag: Fragment? = supportFragmentManager.findFragmentById(R.id.content)

        if (exploreFrag == null) {
            exploreFrag = ExploreFragment()
            ActivityUtils.addFragmentToActivity(
                    supportFragmentManager, exploreFrag, R.id.content)
        }
    }

    override fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        presenter.setAuthListener()
    }

    override fun onStop() {
        super.onStop()
        presenter.removeAuthListener()
    }

    override fun startStylesActivity() {
        val intent = Intent(this, StylesActivity::class.java)
        intent.putExtra("fromMain",true)
        startActivity(intent)
    }

    public override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(showSearchIcon){
            val inflater = menuInflater
            inflater.inflate(R.menu.search_menu, menu)

            val searchItem = menu.findItem(R.id.action_search)
            val searchView = MenuItemCompat.getActionView(searchItem) as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    val fragment = supportFragmentManager.findFragmentById(R.id.content)
                    if (fragment is OnSearchSubmitted)
                        fragment.searchSubmitted(query)
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            return true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_vinyl_settings -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                startStylesActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent() {

        navigationView.setNavigationItemSelectedListener { menuItem ->

            uncheckAllMenuItems(navigationView)

            when (menuItem.itemId) {

                R.id.log_out_navigation_menu_item ->{

                    presenter.signOut()
                }

                R.id.explore_navigation_menu_item ->{
                    showSearchIcon = true
                    ActivityUtils.changeFragment(
                            supportFragmentManager, ExploreFragment(), R.id.content)
                }

                R.id.favourites_navigation_menu_item ->{
                    showSearchIcon = false
                    ActivityUtils.changeFragment(
                            supportFragmentManager, FavouriteFragment(), R.id.content)
                }

                R.id.match_navigation_menu_item ->{
                    showSearchIcon = false
                    ActivityUtils.changeFragment(
                            supportFragmentManager, MatchFragment(), R.id.content)
                }

                R.id.matches_navigation_menu_item ->{
                    showSearchIcon = false
                    ActivityUtils.changeFragment(
                            supportFragmentManager, MatchesFragment(), R.id.content)
                }

                R.id.leaderboard_navigation_menu_item ->{
                    showSearchIcon = false
                    ActivityUtils.changeFragment(
                            supportFragmentManager, LeaderBoardFragment(), R.id.content)
                }

                R.id.settings_navigation_menu_item ->{
                    showSearchIcon = false
                    ActivityUtils.changeFragment(
                            supportFragmentManager, SettingsFragment(), R.id.content)
                }
            }

            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            invalidateOptionsMenu()

            true
        }
    }

    override fun showDisplayName(displayName: String) {
        val header = navigationView.getHeaderView(0)

        val nameText = header?.findViewById<TextView>(R.id.name) as TextView

        nameText.text = displayName
    }

    override fun showProfilePicture(imageUrl: String) {
        val header = nav_view.getHeaderView(0)

        val profileImage = header?.findViewById<ImageView>(R.id.profile_image) as ImageView

        Picasso.with(this@MainActivity)
                .load(imageUrl)
                .resize(160, 160)
                .centerCrop()
                .transform(BorderedCircleTransform())
                .into(profileImage)
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