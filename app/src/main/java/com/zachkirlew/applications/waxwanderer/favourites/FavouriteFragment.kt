package com.zachkirlew.applications.waxwanderer.favourites

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.detail_vinyl.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.util.RecyclerItemDecoration
import kotlinx.android.synthetic.main.explore_item.view.*


class FavouriteFragment: Fragment(), FavouriteContract.View {

    private lateinit var favouritePresenter : FavouriteContract.Presenter

    private lateinit var favouriteAdapter: FavouriteFragment.FavouriteAdapter

    private var noFavouritesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favouriteAdapter = FavouriteAdapter(listOf<VinylRelease>())
    }

    override fun onResume() {
        super.onResume()
        favouritePresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //reuse explore frgment layout as similar
        val root = inflater?.inflate(R.layout.fragment_explore, container, false)

        favouritePresenter = FavouritePresenter(this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = favouriteAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(RecyclerItemDecoration(spacingInPixels))

        noFavouritesText = root.findViewById<TextView>(R.id.text_no_favourites) as TextView


        return root
    }


    override fun setPresenter(presenter: FavouriteContract.Presenter) {
        favouritePresenter = presenter
    }

    override fun showFavouriteVinyls(vinyls: List<VinylRelease>) {
        vinyls.forEach { println(it.style) }
        favouriteAdapter.addVinyls(vinyls)
    }
    override fun showVinylReleaseDetailsUI() {

    }

    override fun showNoVinylsView() {
        noFavouritesText?.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_clear -> mPresenter.clearCompletedTasks()
//            R.id.menu_filter -> showFilteringPopUpMenu()
//            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }

    //Explore adapter

    class FavouriteAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {


        fun addVinyls(vinyls : List<VinylRelease>){
            this.vinyls = vinyls
            notifyDataSetChanged()

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
                context.startActivity(intent)
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