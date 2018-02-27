package com.zachkirlew.applications.waxwanderer.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.explore.OnAddToFavouritesListener
import com.zachkirlew.applications.waxwanderer.explore.OnLongPressListener
import com.zachkirlew.applications.waxwanderer.recommend.RecommendVinylDialogFragment
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import kotlinx.android.synthetic.main.vinyl_item.view.*

class SearchAdapter(private var vinyls: ArrayList<VinylRelease>,
                     private val callback: OnAddToFavouritesListener,
                     private val longPressCallback: OnLongPressListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val TAG = SearchAdapter::class.java.simpleName

    fun addVinyls(vinyls : List<VinylRelease>){
        this.vinyls.addAll(vinyls)
        notifyDataSetChanged()
        Log.i(TAG,"Adapter count: " + this.vinyls.size)
    }

    fun removeVinyls(){
        this.vinyls.clear()
        notifyDataSetChanged()
        Log.i(TAG,"Adapter count: " + this.vinyls.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.vinyl_item, parent, false)
        return ViewHolder(v,callback)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.bindItems(vinyls[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, VinylDetailActivity::class.java)
            intent.putExtra("selected vinyl", vinyls[position])
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            longPressCallback.onLongPress(vinyls[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return vinyls.size
    }

    class ViewHolder(itemView: View, private val callback: OnAddToFavouritesListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(vinyl: VinylRelease) {

            itemView.list_item_view.title = vinyl.title
            itemView.list_item_view.subtitle = "${vinyl.year}\n${vinyl.catno}"

            if(!vinyl.thumb.isNullOrEmpty()) {

                Picasso.with(itemView.context)
                        .load(vinyl.thumb)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(itemView.list_item_view.avatarView)
            }

            itemView.list_item_view.inflateMenu(R.menu.explore_action_menu)

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.action_add -> {
                        callback.onAddedToFavourites(vinyl)
                    }

                    R.id.action_share -> {
                        startShareIntent(itemView.context,vinyl)
                    }
                    R.id.action_recommend -> {
                        openRecommendDialog(itemView.context,vinyl)
                    }
                }
            }
        }

        private fun startShareIntent(context: Context, vinyl: VinylRelease) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this record!\n" + vinyl.title)

            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
        }

        private fun openRecommendDialog(context: Context, vinyl: VinylRelease) {
            val recommendVinylDialogFragment = RecommendVinylDialogFragment()

            val bundle = Bundle()
            bundle.putSerializable("selectedVinyl", vinyl)

            recommendVinylDialogFragment.arguments = bundle

            val activity = context as AppCompatActivity
            recommendVinylDialogFragment.show(activity.supportFragmentManager, "now")
        }
    }
}