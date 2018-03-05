package com.waxwanderer.message

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
import com.waxwanderer.R
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.message.ShareVinylDialogFragment.FavouriteAdapter.OnShareClickedListener
import kotlinx.android.synthetic.main.vinyl_item.view.*


class ShareVinylDialogFragment : DialogFragment() {

    private var mRecyclerView: RecyclerView? = null
    private lateinit var favouriteAdapter : FavouriteAdapter

    var shareCallback: FavouriteAdapter.OnShareClickedListener? = null
    var dismissCallback: DialogInterface.OnDismissListener? = null

    @Override
    override  fun  onCreateDialog(savedInstanceState : Bundle?) : Dialog {
        val dialog = AlertDialog.Builder(activity!!)
                .setTitle("Recommend a vinyl")
                .setNegativeButton("Cancel",{dialogInterface: DialogInterface?, i: Int ->
                    dismissCallback?.onDismiss(dialogInterface)
                    dialogInterface?.dismiss()
                })

        val list = arguments?.getSerializable("favouriteList") as List<VinylRelease>

        val  rootView = activity?.layoutInflater?.inflate(R.layout.fragment_favourites, null)

        mRecyclerView = rootView?.findViewById(R.id.vinyl_list) as RecyclerView
        mRecyclerView?.layoutManager = LinearLayoutManager(context)


        favouriteAdapter = FavouriteAdapter(list,shareCallback)
        mRecyclerView?.adapter = favouriteAdapter

        dialog.setView(rootView)
        return dialog.create()
    }

    fun setOnDismissedListener(mCallback: DialogInterface.OnDismissListener) {
        this.dismissCallback = mCallback
    }

    fun setOnShareClickedListener(mCallback: OnShareClickedListener) {
        this.shareCallback = mCallback
    }


    class FavouriteAdapter(private var vinyls: List<VinylRelease>, private val mCallback : OnShareClickedListener?) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

        interface OnShareClickedListener {
            fun onShared(sharedVinyl: VinylRelease)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.vinyl_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
            holder.bindItems(vinyls[position])

            holder.itemView.setOnClickListener {
                mCallback?.onShared(vinyls[position])
            }
        }

        override fun getItemCount(): Int {
            return vinyls.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(vinyl: VinylRelease) {
                itemView.list_item_view.title = vinyl.title
                itemView.list_item_view.subtitle = "${vinyl.year}\n${vinyl.catno}"


                if(!vinyl.thumb.isNullOrEmpty()) {

                    Picasso.with(itemView.context)
                            .load(vinyl.thumb)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(itemView.list_item_view.avatarView)
                }

            }
        }
    }
}