package com.zachkirlew.applications.waxwanderer.favourites

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import kotlinx.android.synthetic.main.explore_item.view.*
import android.app.Activity
import android.support.v4.app.ActivityCompat.startActivityForResult


class FavouriteFragment: Fragment(), FavouriteContract.View {


    private lateinit var favouritePresenter : FavouriteContract.Presenter

    private lateinit var favouriteAdapter: FavouriteFragment.FavouriteAdapter

    private var noFavouritesText: TextView? = null


    private var user: User? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favouriteAdapter = FavouriteAdapter(ArrayList<VinylRelease>(),this)

        user = activity.intent.getSerializableExtra("selected user") as User?
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //reuse explore frgment layout as similar
        val root = inflater?.inflate(R.layout.fragment_favourites, container, false)

        activity.title = "Favourites"

        favouritePresenter = FavouritePresenter(this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = favouriteAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        noFavouritesText = root.findViewById<TextView>(R.id.text_no_favourites)

        //user is viewing someone else's favourite list
        if(user!=null){
            val userId = user?.id
            favouritePresenter.loadFavouriteVinyls(userId!!)
        }
        //user is viewing their own list
        else{
            favouritePresenter.loadFavouriteVinyls()
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        if (favouriteAdapter.itemCount > 0) noFavouritesText?.visibility = View.GONE
    }

    override fun setPresenter(presenter: FavouriteContract.Presenter) {
        favouritePresenter = presenter
    }

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show()
    }

    override fun showFavouriteVinyls(vinyls: List<VinylRelease>) {
        favouriteAdapter.addVinyls(vinyls)
    }

    override fun showMessageView(message : String) {
        noFavouritesText?.text = message
        noFavouritesText?.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("got here mate and requestCode= $requestCode and result is $resultCode")
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val vinylRelease = data?.getSerializableExtra("deletedVinyl") as VinylRelease
            favouriteAdapter.removeVinyl(vinylRelease)
        }
    }

    class FavouriteAdapter(private var vinyls: ArrayList<VinylRelease>,private val fragment : FavouriteFragment) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

        fun addVinyls(vinyls : List<VinylRelease>){
            this.vinyls.addAll(vinyls)
            notifyDataSetChanged()
        }

        fun removeVinyl(removedRelease : VinylRelease){
            val position = vinyls.indexOfFirst { removedRelease.id == it.id}
            this.vinyls.removeAt(position)
            notifyItemChanged(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.explore_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
            holder.bindItems(vinyls[position])

            holder.itemView.setOnClickListener {

                val context = holder.itemView.context

                val intent = Intent(context, VinylDetailActivity::class.java)
                intent.putExtra("selected vinyl", vinyls[position])
                fragment.startActivityForResult(intent,1)
            }
        }

        override fun getItemCount(): Int {
            return vinyls.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(vinyl: VinylRelease) {
                itemView.album_name.text = vinyl.title
                itemView.artist_name.text=vinyl.year
                itemView.code.text = vinyl.catno

                if(!vinyl.thumb.isNullOrEmpty()) {
                    Picasso.with(itemView.context).load(vinyl.thumb).into(itemView.cover_art)
                }
            }
        }
    }


}