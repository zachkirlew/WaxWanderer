package com.zachkirlew.applications.waxwanderer.explore

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.vinyl_preferences.VinylPreferencesActivity


class ExploreFragment: Fragment(),
        ExploreContract.View, OnQueryTextListener,
        OnSignOutListener,
        OnAddToFavouritesListener,
        OnLongPressListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var explorePresenter : ExploreContract.Presenter

    private lateinit var exploreAdapter: ExploreAdapter

    private lateinit var progressBar : ProgressBar

    private var noFavouritesText: TextView? = null

    private var lastSearch : String? = null

    private lateinit var selectedVinyl: VinylRelease

    private val coordinatorLayout : CoordinatorLayout by lazy{activity!!.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)}

    private val PAGE_SIZE = 50
    private val PAGINATION_MARGIN = 10
    private var endOfList = false

    private lateinit var swipeContainer : SwipeRefreshLayout

    private val TAG = ExploreFragment::class.java.simpleName

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exploreAdapter = ExploreAdapter(ArrayList(0),this,this)
    }

    private lateinit var queryParams: HashMap<String, String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_explore, container, false)

        queryParams = activity?.intent?.getSerializableExtra("params") as HashMap<String, String>

        activity?.title = queryParams.values.first()

        explorePresenter = ExplorePresenter(VinylsRemoteSource.instance,this)

        val exploreList = root?.findViewById(R.id.explore_list) as RecyclerView

        exploreList.layoutManager = LinearLayoutManager(activity)
        exploreList.adapter = exploreAdapter

        exploreList.addOnScrollListener(scrollListener)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        swipeContainer = root.findViewById(R.id.swipe_container)
        swipeContainer.setOnRefreshListener(this)

        noFavouritesText = root.findViewById(R.id.text_no_favourites)

        progressBar = root.findViewById(R.id.progress_bar_explore)

        progressBar.visibility = View.VISIBLE

        return root
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_vinyl_settings -> startVinylPreferenceActivity()
        }
        return false
    }

    override fun startVinylDetailActivity(vinyl: VinylRelease) {

        val intent = Intent(activity, VinylDetailActivity::class.java)
        intent.putExtra("selected vinyl", vinyl)
        startActivity(intent)
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

    override fun onLongPress(item: Any?) {
        selectedVinyl = item as VinylRelease
        explorePresenter.loadVinylRelease(selectedVinyl.id.toString())
    }

    override fun onRefresh() {
        Log.i(TAG,"Refreshed:")
        explorePresenter.refresh()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.layoutManager.childCount
            val totalItemCount = recyclerView.layoutManager.itemCount
            val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (!swipeContainer.isRefreshing && !endOfList) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - PAGINATION_MARGIN
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    Log.i(TAG,"Margin reached, loading next page")
                    explorePresenter.onLoadNextPage()
                }
            }
        }
    }

    override fun clearVinyls() {
        exploreAdapter.removeVinyls()
    }

    override fun showQuickViewDialog(detailedVinylRelease: DetailVinylRelease) {

        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_quickview, null)

        val headerImage = dialogView.findViewById<ImageView>(R.id.header_image)
        val titleText = dialogView.findViewById<TextView>(R.id.title_release)
        val artistsText = dialogView.findViewById<TextView>(R.id.text_artists)
        val labelText = dialogView.findViewById<TextView>(R.id.text_label)
        val releaseDateText = dialogView.findViewById<TextView>(R.id.text_release_date)
        val genreText = dialogView.findViewById<TextView>(R.id.text_genre)
        val stylesText = dialogView.findViewById<TextView>(R.id.text_styles)

        if(detailedVinylRelease.images!=null)
            Picasso.with(activity)
                    .load(detailedVinylRelease.images!![0].uri)
                    .into(headerImage)

        else
            headerImage.visibility = View.GONE

        val artistNames = detailedVinylRelease.artists?.map { it.name }

        titleText.text = detailedVinylRelease.title

        artistsText.text = commaSeparateList(artistNames)

        labelText.text = detailedVinylRelease.labels?.get(0)?.name
        releaseDateText.text = detailedVinylRelease.releasedFormatted
        genreText.text = detailedVinylRelease.genres?.get(0)

        detailedVinylRelease.styles?.let{
            stylesText.text = commaSeparateList(detailedVinylRelease.styles)
        }


        val aDialog =  AlertDialog.Builder(activity!!)
                .setView(dialogView)
                .setPositiveButton("View",{ _, i ->  startVinylDetailActivity(selectedVinyl)})
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        aDialog.show()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        swipeContainer.isRefreshing = isRefreshing
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
            explorePresenter.loadVinylReleases(queryParams)
        }
    }

    override fun onPause() {
        super.onPause()
        explorePresenter.dispose()
    }

    override fun onSignOut() {
        explorePresenter.dispose()
    }

    private fun commaSeparateList(list: List<String>?): String {
        return android.text.TextUtils.join(", ", list)
    }
}