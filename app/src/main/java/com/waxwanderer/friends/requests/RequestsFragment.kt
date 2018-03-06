package com.waxwanderer.friends.requests

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.waxwanderer.R
import com.waxwanderer.base.OnSignOutListener
import com.waxwanderer.data.model.User
import com.waxwanderer.data.remote.notification.PushHelper
import com.waxwanderer.util.EqualSpaceItemDecoration

class RequestsFragment : Fragment(), RequestsContract.View, OnSignOutListener, OnRequestInteractionListener {


    private lateinit var requestsPresenter : RequestsContract.Presenter

    private lateinit var requestsAdapter: RequestsAdapter

    private lateinit var requestList : RecyclerView

    private var noRequestsText: TextView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        requestsAdapter = RequestsAdapter(ArrayList(0),this)
    }

    @Override
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item : MenuItem? = menu.findItem(R.id.action_search)
        item?.isVisible = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_requests, container, false)

        requestsPresenter = RequestsPresenter(this, PushHelper.getInstance(activity!!))

        requestList = root?.findViewById(R.id.requests_list) as RecyclerView
        noRequestsText = root.findViewById(R.id.text_no_requests) as TextView

        setUpAdapter()

        requestsPresenter.loadRequests()

        return root
    }

    private fun setUpAdapter(){
        val mLayoutManager = LinearLayoutManager(activity)

        requestList.layoutManager = mLayoutManager
        requestList.adapter = requestsAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        requestList.addItemDecoration(EqualSpaceItemDecoration(spacingInPixels))
    }

    override fun setPresenter(presenter: RequestsContract.Presenter) {
        requestsPresenter = presenter
    }

    override fun showRequest(user: User?) {
        requestsAdapter.addRequest(user)
    }

    override fun removeRequestFromList(userId: String) {
        requestsAdapter.removeRequest(userId)
    }

    override fun showMessage(message: String?) {
    }

    override fun onRequestAccepted(user: User) {
        requestsPresenter.acceptRequest(user)
    }

    override fun showFriendDialog(userName: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage("You became friends with $userName")
                .setPositiveButton("Okay", { dialog, id ->
                    dialog.dismiss()
                })
        builder.create().show()
    }

    override fun onRequestDeleted(id: String?) {
        requestsPresenter.deleteRequest(id!!)
    }

    override fun showNoRequestsView(show : Boolean) {
        if(show) noRequestsText?.visibility = View.VISIBLE else noRequestsText?.visibility = View.GONE
    }

    override fun onSignOut() {
        requestsPresenter.dispose()
    }

    override fun onPause() {
        super.onPause()
        requestsPresenter.dispose()
    }
}