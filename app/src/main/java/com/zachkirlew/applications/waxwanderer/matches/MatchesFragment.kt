package com.zachkirlew.applications.waxwanderer.matches

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.message.MessageActivity
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration


class MatchesFragment: Fragment(), MatchesContract.View, OnMatchDeletedListener,OnSignOutListener {

    private lateinit var matchesPresenter : MatchesContract.Presenter

    private lateinit var matchesAdapter: MatchesAdapter

    private var noMatchesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchesAdapter = MatchesAdapter(ArrayList<User>(0),this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //reuse explore frgment layout as similar
        val root = inflater.inflate(R.layout.fragment_matches, container, false)

        activity?.title = "Friends"

        matchesPresenter = MatchesPresenter(this)

        val exploreList = root?.findViewById(R.id.matches_list) as RecyclerView

        val mLayoutManager = LinearLayoutManager(activity)

        exploreList.layoutManager = mLayoutManager
        exploreList.adapter = matchesAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        exploreList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))

        noMatchesText = root.findViewById(R.id.text_no_matches) as TextView

        matchesPresenter.loadMatches()

        return root
    }

    override fun setPresenter(presenter: MatchesContract.Presenter) {
        matchesPresenter = presenter
    }

    override fun addMatch(user: User?) {
        matchesAdapter.addMatch(user)
    }

    override fun removeMatch(userId: String) {
        matchesAdapter.removeMatch(userId)
    }

    override fun showMessage(message: String?) {
    }

    override fun startMessagesActivity() {
        val intent = Intent(activity, MessageActivity::class.java)
        startActivity(intent)
    }

    override fun showNoMatchesView(show : Boolean) {
        if(show) noMatchesText?.visibility = View.VISIBLE else noMatchesText?.visibility = View.GONE
    }

    override fun onMatchDeleted(matchId: String?) {
        matchId?.let { matchesPresenter.deleteMatch(it) }
    }

    override fun onSignOut() {
        matchesPresenter.dispose()
    }

    override fun onPause() {
        super.onPause()
        matchesPresenter.dispose()
    }
}