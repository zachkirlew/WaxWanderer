package com.zachkirlew.applications.waxwanderer.friends.all

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.message.MessageActivity
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import com.zachkirlew.applications.waxwanderer.util.CircleTransform
import kotlinx.android.synthetic.main.friend_item.view.*


class FriendsAdapter(private var friends: ArrayList<User>, private val callback : OnFriendDeletedListener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    fun addMatch(friend : User?){
        if (friend != null) {
            this.friends.add(friend)
        }
        notifyItemInserted(friends.size-1)
    }

    fun removeMatch(userId: String?) {
        val position = friends.indexOfFirst { userId == it.id}
        this.friends.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false) as ListItemView
        return ViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(friends[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("matchedUserId",friends[position])
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    class ViewHolder(itemView: ListItemView, private val callback : OnFriendDeletedListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(friend: User) {

            itemView.list_item_view.title = friend.name
            itemView.list_item_view.subtitle = friend.location

            itemView.list_item_view.inflateMenu(R.menu.matches_action_menu)

            Picasso.with(itemView.context)
                    .load(friend.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .transform(CircleTransform())
                    .into(itemView.list_item_view.avatarView)

            itemView.list_item_view.avatarView.setOnClickListener {

                val context = itemView.context

                val intent = Intent(context, UserDetailActivity::class.java)
                intent.putExtra("selected user", friend)
                context.startActivity(intent)
            }

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.action_remove -> {
                        callback.onFriendDeleted(friend.id)
                    }
                }
            }
        }
    }
}

