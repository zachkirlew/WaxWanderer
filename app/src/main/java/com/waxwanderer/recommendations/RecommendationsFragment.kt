package com.waxwanderer.recommendations

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.waxwanderer.data.model.User
import com.waxwanderer.data.recommendation.RecommenderImp
import com.waxwanderer.R


class RecommendationsFragment: Fragment(), RecommendationsContract.View, RecommendationsAdapter.ViewHolder.UserLikeListener {


    private lateinit var presenter: RecommendationsContract.Presenter

    private lateinit var recommendationsList: RecyclerView

    private lateinit var recommendedUsers: ArrayList<User>

    private lateinit var adapter: RecommendationsAdapter

    private lateinit var noRecommendationsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recommendedUsers = ArrayList(0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recommendations, container, false)

        activity?.title = "User recommendations"

        recommendationsList = view.findViewById(R.id.list_recommendations)
        noRecommendationsText = view.findViewById(R.id.text_no_recommendations)

        initializePresenter()

        setupAdapter()
        setupList()

        presenter.start()

        return view
    }

    override fun showRecommendedUser(user: User) {
        noRecommendationsText.visibility = View.GONE
        adapter.addUser(user)
    }

    override fun showMessage(message: String?) {

    }

    override fun onUserLike(userId: String, position: Int) {
        presenter.likeUser(userId,position)
    }

    override fun removeUser(position: Int) {
        adapter.removeUser(position)
        checkIfEmpty()
    }

    override fun showNoRecommendationsView() {
        noRecommendationsText.visibility = View.VISIBLE
    }

    private fun checkIfEmpty(){
        if(adapter.itemCount==0)
            noRecommendationsText.visibility = View.VISIBLE
    }

    private fun initializePresenter() {
        presenter = RecommendationsPresenter(this, RecommenderImp(activity!!))
    }

    private fun setupAdapter() {
        adapter = RecommendationsAdapter(recommendedUsers,this)
    }

    private fun setupList() {
        recommendationsList.layoutManager = LinearLayoutManager(recommendationsList.context)
        recommendationsList.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        presenter.dispose()
    }
}