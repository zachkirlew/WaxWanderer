package com.zachkirlew.applications.waxwanderer.matches

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.message.MessageActivity
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import com.zachkirlew.applications.waxwanderer.util.CircleTransform
import com.zachkirlew.applications.waxwanderer.util.RecyclerItemDecoration
import kotlinx.android.synthetic.main.match_item.view.*


class MatchesFragment: Fragment(), MatchesContract.View {
    private lateinit var matchesPresenter : MatchesContract.Presenter

    private lateinit var matchesAdapter: MatchesFragment.MatchesAdapter

    private var noMatchesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchesAdapter = MatchesAdapter(ArrayList<User>(0))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //reuse explore frgment layout as similar
        val root = inflater?.inflate(R.layout.fragment_matches, container, false)

        activity.title = "Matches"

        matchesPresenter = MatchesPresenter(this)

        val exploreList = root?.findViewById<RecyclerView>(R.id.matches_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = matchesAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(RecyclerItemDecoration(spacingInPixels))

        noMatchesText = root.findViewById<TextView>(R.id.text_no_matches) as TextView

        matchesPresenter.loadMatches()

        return root
    }


    override fun setPresenter(presenter: MatchesContract.Presenter) {
        matchesPresenter = presenter
    }

    override fun addMatch(match: User) {
        matchesAdapter.addMatch(match)
    }

    override fun startMessagesActivity() {
        val intent = Intent(activity, MessageActivity::class.java)
        startActivity(intent)
    }


    override fun showNoMatchesView() {
        noMatchesText?.visibility = View.VISIBLE
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_filter -> showFilteringPopUpMenu()
//            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }

    //Explore adapter

    class MatchesAdapter(private var matches: ArrayList<User>) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

        fun addMatch(match : User){
            this.matches.add(match)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false) as ListItemView
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: MatchesAdapter.ViewHolder, position: Int) {
            holder.bindItems(matches[position])

            holder.itemView.setOnClickListener {

                val context = holder.itemView.context

                val intent = Intent(context, UserDetailActivity::class.java)
                intent.putExtra("selected user", matches[position])
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return matches.size
        }

        class ViewHolder(itemView: ListItemView) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(match: User) {

                itemView.list_item_view.title = match.name
                itemView.list_item_view.subtitle = match.location


                Picasso.with(itemView.context)
                        .load(match.imageurl)
                        .placeholder(R.drawable.ic_male_user_profile_picture)
                        .transform(CircleTransform())
                        .into(itemView.list_item_view.avatarView)


                itemView.list_item_view.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_message -> {

                            val intent = Intent(itemView.context, MessageActivity::class.java)
                            intent.putExtra("matchId",match.id)
                            itemView.context.startActivity(intent)
                        }
                        R.id.action_remove -> println("Removed")
                    }
                }
            }
        }
    }


}