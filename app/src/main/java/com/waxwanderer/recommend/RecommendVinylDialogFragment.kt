package com.waxwanderer.recommend

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
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.waxwanderer.R
import com.waxwanderer.data.model.User
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.data.remote.notification.PushHelper
import com.waxwanderer.util.CircleTransform
import kotlinx.android.synthetic.main.friend_item.view.*

class RecommendVinylDialogFragment : DialogFragment(), RecommendVinylDialogContract.View {

    private var mRecyclerView: RecyclerView? = null

    private lateinit var userAdapter: UserAdapter

    private var noFriendsText: TextView? = null

    private lateinit var recommendDialogPresenter : RecommendVinylDialogContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userAdapter = UserAdapter(ArrayList(0))
    }

    private lateinit var vinyl: VinylRelease

    @Override
    override  fun  onCreateDialog(savedInstanceState : Bundle?) : Dialog {

        val dialog = AlertDialog.Builder(activity!!)
                .setTitle("Recommend with...")
                .setNegativeButton("Cancel",{ _: DialogInterface?, _: Int ->
                    this.dismiss()
                })

        vinyl = arguments?.getSerializable("selectedVinyl") as VinylRelease

        val  rootView = activity?.layoutInflater?.inflate(R.layout.fragment_friends, null)

        recommendDialogPresenter = RecommendVinylDialogPresenter(this, PushHelper.getInstance(activity!!))

        noFriendsText = rootView?.findViewById(R.id.text_no_friends) as TextView

        mRecyclerView = rootView.findViewById(R.id.friends_list) as RecyclerView
        mRecyclerView?.layoutManager = LinearLayoutManager(context)


        mRecyclerView?.adapter = userAdapter

        recommendDialogPresenter.loadFriends()

        dialog.setView(rootView)

        return dialog.create()
    }


    override fun setPresenter(presenter: RecommendVinylDialogContract.Presenter) {
        recommendDialogPresenter = presenter
    }

    override fun showFriend(user: User?) {
        userAdapter.addFriend(user)
    }

    override fun showNoFriendsView(show: Boolean) {
        if(show) noFriendsText?.visibility = View.VISIBLE else noFriendsText?.visibility = View.GONE
    }


    override fun showMessage(message: String?) {
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        recommendDialogPresenter.dispose()
    }

    inner class UserAdapter(private var friends: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

        fun addFriend(friend : User?){
            if (friend != null) {
                this.friends.add(friend)
            }
            notifyItemInserted(friends.size-1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(friends[position])

            holder.itemView.setOnClickListener {
                recommendDialogPresenter.sendRecommendation(friends[position],vinyl)
            }
        }

        override fun getItemCount(): Int {
            return friends.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(friend: User) {
                itemView.list_item_view.title = friend.name
                itemView.list_item_view.subtitle = friend.location

                Picasso.with(itemView.context)
                        .load(friend.imageurl)
                        .placeholder(R.drawable.ic_male_user_profile_picture)
                        .transform(CircleTransform())
                        .into(itemView.list_item_view.avatarView)
                }
            }
        }
}