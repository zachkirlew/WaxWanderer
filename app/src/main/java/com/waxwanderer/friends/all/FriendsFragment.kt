package com.waxwanderer.friends.all

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.waxwanderer.R
import com.waxwanderer.base.OnSignOutListener
import com.waxwanderer.data.model.User
import com.waxwanderer.message.MessageActivity
import com.waxwanderer.util.EqualSpaceItemDecoration


class FriendsFragment : Fragment(), FriendsContract.View, OnFriendDeletedListener, OnSignOutListener {

    private lateinit var friendsPresenter : FriendsContract.Presenter

    private lateinit var friendsAdapter: FriendsAdapter

    private lateinit var friendsList : RecyclerView

    private var noFriendsText: TextView? = null

    private val coordinatorLayout : CoordinatorLayout by lazy{activity!!.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)}

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        friendsAdapter = FriendsAdapter(ArrayList(0), this)
    }

    @Override
    override fun onPrepareOptionsMenu(menu: Menu?) {
        val item = menu?.findItem(R.id.action_search)
        item?.isVisible = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_friends, container, false)

        friendsPresenter = FriendsPresenter(this)

        friendsList = root?.findViewById(R.id.friends_list) as RecyclerView
        noFriendsText = root.findViewById(R.id.text_no_friends) as TextView

        setUpAdapter()

        friendsPresenter.loadMatches()

        return root
    }

    private fun setUpAdapter(){
        val mLayoutManager = LinearLayoutManager(activity)

        friendsList.layoutManager = mLayoutManager
        friendsList.adapter = friendsAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        friendsList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))
    }

    override fun setPresenter(presenter: FriendsContract.Presenter) {
        friendsPresenter = presenter
    }

    override fun showFriend(user: User?) {
        friendsAdapter.addMatch(user)
    }

    override fun removeFriendFromList(userId: String) {
        friendsAdapter.removeMatch(userId)
    }

    override fun showMessage(message: String?) {
        message?.let { Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).show() }
    }

    override fun startMessagesActivity() {
        val intent = Intent(activity, MessageActivity::class.java)
        startActivity(intent)
    }

    override fun showNoFriendsView(show : Boolean) {
        if(show) noFriendsText?.visibility = View.VISIBLE else noFriendsText?.visibility = View.GONE
    }

    override fun onFriendDeleted(id: String?) {
        id?.let { friendsPresenter.deleteMatch(it) }
    }

    override fun onSignOut() {
        friendsPresenter.dispose()
    }

    override fun onPause() {
        super.onPause()
        friendsPresenter.dispose()
    }
}