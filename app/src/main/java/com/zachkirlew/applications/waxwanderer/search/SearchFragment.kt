package com.zachkirlew.applications.waxwanderer.search

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
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import com.zachkirlew.applications.waxwanderer.vinyl.OnLongPressListener
import com.zachkirlew.applications.waxwanderer.vinyl.OnQueryTextListener
import com.zachkirlew.applications.waxwanderer.vinyl.OnVinylsChangedListener
import com.zachkirlew.applications.waxwanderer.vinyl.VinylAdapter
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.vinyl_preferences.VinylPreferencesActivity

class SearchFragment: Fragment(),
        SearchContract.View, OnQueryTextListener,
        OnSignOutListener, SwipeRefreshLayout.OnRefreshListener, OnVinylsChangedListener, OnLongPressListener {


    private lateinit var searchPresenter : SearchContract.Presenter

    private lateinit var searchAdapter: VinylAdapter

    private var searchPromptText: TextView? = null

    private lateinit var selectedVinyl: VinylRelease

    private val coordinatorLayout : CoordinatorLayout by lazy{activity!!.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)}

    private val PAGE_SIZE = 50
    private val PAGINATION_MARGIN = 10
    private var endOfList = false

    private lateinit var swipeContainer : SwipeRefreshLayout

    private val queryParams: HashMap<String, String> = HashMap()

    private val TAG = SearchFragment::class.java.simpleName

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        searchAdapter = VinylAdapter(ArrayList(0),this,this,false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        activity?.title = "Search"

        searchPresenter = SearchPresenter(VinylsRemoteSource.instance,this)

        val exploreList = root?.findViewById(R.id.vinyl_list) as RecyclerView

        exploreList.layoutManager = LinearLayoutManager(activity)
        exploreList.adapter = searchAdapter

        exploreList.addOnScrollListener(scrollListener)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        swipeContainer = root.findViewById(R.id.swipe_container)
        swipeContainer.setOnRefreshListener(this)

        searchPromptText = root.findViewById(R.id.text_search_prompt)


        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_advanced_search -> showAdvancedSearchDialog()
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
        refreshSearch()

        queryParams["q"] = searchText!!
        searchPresenter.searchVinylReleases(queryParams)
    }

    private fun refreshSearch(){
        queryParams.clear()
        searchAdapter.removeVinyls()
    }

    override fun onQueryTextChange(searchText: String?) {
    }

    override fun setEndOfList(isEnd: Boolean) {
        endOfList = isEnd
    }

    override fun showMessage(message: String?) {
        message?.let { Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).show() }
    }

    override fun setPresenter(presenter: SearchContract.Presenter) {
        searchPresenter = presenter
    }

    override fun showVinylReleases(vinyls: List<VinylRelease>) {
        searchPromptText?.visibility = View.GONE
        searchAdapter.addVinyls(vinyls)
    }

    override fun onLongPress(item: Any?) {
        selectedVinyl = item as VinylRelease
        searchPresenter.loadVinylRelease(selectedVinyl.id.toString())
    }

    override fun onRefresh() {
        Log.i(TAG,"Refreshed:")
        searchPresenter.refresh()
        endOfList = false
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
                    searchPresenter.onLoadNextPage()
                }
            }
        }
    }

    override fun onFiltered(isEmpty: Boolean) {
    }

    override fun onRemovedFromFavourites(vinylId: Int) {
    }

    override fun clearVinyls() {
        searchAdapter.removeVinyls()
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

    private fun showAdvancedSearchDialog() {

        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_advanced_search, null)

        val inputArtist = dialogView.findViewById<EditText>(R.id.input_artist)
        val inputStyle = dialogView.findViewById<EditText>(R.id.input_style)
        val inputCountry = dialogView.findViewById<EditText>(R.id.input_country)
        val inputYear = dialogView.findViewById<EditText>(R.id.input_year)
        val inputLabel = dialogView.findViewById<EditText>(R.id.input_label)
        val inputGenre = dialogView.findViewById<EditText>(R.id.input_genre)
        val inputTrack = dialogView.findViewById<EditText>(R.id.input_track)

        val aDialog =  AlertDialog.Builder(activity!!)
                .setView(dialogView)
                .setPositiveButton("Search",{ _, _ ->

                    refreshSearch()

                    if(inputArtist.text.isNotEmpty() && inputArtist.text.isNotBlank())
                        queryParams["artist"] = inputArtist.text.toString()

                    if(inputStyle.text.isNotEmpty() && inputStyle.text.isNotBlank())
                        queryParams["style"] = inputStyle.text.toString()

                    if(inputCountry.text.isNotEmpty() && inputCountry.text.isNotBlank())
                        queryParams["country"] = inputCountry.text.toString()

                    if(inputLabel.text.isNotEmpty() && inputLabel.text.isNotBlank())
                        queryParams["label"] = inputLabel.text.toString()

                    if(inputGenre.text.isNotEmpty() && inputGenre.text.isNotBlank())
                        queryParams["genre"] = inputGenre.text.toString()

                    if(inputTrack.text.isNotEmpty() && inputTrack.text.isNotBlank())
                        queryParams["track"] = inputTrack.text.toString()

                    if(inputYear.text.isNotEmpty() && inputYear.text.isNotBlank())
                        queryParams["year"] = inputYear.text.toString()

                    searchPresenter.searchVinylReleases(queryParams)
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        aDialog.show()
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        swipeContainer.isRefreshing = isRefreshing
    }

    override fun showNoVinylsView() {
        searchAdapter.removeVinyls()
        searchPromptText?.text = getString(R.string.text_no_vinyls)
        searchPromptText?.visibility = View.VISIBLE
    }

    override fun showNoInternetMessage() {
        searchPromptText?.text = getString(R.string.text_no_internet)
        searchPromptText?.visibility = View.VISIBLE
    }

    override fun onAddedToFavourites(vinyl : VinylRelease) {
        searchPresenter.addToFavourites(vinyl)
    }

    override fun onResume() {
        super.onResume()
        searchPresenter.start()
    }

    override fun onPause() {
        super.onPause()
        searchPresenter.dispose()
    }

    override fun onSignOut() {
        searchPresenter.dispose()
    }


    private fun commaSeparateList(list: List<String>?): String {
        return android.text.TextUtils.join(", ", list)
    }
}