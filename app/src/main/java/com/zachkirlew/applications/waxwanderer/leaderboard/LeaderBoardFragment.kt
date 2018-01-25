package com.zachkirlew.applications.waxwanderer.leaderboard

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import com.zachkirlew.applications.waxwanderer.util.CircleTransform
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import kotlinx.android.synthetic.main.match_item.view.*


class LeaderBoardFragment : Fragment(), LeaderBoardContract.View{


    private lateinit var leaderboardPresenter : LeaderBoardContract.Presenter

    private lateinit var leaderboardAdapter: LeaderBoardFragment.LeaderBoardAdapter

    private var noMatchesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        leaderboardAdapter = LeaderBoardAdapter(ArrayList<User>(0))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater?.inflate(R.layout.fragment_matches, container, false)

        activity.title = "Leader Board"
        leaderboardPresenter = LeaderBoardPresenter(this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.matches_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true

        exploreList.layoutManager = mLayoutManager

        exploreList.adapter = leaderboardAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        noMatchesText = root.findViewById<TextView>(R.id.text_no_matches) as TextView

        leaderboardPresenter.loadUsers()

        return root
    }

    override fun showMessage(message: String?) {

    }

    override fun setPresenter(presenter: LeaderBoardContract.Presenter) {
        leaderboardPresenter = presenter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_filter -> showFilteringPopUpMenu()
//            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }

    override fun showUsers(users: List<User>) {
        leaderboardAdapter.addUsers(users)
    }

    //Explore adapter

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


}