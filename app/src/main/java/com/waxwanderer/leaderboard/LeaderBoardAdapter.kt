package com.waxwanderer.leaderboard

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.waxwanderer.R
import com.waxwanderer.data.model.User
import com.waxwanderer.user_detail.UserDetailActivity
import com.waxwanderer.util.CircleTransform
import kotlinx.android.synthetic.main.leader_board_item.view.*

class LeaderBoardAdapter(private var users: List<User>) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    fun addUsers(users : List<User>){
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.leader_board_item, parent, false) as ListItemView
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LeaderBoardAdapter.ViewHolder, position: Int) {
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

    class ViewHolder(itemView: ListItemView) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: User) {

            itemView.list_item_view.title = user.name
            itemView.list_item_view.subtitle = user.score.toString()

            Picasso.with(itemView.context)
                    .load(user.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .transform(CircleTransform())
                    .into(itemView.list_item_view.avatarView)
        }
    }
}