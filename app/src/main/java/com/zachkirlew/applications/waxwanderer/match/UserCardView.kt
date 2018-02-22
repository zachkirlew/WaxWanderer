package com.zachkirlew.applications.waxwanderer.match

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.swipe.SwipeIn
import com.mindorks.placeholderview.annotations.swipe.SwipeOut
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.UserCard
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.favourites.FavouriteActivity
import com.zachkirlew.applications.waxwanderer.util.StringUtils
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*

@Layout(R.layout.card_user)
class UserCardView(private val mContext: Context, private val userCard: UserCard, private val mSwipeView: SwipePlaceHolderView,private val callback : OnSwipeLeftListener)
{
    @com.mindorks.placeholderview.annotations.View(R.id.profile_image_view)
    private val profileImageView: ImageView? = null

    @com.mindorks.placeholderview.annotations.View(R.id.text_user_name)
    private val nameTxt: TextView? = null

    @com.mindorks.placeholderview.annotations.View(R.id.text_no_user_favourites)
    private val noFavouritesText: TextView? = null

    @com.mindorks.placeholderview.annotations.View(R.id.button_view_favourites)
    private val viewFavourites: Button? = null

    @com.mindorks.placeholderview.annotations.View(R.id.text_age_location)
    private val locationNameTxt: TextView? = null

    @com.mindorks.placeholderview.annotations.View(R.id.list_user_favourites)
    private val recyclerView: RecyclerView? = null

    @com.mindorks.placeholderview.annotations.View(R.id.text_styles)
    private val stylesText: TextView? = null

    fun showVinyls(favouriteVinyls :List<VinylRelease>){

        val mLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        val favouriteAdapter = UserCardFavouriteAdapter(favouriteVinyls)

        recyclerView?.layoutManager = mLayoutManager
        recyclerView?.adapter = favouriteAdapter

        favouriteAdapter.notifyDataSetChanged()
    }

    private fun showStyles(vinylPreference: List<String>) {
        val commaSeparatedStyles = android.text.TextUtils.join(", ", vinylPreference)
        stylesText?.text = commaSeparatedStyles
    }

    @Resolve
    private fun onResolved() {

        val user = userCard.user

        Picasso.with(mContext)
                .load(userCard.user.imageurl)
                .placeholder(R.drawable.ic_male_user_profile_picture)
                .fit()
                .centerCrop()
                .into(profileImageView)

        nameTxt?.text = StringUtils.getFirstName(user.name)

        val userAge = dobToAge(user.dob)

        locationNameTxt?.text = "$userAge, ${user.location}"

        showStyles(userCard.vinylPreference)

        //if user has favourites
        if(userCard.favourites!=null){
            showVinyls(userCard.favourites)
        }
        else{
            noFavouritesText?.visibility = View.VISIBLE
        }

        viewFavourites?.setOnClickListener {

            val intent = Intent(mContext, FavouriteActivity::class.java)
            intent.putExtra("selected user", user)
            mContext.startActivity(intent)
        }
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

    @SwipeIn
    private fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
        callback.onSwipedLeft(userCard.user)
    }
}