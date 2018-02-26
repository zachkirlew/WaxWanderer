package com.zachkirlew.applications.waxwanderer.favourites

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.explore.OnLongPressListener
import com.zachkirlew.applications.waxwanderer.explore.OnQueryTextListener
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity


class FavouriteFragment : Fragment(), FavouriteContract.View, OnSignOutListener, OnFavouriteRemovedListener, OnQueryTextListener, OnLongPressListener, OnFavouritesFiltered {


    private lateinit var favouritePresenter: FavouriteContract.Presenter

    private lateinit var favouriteAdapter: FavouriteAdapter

    private var noFavouritesText: TextView? = null

    private lateinit var selectedVinyl: VinylRelease

    private var user: User? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        favouriteAdapter = FavouriteAdapter(ArrayList(), this, this,this,this)

        user = activity?.intent?.getSerializableExtra("selected user") as User?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //reuse explore frgment layout as similar
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)

        activity?.title = "Favourites"

        favouritePresenter = FavouritePresenter(this, VinylsRemoteSource.instance)

        val exploreList = root?.findViewById(R.id.explore_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = favouriteAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        noFavouritesText = root.findViewById(R.id.text_no_favourites)

        return root
    }

    override fun onResume() {
        super.onResume()

        //user is viewing someone else's favourite list
        if (user != null) {
            val userId = user?.id
            favouritePresenter.loadFavouriteVinyls(userId!!)
        }
        //user is viewing their own list
        else {
            favouritePresenter.loadFavouriteVinyls()
        }
    }

    override fun onPause() {
        super.onPause()
        favouritePresenter.dispose()
    }

    override fun setPresenter(presenter: FavouriteContract.Presenter) {
        favouritePresenter = presenter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favourite, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_sort -> showSortByDialog()
        }
        return false
    }

    override fun onLongPress(item: Any?) {
        selectedVinyl = item as VinylRelease
        favouritePresenter.loadVinylRelease(selectedVinyl.id.toString())
    }

    override fun onQueryTextSubmit(searchText: String?) {

    }

    override fun onQueryTextChange(searchText: String?) {
        favouriteAdapter.filter.filter(searchText)
    }

    override fun onFiltered(isEmpty: Boolean) {
        if(isEmpty){
            noFavouritesText?.text = "No favourites to display"
            noFavouritesText?.visibility = View.VISIBLE
        }
        else{
            noFavouritesText?.visibility = View.GONE
        }
    }

    override fun showMessage(message: String?) {
        noFavouritesText?.text = message
        noFavouritesText?.visibility = View.VISIBLE
    }

    override fun showFavouriteVinyls(vinyls: List<VinylRelease>) {
        favouriteAdapter.addVinyls(vinyls)
    }


    override fun showVinylRemoved(vinylId: Int) {
        favouriteAdapter.removeVinyl(vinylId)
    }

    override fun onFavouriteRemoved(vinylId: Int) {
        favouritePresenter.removeVinylFromFavourites(vinylId)
    }


    override fun onSignOut() {
        favouritePresenter.dispose()
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

    override fun startVinylDetailActivity(vinyl: VinylRelease) {

        val intent = Intent(activity, VinylDetailActivity::class.java)
        intent.putExtra("selected vinyl", vinyl)
        startActivity(intent)
    }

    private fun showSortByDialog(){
        val singleChoiceItems = resources.getStringArray(R.array.favourite_dialog_sort)
        var itemSelected = 0
        AlertDialog.Builder(activity!!)
                .setTitle("Sort releases by:")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, { _, selectedIndex -> itemSelected = selectedIndex})
                .setPositiveButton("OK", {d,i ->favouriteAdapter.sortVinyls(singleChoiceItems[itemSelected])})
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun commaSeparateList(list: List<String>?): String {
        return android.text.TextUtils.join(", ", list)
    }
}