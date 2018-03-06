package com.waxwanderer.browse

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import com.waxwanderer.R
import com.waxwanderer.base.OnSignOutListener
import com.waxwanderer.vinyl_preferences.VinylPreferencesActivity
import android.support.v7.widget.GridLayoutManager
import com.waxwanderer.browse.StylesAdapter
import com.waxwanderer.data.model.Style


class BrowseFragment: Fragment(), BrowseContract.View, OnSignOutListener {


    private lateinit var explorePresenter : BrowseContract.Presenter


    private lateinit var progressBar : ProgressBar

    private lateinit var userStylesAdapter: StylesAdapter
    private lateinit var allGenresAdapter: StylesAdapter

    private val coordinatorLayout : CoordinatorLayout by lazy{activity!!.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)}

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        userStylesAdapter = StylesAdapter(ArrayList(0),"style")
        allGenresAdapter = StylesAdapter(ArrayList(0),"genre")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_browse, container, false)

        activity?.title = "Browse"

        explorePresenter = BrowsePresenter(this)

        val browseList = root?.findViewById(R.id.browse_list) as RecyclerView
        val allGenresList = root.findViewById(R.id.all_genres_list) as RecyclerView

        browseList.layoutManager = (GridLayoutManager(browseList.context, 2))
        allGenresList.layoutManager = (GridLayoutManager(browseList.context, 2))


        browseList.adapter = userStylesAdapter
        allGenresList.adapter = allGenresAdapter

        progressBar = root.findViewById(R.id.progress_bar_vinyl)

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

    override fun showStyles(styles: List<Style>) {
        progressBar.visibility = View.GONE
        userStylesAdapter.addStyles(styles)
    }

    override fun showAllGenres(genres: List<Style>) {
        allGenresAdapter.addStyles(genres)
    }

    override fun changeProgressBarVisibility(show: Boolean) {
        if(show) progressBar.visibility = View.VISIBLE else progressBar.visibility = View.GONE
    }
    override fun startVinylPreferenceActivity() {
        val intent = Intent(activity, VinylPreferencesActivity::class.java)
        intent.putExtra("fromMain",true)
        startActivity(intent)
    }


    override fun showMessage(message: String?) {
        message?.let { Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).show() }
    }

    override fun setPresenter(presenter: BrowseContract.Presenter) {
        explorePresenter = presenter
    }

    override fun onResume() {
        super.onResume()
        explorePresenter.start()

    }

    override fun onPause() {
        super.onPause()
        explorePresenter.dispose()
    }

    override fun onSignOut() {
        explorePresenter.dispose()
    }

}