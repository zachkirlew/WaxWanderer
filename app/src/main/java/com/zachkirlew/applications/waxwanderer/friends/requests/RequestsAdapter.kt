package com.zachkirlew.applications.waxwanderer.friends.requests

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


class RequestsAdapter(private var requests: ArrayList<User>, private val callback : OnRequestInteractionListener) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    fun addRequest(request : User?){
        if (request != null) {
            this.requests.add(request)
        }
        notifyItemInserted(requests.size-1)
    }

    fun removeRequest(userId: String?) {
        val position = requests.indexOfFirst { userId == it.id}
        this.requests.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false) as ListItemView
        return ViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: RequestsAdapter.ViewHolder, position: Int) {
        holder.bindItems(requests[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, UserDetailActivity::class.java)
            intent.putExtra("selected user", requests[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    class ViewHolder(itemView: ListItemView, private val callback : OnRequestInteractionListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: User) {

            itemView.list_item_view.title = user.name
            itemView.list_item_view.subtitle = user.location


            Picasso.with(itemView.context)
                    .load(user.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .transform(CircleTransform())
                    .into(itemView.list_item_view.avatarView)

            itemView.list_item_view.inflateMenu(R.menu.requests_action_menu)

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_accept -> {
                        callback.onRequestAccepted(user)
                    }

                    R.id.action_remove -> {
                        callback.onRequestDeleted(user.id)
                    }
                }
            }
        }
    }
}

