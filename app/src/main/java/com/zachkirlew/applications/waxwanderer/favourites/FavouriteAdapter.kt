package com.zachkirlew.applications.waxwanderer.favourites

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import kotlinx.android.synthetic.main.vinyl_item.view.*


class FavouriteAdapter(private var vinyls: ArrayList<VinylRelease>,
                       private val fragment: FavouriteFragment,
                       private val callback: OnFavouriteRemovedListener) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>(), Filterable {

    private val mFilter: CustomFilter

    private val copy = vinyls

    init {
        mFilter = CustomFilter(this)
    }

    fun addVinyls(vinyls: List<VinylRelease>) {
        this.vinyls.clear()
        this.vinyls.addAll(vinyls)
        notifyDataSetChanged()
    }

    fun sortVinyls(sortBy : String){
        when(sortBy){
            "Release title" -> {vinyls.sortBy { it.title} }
            "Year" -> vinyls.sortBy { it.year }
            "Catalogue number" -> vinyls.sortBy { it.catno }
            else -> return
        }
        notifyDataSetChanged()
    }

    fun removeVinyl(vinylId: Int) {
        val position = vinyls.indexOfFirst { vinylId == it.id }
        this.vinyls.removeAt(position)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.vinyl_item, parent, false)
        return ViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
        holder.bindItems(vinyls[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, VinylDetailActivity::class.java)
            intent.putExtra("selected vinyl", vinyls[position])
            fragment.startActivityForResult(intent, 1)
        }
    }

    override fun getItemCount(): Int {
        return vinyls.size
    }

    inner class CustomFilter constructor(private val mAdapter: FavouriteAdapter) : Filter() {

        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            vinyls = copy

            val filteredVinyls = ArrayList<VinylRelease>()

            val results = Filter.FilterResults()
            if (constraint.isEmpty()) {
                filteredVinyls.addAll(vinyls)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                vinyls.filterTo(filteredVinyls) { it.title?.toLowerCase()?.contains(filterPattern)!! }
            }
            System.out.println("Count Number " + filteredVinyls.size)
            results.values = filteredVinyls
            results.count = filteredVinyls.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            mAdapter.vinyls = (results.values as ArrayList<VinylRelease>)
            mAdapter.notifyDataSetChanged()
        }
    }

    class ViewHolder(itemView: View, private val callback: OnFavouriteRemovedListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(vinyl: VinylRelease) {
            itemView.list_item_view.title = vinyl.title
            itemView.list_item_view.subtitle = "${vinyl.year}\n${vinyl.catno}"

            if (!vinyl.thumb.isNullOrEmpty()) {

                Picasso.with(itemView.context)
                        .load(vinyl.thumb)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(itemView.list_item_view.avatarView)
            }

            itemView.list_item_view.inflateMenu(R.menu.favourite_action_menu)

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.action_remove -> {
                        callback.onFavouriteRemoved(vinyl.id!!)
                    }
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return mFilter
    }
}
