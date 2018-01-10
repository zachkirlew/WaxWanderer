package com.zachkirlew.applications.waxwanderer.similar_users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.daprlabs.aaron.swipedeck.SwipeDeck
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.detail_vinyl.VinylDetailActivity
import kotlinx.android.synthetic.main.explore_item.view.*
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*


class SimilarUsersFragment : Fragment(), SimilarUsersContract.View {

    private lateinit var similarUsersPresenter : SimilarUsersContract.Presenter

    private lateinit var swipeAdapter: SimilarUsersFragment.SwipeDeckAdapter

    private lateinit var cardStack : SwipeDeck

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swipeAdapter = SwipeDeckAdapter(listOf<User>(),activity)
    }

    override fun onResume() {
        super.onResume()
        similarUsersPresenter.start()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity.title = "Match"

        val root = inflater?.inflate(R.layout.fragment_similar_users, container, false)

        cardStack = root?.findViewById<SwipeDeck>(R.id.swipe_deck) as SwipeDeck

        cardStack.setAdapter(swipeAdapter)

        similarUsersPresenter = SimilarUsersPresenter(this)

        cardStack.setCallback(object : SwipeDeck.SwipeDeckCallback {
            override fun cardSwipedLeft(stableId: Long) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + stableId)
            }

            override fun cardSwipedRight(stableId: Long) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId)
            }

        })


        return root
    }


    override fun setPresenter(presenter: SimilarUsersContract.Presenter) {
        similarUsersPresenter = presenter
    }

    override fun showSimilarUsers(users: List<User>) {
        swipeAdapter.addUsers(users)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_clear -> mPresenter.clearCompletedTasks()
//            R.id.menu_filter -> showFilteringPopUpMenu()
//            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }


    inner class SwipeDeckAdapter(private var similarUsers: List<User>, private val context: Context) : BaseAdapter() {

        fun addUsers(users : List<User>){
            this.similarUsers = users
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return similarUsers.size
        }

        override fun getItem(position: Int): Any {
            return similarUsers[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var v: View? = convertView

            if (v == null) {
                val inflater = layoutInflater
                // normally use a viewholder

                v = inflater.inflate(R.layout.card_similar_user, parent, false)
            }

            val imageView = v?.findViewById<ImageView>(R.id.offer_image) as ImageView

            val user = getItem(position) as User

            val userNameText = v.findViewById<TextView>(R.id.text_user_name) as TextView
            userNameText.text = user.name

            val userAge = dobToAge(user.dob)

            val userAgeLocationText = v.findViewById<TextView>(R.id.text_age_location) as TextView
            userAgeLocationText.text = "$userAge, ${user.location}"


            val tenFavourites = user.favourites?.map { it.value }?.take(10)

            val recyclerView = v.findViewById<RecyclerView>(R.id.list_user_favourites) as RecyclerView

            val mLayoutManager = LinearLayoutManager(activity)

            val favouriteAdapter = FavouriteAdapter(listOf<VinylRelease>())

            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = favouriteAdapter

            if(tenFavourites!=null){
                favouriteAdapter.addVinyls(tenFavourites)
            }



            Picasso.with(context)
                        .load(user.imageurl)
                        .placeholder(R.drawable.ic_male_user_profile_picture)
                        .fit()
                        .centerCrop()
                        .into(imageView)

            v.setOnClickListener(View.OnClickListener {


            })
            return v
        }

        private fun dobToAge(date : Date?) : String{
            val birthDate = LocalDate(date)
            val todaysDate = LocalDate()

            val period = Period(birthDate, todaysDate, PeriodType.yearMonthDay())

            return period.years.toString()
        }

        inner class FavouriteAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {


            fun addVinyls(vinyls : List<VinylRelease>){
                this.vinyls = vinyls
                notifyDataSetChanged()

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.explore_item, parent, false)
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
                    itemView.artist_name.text=vinyl.year
                    itemView.code.text = vinyl.catno

                    if(!vinyl.thumb.isNullOrEmpty()) {
                        Picasso.with(itemView.context).load(vinyl.thumb).into(itemView.cover_art)
                    }
                }
            }
        }
    }


}