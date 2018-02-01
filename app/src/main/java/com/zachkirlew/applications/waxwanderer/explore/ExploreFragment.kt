package com.zachkirlew.applications.waxwanderer.explore

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import kotlinx.android.synthetic.main.explore_item.view.*

class ExploreFragment: Fragment(), ExploreContract.View, OnSearchSubmitted{

    private lateinit var explorePresenter : ExploreContract.Presenter

    private lateinit var exploreAdapter: ExploreAdapter

    private lateinit var progressBar : ProgressBar

    private var noFavouritesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        exploreAdapter = ExploreAdapter(ArrayList<VinylRelease>(0))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_explore, container, false)

        activity.title = "Explore"

        explorePresenter = ExplorePresenter(VinylRepository.getInstance(VinylsRemoteSource.instance),this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView

        exploreList.layoutManager = LinearLayoutManager(activity)
        exploreList.adapter = exploreAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        noFavouritesText = root.findViewById<TextView>(R.id.text_no_favourites)

        progressBar = root.findViewById<ProgressBar>(R.id.progress_bar_explore)

        progressBar.visibility = View.VISIBLE

        explorePresenter.start()

        return root
    }

    override fun searchSubmitted(searchText: String?) {
        exploreAdapter.removeVinyls()
        explorePresenter.searchVinylReleases(searchText)
    }

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show()
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

    override fun onStop() {
        super.onStop()
        explorePresenter.dispose()
    }
}