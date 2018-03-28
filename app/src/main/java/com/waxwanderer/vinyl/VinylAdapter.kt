package com.waxwanderer.vinyl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.squareup.picasso.Picasso
import com.waxwanderer.R
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.vinyl_detail.VinylDetailActivity
import com.waxwanderer.recommend.RecommendVinylDialogFragment
import kotlinx.android.synthetic.main.vinyl_item.view.*

class VinylAdapter(private var vinyls: ArrayList<VinylRelease>,
                   private val callback : OnVinylsChangedListener,
                   private val longPressCallback: OnLongPressListener,
                   private val isFavouriteAdapter : Boolean) : RecyclerView.Adapter<VinylAdapter.ViewHolder>(), Filterable {

    private val mFilter: CustomFilter

    private val copy = vinyls

    private val TAG = VinylAdapter::class.java.simpleName

    init {
        mFilter = CustomFilter(this)
    }

    fun addVinyls(vinyls: List<VinylRelease>) {
        if(isFavouriteAdapter)
            this.vinyls.clear()
        this.vinyls.addAll(vinyls)
        notifyDataSetChanged()
        Log.i(TAG,"Adapter count: " + this.vinyls.size)
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

    fun removeVinyls(){
        this.vinyls.clear()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.vinyl_item, parent, false)
        return ViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(vinyls[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, VinylDetailActivity::class.java)
            intent.putExtra("selected vinyl", vinyls[position])
            (context as AppCompatActivity).startActivityForResult(intent, 1)
        }

        holder.itemView.setOnLongClickListener {
            longPressCallback.onLongPress(vinyls[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return vinyls.size
    }

    inner class CustomFilter constructor(private val mAdapter: VinylAdapter) : Filter() {

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
            results.values = filteredVinyls
            results.count = filteredVinyls.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            mAdapter.vinyls = (results.values as ArrayList<VinylRelease>)
            mAdapter.notifyDataSetChanged()

            callback.onFiltered(mAdapter.vinyls.count()== 0)
        }
    }

    inner class ViewHolder(itemView: View, private val callback: OnVinylsChangedListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(vinyl: VinylRelease) {
            itemView.list_item_view.title = vinyl.title
            itemView.list_item_view.subtitle = "${vinyl.year}\n${vinyl.catno}"

            if (!vinyl.thumb.isNullOrEmpty()) {

                Picasso.with(itemView.context)
                        .load(vinyl.thumb)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(itemView.list_item_view.avatarView)
            }

            if(isFavouriteAdapter)
                itemView.list_item_view.inflateMenu(R.menu.favourite_action_menu)
            else{
                itemView.list_item_view.inflateMenu(R.menu.vinyl_action_menu)
            }

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.action_add -> {
                        callback.onAddedToFavourites(vinyl)
                    }

                    R.id.action_remove -> {
                        callback.onRemovedFromFavourites(vinyl.id!!)
                    }

                    R.id.action_share -> {
                        createDynamicLink(itemView.context,vinyl)
                    }
                    R.id.action_recommend -> {
                        openRecommendDialog(itemView.context,vinyl)
                    }
                }
            }
        }

        private fun startShareIntent(context: Context, link: String, vinyl: VinylRelease) {

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this record!\n\n" + vinyl.title + "\n\n$link")

            context.startActivity(Intent.createChooser(shareIntent, "Share with"))
        }

        private fun createDynamicLink(context: Context, vinyl: VinylRelease) {
            val dynamicLinkDomain = context.getString(R.string.dynamic_link_domain)
            val deepLinkUrl = context.getString(R.string.deep_link_url)

            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("$deepLinkUrl/track?id=${vinyl.id}"))
                    .setDynamicLinkDomain(dynamicLinkDomain)
                    .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                    .buildDynamicLink()

            shortenLink(context,dynamicLink.uri.toString(),vinyl)
        }

        private fun shortenLink(context : Context,longLink: String, vinyl: VinylRelease) {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(Uri.parse(longLink))
                    .buildShortDynamicLink()
                    .addOnCompleteListener((context as AppCompatActivity), {
                        if (it.isSuccessful) {
                            val shortLink = it.result.shortLink.toString()

                            startShareIntent(itemView.context,shortLink,vinyl)
                        }
                        else {
                            Log.e("FavAdapter",it.exception?.message)
                        }
                    })
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

    override fun getFilter(): Filter {
        return mFilter
    }
}