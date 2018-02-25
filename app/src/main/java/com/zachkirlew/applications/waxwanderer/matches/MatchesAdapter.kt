package com.zachkirlew.applications.waxwanderer.matches

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.message.MessageActivity
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import com.zachkirlew.applications.waxwanderer.util.CircleTransform
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import kotlinx.android.synthetic.main.match_item.view.*

class MatchesAdapter(private var matches: ArrayList<User>, private val callback : OnMatchDeletedListener) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

    fun addMatch(match : User?){
        if (match != null) {
            this.matches.add(match)
        }
        notifyItemInserted(matches.size-1)
    }

    fun removeMatch(userId: String?) {
        val position = matches.indexOfFirst { userId == it.id}
        this.matches.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false) as ListItemView
        return ViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: MatchesAdapter.ViewHolder, position: Int) {
        holder.bindItems(matches[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("matchedUserId",matches[position])
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    class ViewHolder(itemView: ListItemView, private val callback : OnMatchDeletedListener) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(match: User) {

            itemView.list_item_view.title = match.name
            itemView.list_item_view.subtitle = match.location


            Picasso.with(itemView.context)
                    .load(match.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .transform(CircleTransform())
                    .into(itemView.list_item_view.avatarView)

            itemView.list_item_view.avatarView.setOnClickListener {

                val context = itemView.context

                val intent = Intent(context, UserDetailActivity::class.java)
                intent.putExtra("selected user", match)
                context.startActivity(intent)
            }

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.action_remove -> {
                        callback.onMatchDeleted(match.id)
                    }
                }
            }
        }
    }
}

