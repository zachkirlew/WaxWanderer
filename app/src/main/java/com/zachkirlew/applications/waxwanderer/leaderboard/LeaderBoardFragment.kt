package com.zachkirlew.applications.waxwanderer.leaderboard

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
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration


class LeaderBoardFragment : Fragment(), LeaderBoardContract.View,OnSignOutListener{

    private lateinit var leaderboardPresenter : LeaderBoardContract.Presenter
    private lateinit var leaderboardAdapter: LeaderBoardAdapter
    private var noMatchesText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        leaderboardAdapter = LeaderBoardAdapter(ArrayList<User>(0))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_leader_board, container, false)

        activity?.title = "Leader Board"

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

    override fun showUsers(users: List<User>) {
        leaderboardAdapter.addUsers(users)
    }

    override fun onStop() {
        super.onStop()
        leaderboardPresenter.dispose()
    }

    override fun onSignOut() {
        leaderboardPresenter.dispose()
    }
}