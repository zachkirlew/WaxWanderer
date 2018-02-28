package com.zachkirlew.applications.waxwanderer.user_detail

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.lucasurbas.listitemview.ListItemView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.vinyl_detail.VinylDetailActivity
import com.zachkirlew.applications.waxwanderer.favourites.FavouriteActivity
import kotlinx.android.synthetic.main.vinyl_favourite_item.view.*
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*


class UserDetailActivity : AppCompatActivity(), UserDetailContract.View  {


    private val coordinatorLayout by lazy{findViewById<CoordinatorLayout>(R.id.coordinator_layout)}

    private val user by lazy { intent.getSerializableExtra("selected user") as User }

    private lateinit var presenter: UserDetailPresenter

    private lateinit var favouriteAdapter: UserDetailActivity.FavouriteAdapter

    private val noFavouritesText by lazy {findViewById<TextView>(R.id.text_no_user_favourites) }

    private val preferredStylesText by lazy {findViewById<TextView>(R.id.text_styles) }

    private val viewFavouritesButton by lazy {findViewById<Button>(R.id.button_view_favourites)}

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        favouriteAdapter = FavouriteAdapter(listOf<VinylRelease>())

        presenter = UserDetailPresenter(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val customTitle = findViewById<TextView>(R.id.text_title)
        val customSubtitle = findViewById<TextView>(R.id.text_subtitle)

        val titleLayout = findViewById<View>(R.id.layout_title)
        titleLayout.post({
            val layoutParams = toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams
            layoutParams.height = titleLayout.height
            toolbar.layoutParams = layoutParams
        })

        title = null

        customTitle.text = user.name

        val userAge = dobToAge(user.dob)
        customSubtitle.text = "$userAge, ${user.location}"

        val favouritesList = findViewById<RecyclerView>(R.id.list_user_favourites) as RecyclerView

        val mLayoutManager = LinearLayoutManager(this)

        favouritesList.layoutManager = mLayoutManager
        favouritesList.adapter = favouriteAdapter

        user.imageurl?.let { showImageBackDrop(user.imageurl!!) }

        viewFavouritesButton.setOnClickListener(onViewTracksClickListener)

        presenter.loadUserStyles(user.id!!)
        presenter.loadUserFavourites(user.id!!)
    }

    private fun showImageBackDrop(imageUrl: String) {

        val imageView = findViewById<ImageView>(R.id.backdrop) as ImageView

        Picasso.with(this).load(imageUrl).into(imageView)
    }

    private val onViewTracksClickListener = View.OnClickListener {
        val intent = Intent(this, FavouriteActivity::class.java)
        intent.putExtra("selected user", user)
        startActivity(intent)
    }

    override fun showNoFavouritesView() {
        noFavouritesText.visibility = View.VISIBLE
    }

    override fun showUserStyles(stylesText: String) {
        preferredStylesText.text = stylesText
    }

    override fun showUserFavourites(favourites: List<VinylRelease>) {
        favouriteAdapter.addVinyls(favourites)
    }

    private fun dobToAge(date : Date?) : String{
        val birthDate = LocalDate(date)
        val todaysDate = LocalDate()

        val period = Period(birthDate, todaysDate, PeriodType.yearMonthDay())

        return period.years.toString()
    }

    override fun showMessage(message: String) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.dispose()
    }

    class FavouriteAdapter(private var vinyls: List<VinylRelease>) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

        fun addVinyls(vinyls : List<VinylRelease>){
            this.vinyls = vinyls
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.vinyl_favourite_item, parent, false) as ListItemView
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

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(vinyl: VinylRelease) {
                itemView.vinyl_favourite_item_view.title = vinyl.title
                itemView.vinyl_favourite_item_view.subtitle=vinyl.year

                val thumbUrl = vinyl.thumb

                thumbUrl?.let {
                    if(thumbUrl.isNotEmpty())
                        Picasso.with(itemView.context)
                                .load(vinyl.thumb)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(itemView.vinyl_favourite_item_view.avatarView)
                    else{
                        itemView.vinyl_favourite_item_view.avatarView.setImageResource(R.mipmap.ic_launcher)
                    }
                }
            }
        }
    }

}