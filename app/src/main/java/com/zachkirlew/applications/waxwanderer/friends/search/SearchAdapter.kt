package com.zachkirlew.applications.waxwanderer.friends.search

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import com.zachkirlew.applications.waxwanderer.util.CircleTransform
import kotlinx.android.synthetic.main.friend_item.view.*

class SearchAdapter(private var users: ArrayList<User>, private val callback : OnRequestSentListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun addUser(user : User?){
        if (user != null) {
            this.users.add(user)
        }
        notifyItemInserted(users.size-1)
    }

    fun clear(){
        this.users.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false) as ListItemView
        return ViewHolder(v,callback)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.bindItems(users[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, UserDetailActivity::class.java)
            intent.putExtra("selected user", users[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(itemView: ListItemView,val callback: OnRequestSentListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: User) {

            itemView.list_item_view.title = user.name
            itemView.list_item_view.subtitle = user.location


            Picasso.with(itemView.context)
                    .load(user.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .transform(CircleTransform())
                    .into(itemView.list_item_view.avatarView)

            itemView.list_item_view.inflateMenu(R.menu.friend_search_action_menu)

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.action_send_request -> {
                        callback.onRequestSent(user)
                    }
                }
            }
        }
    }
}

