package com.zachkirlew.applications.waxwanderer.match

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import kotlinx.android.synthetic.main.user_card_favourite_item.view.*


class UserCardFavouriteAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<UserCardFavouriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardFavouriteAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_card_favourite_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserCardFavouriteAdapter.ViewHolder, position: Int) {
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(vinyl: VinylRelease) {
            itemView.album_name.text = vinyl.title

            if(!vinyl.thumb.isNullOrEmpty()) {
                Picasso.with(itemView.context).load(vinyl.thumb).into(itemView.cover_art)
            }
        }
    }
}