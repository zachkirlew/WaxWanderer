package com.zachkirlew.applications.waxwanderer.message

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import kotlinx.android.synthetic.main.explore_item.view.*
import com.zachkirlew.applications.waxwanderer.message.ShareVinylDialogFragment.FavouriteAdapter.OnShareClickedListener




class ShareVinylDialogFragment() : DialogFragment() {

    private var mRecyclerView: RecyclerView? = null
    private lateinit var favouriteAdapter : FavouriteAdapter

    var mCallback: FavouriteAdapter.OnShareClickedListener? = null

    @Override
    override  fun  onCreateDialog(savedInstanceState : Bundle?) : Dialog {
        val dialog = AlertDialog.Builder(activity)
                .setTitle("Choose a vinyl")
                .setNegativeButton("Cancel",{dialogInterface: DialogInterface?, i: Int ->
                    dialogInterface?.dismiss()
                })

        val list = arguments.getSerializable("favouriteList") as List<VinylRelease>

        val  rootView = activity.layoutInflater.inflate(R.layout.fragment_favourites, null)

        mRecyclerView = rootView?.findViewById<RecyclerView>(R.id.explore_list) as RecyclerView
        mRecyclerView?.layoutManager = LinearLayoutManager(context)


        favouriteAdapter = FavouriteAdapter(list,mCallback)
        mRecyclerView?.adapter = favouriteAdapter

        dialog.setView(rootView)
        return dialog.create()
    }


    fun setOnShareClickedListener(mCallback: OnShareClickedListener) {
        this.mCallback = mCallback
    }


    class FavouriteAdapter(private var vinyls: List<VinylRelease>,private val mCallback : OnShareClickedListener?) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

        interface OnShareClickedListener {
            fun ShareClicked(sharedVinyl: VinylRelease)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.explore_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
            holder.bindItems(vinyls[position])

            holder.itemView.setOnClickListener {

                val context = holder.itemView.context


                mCallback?.ShareClicked(vinyls[position])

//                val intent = Intent(context, VinylDetailActivity::class.java)
//                intent.putExtra("selected vinyl", vinyls[position])
//                context.startActivity(intent)
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