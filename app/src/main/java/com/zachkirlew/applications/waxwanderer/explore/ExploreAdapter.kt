package com.zachkirlew.applications.waxwanderer.explore

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import kotlinx.android.synthetic.main.explore_item.view.*

class ExploreAdapter(private var vinyls: ArrayList<VinylRelease>) : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    fun addVinyls(vinyls : List<VinylRelease>){
        this.vinyls.addAll(vinyls)
        notifyDataSetChanged()
    }

    fun removeVinyls(){
        this.vinyls.clear()
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