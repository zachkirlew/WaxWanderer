package com.zachkirlew.applications.waxwanderer.explore

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.VinylRelease
import kotlinx.android.synthetic.main.explore_item.view.*


class ExploreFragment: Fragment(), ExploreContract.View {

    private lateinit var explorePresenter : ExploreContract.Presenter

    private lateinit var exploreAdapter: ExploreFragment.ExploreAdapter

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exploreAdapter = ExploreAdapter(listOf<VinylRelease>())
    }

    override fun onResume() {
        super.onResume()
        explorePresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_explore, container, false)

        val exploreList = root?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = exploreAdapter


        return root
    }


    override fun setPresenter(presenter: ExploreContract.Presenter) {
        explorePresenter = presenter
    }

    override fun showVinylReleases(vinyls: List<VinylRelease>) {
        exploreAdapter.addVinyls(vinyls)
    }

    override fun showVinylReleaseDetailsUI() {

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

    class ExploreAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {


        fun addVinyls(vinyls : List<VinylRelease>){
            this.vinyls = vinyls
            notifyDataSetChanged()

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.explore_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ExploreAdapter.ViewHolder, position: Int) {
            holder.bindItems(vinyls[position])
        }

        override fun getItemCount(): Int {
            return vinyls.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(vinyl: VinylRelease) {
                itemView.album_name.text = vinyl.title
                itemView.artist_name.text=vinyl.year
                itemView.code.text = vinyl.catno

            }
        }

    }


}