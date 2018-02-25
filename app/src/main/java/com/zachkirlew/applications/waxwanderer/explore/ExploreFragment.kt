package com.zachkirlew.applications.waxwanderer.explore

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import com.zachkirlew.applications.waxwanderer.vinyl_preferences.VinylPreferencesActivity

class ExploreFragment: Fragment(), ExploreContract.View, OnQueryTextListener,OnSignOutListener, OnAddToFavouritesListener {

    private lateinit var explorePresenter : ExploreContract.Presenter

    private lateinit var exploreAdapter: ExploreAdapter

    private lateinit var progressBar : ProgressBar

    private var noFavouritesText: TextView? = null

    private var lastSearch : String? = null

    private val coordinatorLayout : CoordinatorLayout by lazy{activity!!.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)}

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        exploreAdapter = ExploreAdapter(ArrayList(0),this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_explore, container, false)

        activity?.title = "Explore"

        explorePresenter = ExplorePresenter(VinylsRemoteSource.instance,this)

        val exploreList = root?.findViewById(R.id.explore_list) as RecyclerView

        exploreList.layoutManager = LinearLayoutManager(activity)
        exploreList.adapter = exploreAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        noFavouritesText = root.findViewById(R.id.text_no_favourites)

        progressBar = root.findViewById(R.id.progress_bar_explore)

        progressBar.visibility = View.VISIBLE

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.explore_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_vinyl_settings -> startVinylPreferenceActivity()
        }
        return false
    }

    override fun startVinylPreferenceActivity() {
        val intent = Intent(activity, VinylPreferencesActivity::class.java)
        intent.putExtra("fromMain",true)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(searchText: String?) {
        lastSearch = searchText
        exploreAdapter.removeVinyls()
        explorePresenter.searchVinylReleases(searchText)
    }

    override fun onQueryTextChange(searchText: String?) {
    }

    override fun showMessage(message: String?) {
        message?.let { Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).show() }
    }

    override fun setPresenter(presenter: ExploreContract.Presenter) {
        explorePresenter = presenter
    }

    override fun showVinylReleases(vinyls: List<VinylRelease>) {
        noFavouritesText?.visibility = View.GONE

        progressBar.visibility = View.GONE
        exploreAdapter.addVinyls(vinyls)
    }

    override fun showNoVinylsView() {
        progressBar.visibility = View.GONE
        exploreAdapter.removeVinyls()
        noFavouritesText?.text = getString(R.string.text_no_vinyls)
        noFavouritesText?.visibility = View.VISIBLE
    }

    override fun showNoInternetMessage() {
        progressBar.visibility = View.GONE
        noFavouritesText?.text = getString(R.string.text_no_internet)
        noFavouritesText?.visibility = View.VISIBLE
    }

    override fun onAddedToFavourites(vinyl : VinylRelease) {
        explorePresenter.addToFavourites(vinyl)
    }

    override fun onResume() {
        super.onResume()
        explorePresenter.start()

        if(lastSearch==null){
            exploreAdapter.removeVinyls()
            explorePresenter.loadVinylPreferences()
        }
    }

    override fun onPause() {
        super.onPause()
        explorePresenter.dispose()
    }

    override fun onSignOut() {
        explorePresenter.dispose()
    }
}