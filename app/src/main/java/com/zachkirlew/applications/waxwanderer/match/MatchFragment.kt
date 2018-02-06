package com.zachkirlew.applications.waxwanderer.match

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import com.facebook.FacebookSdk.getApplicationContext
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.base.OnSignOutListener
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.UserCard
import com.zachkirlew.applications.waxwanderer.recommendations.RecommendationsActivity


class MatchFragment : Fragment(), MatchContract.View,OnSignOutListener, OnSwipeLeftListener {

    private lateinit var matchPresenter: MatchContract.Presenter

    private lateinit var mSwipeView: SwipePlaceHolderView

    private lateinit var likeButton: ImageButton
    private lateinit var dislikeButton: ImageButton

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity.title = "Match"

        val root = inflater?.inflate(R.layout.fragment_match, container, false)

        mSwipeView = root?.findViewById<SwipePlaceHolderView>(R.id.swipeView) as SwipePlaceHolderView

        SwipeViewBuilderInstance(mSwipeView)

        mContext = getApplicationContext()

        setHasOptionsMenu(true)

        matchPresenter = MatchPresenter(this, UserPreferences())

        likeButton = root.findViewById<ImageButton>(R.id.acceptBtn) as ImageButton
        dislikeButton = root.findViewById<ImageButton>(R.id.rejectBtn) as ImageButton

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

    override fun showMessage(message: String?) {
        Toast.makeText(activity, message,
                Toast.LENGTH_SHORT).show()
    }

    override fun showMatchDialog(likedUserName: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("You matched with $likedUserName")
                .setPositiveButton("Okay", { dialog, id ->
                    dialog.dismiss()
                })
        builder.create().show()
    }

    override fun setPresenter(presenter: MatchContract.Presenter) {
        matchPresenter = presenter
    }

    override fun addUserCard(userCard: UserCard) {
        mSwipeView.addView(UserCardView(mContext,userCard,mSwipeView,this))
    }


    override fun onSwipedLeft(user: User) {
        matchPresenter.handleLike(user)
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