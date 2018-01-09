package com.zachkirlew.applications.waxwanderer.explore

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.detail_vinyl.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.util.RecyclerItemDecoration
import kotlinx.android.synthetic.main.explore_item.view.*

class ExploreFragment: Fragment(), ExploreContract.View, OnSearchSubmitted{

    private lateinit var explorePresenter : ExploreContract.Presenter

    private lateinit var exploreAdapter: ExploreFragment.ExploreAdapter

    private var noFavouritesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        exploreAdapter = ExploreAdapter(listOf<VinylRelease>())
    }

    override fun onResume() {
        super.onResume()
        explorePresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_explore, container, false)

        explorePresenter = ExplorePresenter(VinylRepository.getInstance(VinylsRemoteSource.instance),this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = exploreAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(RecyclerItemDecoration(spacingInPixels))

        noFavouritesText = root.findViewById<TextView>(R.id.text_no_favourites) as TextView

        return root
    }

    override fun searchSubmitted(searchText: String?) {
        explorePresenter.searchVinylReleases(searchText)
    }

    override fun setPresenter(presenter: ExploreContract.Presenter) {
        explorePresenter = presenter
    }

    override fun showNoVinylsView() {
        exploreAdapter.removeVinyls()
        noFavouritesText?.text = "No vinyls to display. Please search again or change your vinyl preferences"
        noFavouritesText?.visibility = View.VISIBLE
    }

    override fun showVinylReleases(vinyls: List<VinylRelease>) {
        vinyls.forEach { println(it.style) }
        exploreAdapter.addVinyls(vinyls)
    }

    override fun showVinylReleaseDetailsUI() {

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

    class ExploreAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {


        fun addVinyls(vinyls : List<VinylRelease>){
            this.vinyls = vinyls
            notifyDataSetChanged()
        }

        fun removeVinyls(){
            this.vinyls = emptyList()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.explore_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ExploreAdapter.ViewHolder, position: Int) {
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