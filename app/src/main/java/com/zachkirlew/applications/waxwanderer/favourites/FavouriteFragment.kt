package com.zachkirlew.applications.waxwanderer.favourites

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration


class FavouriteFragment: Fragment(), FavouriteContract.View,OnSignOutListener, OnFavouriteRemovedListener {


    private lateinit var favouritePresenter : FavouriteContract.Presenter

    private lateinit var favouriteAdapter: FavouriteAdapter

    private var noFavouritesText: TextView? = null


    private var user: User? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favouriteAdapter = FavouriteAdapter(ArrayList(),this,this)

        user = activity?.intent?.getSerializableExtra("selected user") as User?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //reuse explore frgment layout as similar
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)

        activity?.title = "Favourites"

        favouritePresenter = FavouritePresenter(this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView

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
        if(user!=null){
            val userId = user?.id
            favouritePresenter.loadFavouriteVinyls(userId!!)
        }
        //user is viewing their own list
        else{
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
}