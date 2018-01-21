package com.zachkirlew.applications.waxwanderer.similar_users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.facebook.FacebookSdk.getApplicationContext
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.swipe.*
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import com.zachkirlew.applications.waxwanderer.detail_vinyl.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.favourites.FavouriteActivity
import com.zachkirlew.applications.waxwanderer.util.StringUtils
import kotlinx.android.synthetic.main.top_ten_vinyl_item.view.*
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*


class SimilarUsersFragment : Fragment(), SimilarUsersContract.View {

    private lateinit var similarUsersPresenter : SimilarUsersContract.Presenter

    private lateinit var mSwipeView: SwipePlaceHolderView

    private lateinit var likeButton: ImageButton
    private lateinit var dislikeButton: ImageButton

    private lateinit var mContext: Context

    private lateinit var userCards : List<SimilarUsersFragment.UserCard>

    override fun onResume() {
        super.onResume()
        similarUsersPresenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity.title = "Match"

        val root = inflater?.inflate(R.layout.fragment_similar_users, container, false)

        mSwipeView = root?.findViewById<SwipePlaceHolderView>(R.id.swipeView) as SwipePlaceHolderView

        SwipeViewBuilderInstance(mSwipeView)

        mContext = getApplicationContext()

        setHasOptionsMenu(true)


        similarUsersPresenter = SimilarUsersPresenter(this, UserPreferences(), RecommenderImp(activity))

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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun showMessage(message: String) {
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

    override fun setPresenter(presenter: SimilarUsersContract.Presenter) {
        similarUsersPresenter = presenter
    }


    override fun showSimilarUsers(users: List<User>) {
        userCards = users.mapIndexed { index, user ->
            UserCard(mContext, user, mSwipeView,index)

        }

        userCards.forEach{ mSwipeView.addView(it)}
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_clear -> mPresenter.clearCompletedTasks()
//            R.id.menu_filter -> showFilteringPopUpMenu()
//            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }

    override fun showUserFavourites(vinyls: List<VinylRelease>, viewPosition: Int) {
        userCards[viewPosition].showVinyls(vinyls)

    }

    override fun showVinylPreference(commaSeparatedStyles: String, viewPosition: Int) {
        userCards[viewPosition].showStylesText(commaSeparatedStyles)
    }

    override fun showNoUserFavourites() {

    }

    @Layout(R.layout.card_similar_user)
    inner class UserCard(private val mContext: Context, private val user: User, private val mSwipeView: SwipePlaceHolderView,private val viewPosition : Int)
    {


        @com.mindorks.placeholderview.annotations.View(R.id.profile_image_view)
        private val profileImageView: ImageView? = null

        @com.mindorks.placeholderview.annotations.View(R.id.text_user_name)
        private val nameTxt: TextView? = null

        @com.mindorks.placeholderview.annotations.View(R.id.text_top_tracks)
        private val topTracksText: TextView? = null

        @com.mindorks.placeholderview.annotations.View(R.id.text_age_location)
        private val locationNameTxt: TextView? = null

        @com.mindorks.placeholderview.annotations.View(R.id.list_user_favourites)
        private val recyclerView: RecyclerView? = null

        @com.mindorks.placeholderview.annotations.View(R.id.text_styles)
        private val stylesText: TextView? = null

        fun showVinyls(favouriteVinyls :List<VinylRelease>){

            val tenFavourites = favouriteVinyls.take(10)

            val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            val favouriteAdapter = FavouriteAdapter(listOf<VinylRelease>())

            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.adapter = favouriteAdapter

            favouriteAdapter.addVinyls(tenFavourites)
            favouriteAdapter.notifyDataSetChanged()
        }

        fun showStylesText(commaSeparatedStyles : String){
            stylesText?.text = commaSeparatedStyles
        }

        @Resolve
        private fun onResolved() {

            Picasso.with(mContext)
                    .load(user.imageurl)
                    .placeholder(R.drawable.ic_male_user_profile_picture)
                    .fit()
                    .centerCrop()
                    .into(profileImageView)

            nameTxt?.text = StringUtils.getFirstName(user.name)

            val userAge = dobToAge(user.dob)

            locationNameTxt?.text = "$userAge, ${user.location}"


            topTracksText?.setOnClickListener {

                val intent = Intent(context, FavouriteActivity::class.java)
                intent.putExtra("selected user", user)
                context.startActivity(intent)
            }
            similarUsersPresenter.loadVinylPreference(user.id,viewPosition)
            similarUsersPresenter.loadUserFavourites(user.id,viewPosition)
        }

        private fun dobToAge(date : Date?) : String{
            val birthDate = LocalDate(date)
            val todaysDate = LocalDate()

            val period = Period(birthDate, todaysDate, PeriodType.yearMonthDay())

            return period.years.toString()
        }

        @SwipeOut
        private fun onSwipedOut() {
            Log.d("EVENT", "onSwipedOut")
            mSwipeView.addView(this)
        }

        @SwipeCancelState
        private fun onSwipeCancelState() {
            Log.d("EVENT", "onSwipeCancelState")
        }

        @SwipeIn
        private fun onSwipeIn() {
            Log.d("EVENT", "onSwipedIn")
            similarUsersPresenter.handleLike(user)
        }

        @SwipeInState
        private fun onSwipeInState() {
            Log.d("EVENT", "onSwipeInState")
        }

        @SwipeOutState
        private fun onSwipeOutState() {
            Log.d("EVENT", "onSwipeOutState")
        }
    }

    inner class FavouriteAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

        fun addVinyls(vinyls : List<VinylRelease>){
            this.vinyls = vinyls
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.top_ten_vinyl_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
            holder.bindItems(vinyls[position])

            holder.itemView.setOnClickListener {

                val context = holder.itemView.context

                val intent = Intent(context, VinylDetailActivity::class.java)
                intent.putExtra("selected vinyl", vinyls[position])
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return vinyls.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(vinyl: VinylRelease) {
                itemView.album_name.text = vinyl.title

                if(!vinyl.thumb.isNullOrEmpty()) {
                    Picasso.with(itemView.context).load(vinyl.thumb).into(itemView.cover_art)
                }
            }
        }
    }
}