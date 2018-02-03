package com.zachkirlew.applications.waxwanderer.matches

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
import com.zachkirlew.applications.waxwanderer.message.MessageActivity
import com.zachkirlew.applications.waxwanderer.user_detail.UserDetailActivity
import com.zachkirlew.applications.waxwanderer.util.CircleTransform
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import kotlinx.android.synthetic.main.match_item.view.*


class MatchesFragment: Fragment(), MatchesContract.View{

    private lateinit var matchesPresenter : MatchesContract.Presenter

    private lateinit var matchesAdapter: MatchesAdapter

    private var noMatchesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchesAdapter = MatchesAdapter(ArrayList<User>(0),this)
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
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

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

    override fun showMessage(message: String?) {
    }

    override fun startMessagesActivity() {
        val intent = Intent(activity, MessageActivity::class.java)
        startActivity(intent)
    }

    override fun clearMatches() {
        matchesAdapter.clear()
    }

    override fun showNoMatchesView(show : Boolean) {
        if(show) noMatchesText?.visibility = View.VISIBLE else noMatchesText?.visibility = View.GONE
    }

    fun onMatchDeleted(matchId: String?) {
        //remove match from UI
//        matchesAdapter.remove(matchId)
        matchId?.let { matchesPresenter.deleteMatch(it) }
    }
}