package com.waxwanderer.recommendations

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
import kotlinx.android.synthetic.main.recommendation_item.view.*

class RecommendationsAdapter(private var recommendations: ArrayList<User>, private var callback : ViewHolder.UserLikeListener) : RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>() {

    fun addUser(match : User){
        this.recommendations.add(match)
        notifyDataSetChanged()
    }

    fun removeUser(position: Int) {
        this.recommendations.removeAt(position)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recommendation_item, parent, false) as ListItemView
        return ViewHolder(v,callback)
    }

    override fun onBindViewHolder(holder: RecommendationsAdapter.ViewHolder, position: Int) {
        holder.bindItems(recommendations[position])

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, UserDetailActivity::class.java)
            intent.putExtra("selected user", recommendations[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recommendations.size
    }

    class ViewHolder(itemView: ListItemView,private var callback: UserLikeListener) : RecyclerView.ViewHolder(itemView) {

        interface UserLikeListener {
            fun onUserLike(userId : String,position: Int)
        }

        fun bindItems(user: User) {

            itemView.list_item_view.title = user.name
            itemView.list_item_view.subtitle = user.location

            Picasso.with(itemView.context)
                    .load(user.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .transform(CircleTransform())
                    .into(itemView.list_item_view.avatarView)

            itemView.list_item_view.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_like -> {
                        callback.onUserLike(user.id!!,adapterPosition)
                    }
                }
            }

        }
    }
}
