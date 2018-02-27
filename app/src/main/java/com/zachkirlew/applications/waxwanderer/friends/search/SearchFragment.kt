package com.zachkirlew.applications.waxwanderer.friends.search

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
import com.zachkirlew.applications.waxwanderer.data.remote.notification.PushHelper
import com.zachkirlew.applications.waxwanderer.util.EqualSpaceItemDecoration
import java.util.regex.Pattern


class SearchFragment : Fragment(), SearchContract.View, OnSignOutListener, OnRequestSentListener {

    private lateinit var friendsPresenter : SearchContract.Presenter

    private lateinit var friendsAdapter: SearchAdapter

    private lateinit var friendsList : RecyclerView

    private var searchPromptText: TextView? = null

    private var isSearching : Boolean = false

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        friendsAdapter = SearchAdapter(ArrayList(0),this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_search_friends, container, false)

        friendsPresenter = SearchPresenter(this, PushHelper.getInstance(activity!!))

        friendsList = root?.findViewById(R.id.friends_list) as RecyclerView
        searchPromptText = root.findViewById(R.id.text_search_prompt) as TextView

        setUpAdapter()


        return root
    }

    override fun handleSearch(queryText: String?) {
        if(!isSearching){
            isSearching = true
            friendsAdapter.clear()
            friendsPresenter.loadUsers(queryText)
            println(queryText)
        }
    }

    override fun setIsSearching(isSearching: Boolean) {
        this.isSearching = isSearching
    }

    override fun setPresenter(presenter: SearchContract.Presenter) {
        friendsPresenter = presenter
    }

    override fun showUser(user: User?) {
        friendsAdapter.addUser(user)
    }

    override fun showMessage(message: String?) {
    }


    override fun onRequestSent(user: User) {
       friendsPresenter.sendFriendRequest(user)
    }

    override fun showNoUsersView(show : Boolean) {
        if(show){
            searchPromptText?.visibility = View.VISIBLE
            searchPromptText?.text = "No users to display"
        }
        else
            searchPromptText?.visibility = View.GONE
    }

    override fun onSignOut() {
        friendsPresenter.dispose()
    }

    override fun onPause() {
        super.onPause()
        friendsPresenter.dispose()
    }

    private fun setUpAdapter(){
        val mLayoutManager = LinearLayoutManager(activity)

        friendsList.layoutManager = mLayoutManager
        friendsList.adapter = friendsAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        friendsList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))
    }
}