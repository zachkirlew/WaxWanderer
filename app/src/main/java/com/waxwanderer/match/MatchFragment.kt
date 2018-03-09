package com.waxwanderer.match

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.facebook.FacebookSdk.getApplicationContext
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.listeners.ItemRemovedListener
import com.waxwanderer.R
import com.waxwanderer.base.OnSignOutListener
import com.waxwanderer.data.local.UserPreferences
import com.waxwanderer.data.model.User
import com.waxwanderer.data.model.UserCard
import com.waxwanderer.data.remote.notification.PushHelper
import com.waxwanderer.recommendations.RecommendationsActivity


class MatchFragment : Fragment(), MatchContract.View, OnSignOutListener, OnSwipeListener, ItemRemovedListener {

    private lateinit var matchPresenter: MatchContract.Presenter

    private lateinit var mSwipeView: SwipePlaceHolderView

    private lateinit var likeButton: ImageButton
    private lateinit var dislikeButton: ImageButton

    private lateinit var progressBar: ProgressBar

    private lateinit var noUsersText : TextView

    private lateinit var mContext: Context

    private val TAG = MatchFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity?.title = "Match"

        val view = inflater.inflate(R.layout.fragment_match, container, false)
        val root = init(view)

        SwipeViewBuilderInstance(mSwipeView)

        mSwipeView.addItemRemoveListener(this)

        mContext = getApplicationContext()

        setHasOptionsMenu(true)

        matchPresenter = MatchPresenter(this, UserPreferences(), PushHelper.getInstance(activity!!))


        likeButton.setOnClickListener {
            mSwipeView.doSwipe(true)
        }

        dislikeButton.setOnClickListener {
            mSwipeView.doSwipe(false)
        }

        return root
    }

    @Override
    override fun onCreateOptionsMenu( menu : Menu,  inflater : MenuInflater) {
        inflater.inflate(R.menu.menu_match_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun init(root : View?) : View{

        mSwipeView = root?.findViewById(R.id.swipeView) as SwipePlaceHolderView
        progressBar = root.findViewById(R.id.progress_bar_match) as ProgressBar
        likeButton = root.findViewById(R.id.acceptBtn) as ImageButton
        dislikeButton = root.findViewById(R.id.rejectBtn) as ImageButton
        noUsersText = root.findViewById(R.id.text_no_users) as TextView

        return root
    }

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show()
    }

    override fun showMatchDialog(likedUserName: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage("You became friends with $likedUserName")
                .setPositiveButton("Okay", { dialog, _ ->
                    dialog.dismiss()
                })
        builder.create().show()
    }

    override fun setPresenter(presenter: MatchContract.Presenter) {
        matchPresenter = presenter
    }

    override fun addUserCard(userCard: UserCard) {
        progressBar.visibility = View.GONE
        mSwipeView.addView(UserCardView(mContext,userCard,mSwipeView,this))
    }

    override fun onSwipedLeft(user: User, position: Int) {
        matchPresenter.interactWithUser(user,true)

    }

    override fun onSwipedRight(user: User, position: Int) {
        matchPresenter.interactWithUser(user,false)

    }

    override fun onItemRemoved(count: Int) {
        Log.i(TAG,"Removed item, left:" + count)
        if(count==0)
            noUsersText.visibility = View.VISIBLE

    }

    override fun startRecommendationsActivity() {
        val intent = Intent(activity, RecommendationsActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_recommendations -> startRecommendationsActivity()
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        matchPresenter.dispose()
    }

    override fun onResume() {
        super.onResume()
        matchPresenter.start()
    }

    override fun onSignOut() {
        matchPresenter.dispose()
    }
}