package com.zachkirlew.applications.waxwanderer.vinyl_detail

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.*
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Tracklist
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Video
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.login.LoginActivity
import com.zachkirlew.applications.waxwanderer.main.MainActivity


class VinylDetailActivity : AppCompatActivity(), VinylDetailContract.View, View.OnClickListener {

    private lateinit var vinylRelease: VinylRelease

    private lateinit var presenter: VinylDetailContract.Presenter

    private val favouriteButton by lazy { findViewById<FloatingActionButton>(R.id.fab_favourite) }

    private val releaseText by lazy { findViewById<TextView>(R.id.title_release) }

    private val artistsText by lazy { findViewById<TextView>(R.id.text_artists) }
    private val labelText by lazy { findViewById<TextView>(R.id.text_label) }
    private val releaseDateText by lazy { findViewById<TextView>(R.id.text_release_date) }
    private val genreText by lazy { findViewById<TextView>(R.id.text_genre) }
    private val stylesText by lazy { findViewById<TextView>(R.id.text_styles) }

    private val cardYouTube by lazy { findViewById<CardView>(R.id.card_youtube) }

    private val coordinatorLayout by lazy { findViewById<CoordinatorLayout>(R.id.coordinator_layout) }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    private val collapsingToolbarLayout by lazy { findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar) }

    private val appbar by lazy { findViewById<AppBarLayout>(R.id.appbar) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vinyl_detail)

        setPresenter(VinylDetailPresenter((VinylsRemoteSource.instance),
                this, RecommenderImp(this)))

        setSupportActionBar(toolbar)

        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //if user has come from deep link
        if (intent.data != null) {
            val idParameter = intent.data.getQueryParameter("id")
            presenter.loadVinylRelease(idParameter)

            if(FirebaseAuth.getInstance().currentUser==null){
                showLoginDialog()
            }
            else{
                presenter.checkInFavourites(idParameter)
            }

        } else {
            val vinylRelease = intent.getSerializableExtra("selected vinyl") as VinylRelease
            presenter.loadVinylRelease(vinylRelease.id.toString())
            presenter.checkInFavourites(vinylRelease.id.toString())
        }

        favouriteButton.setOnClickListener(this)
    }

    private fun showLoginDialog() {

        val aDialog = AlertDialog.Builder(this)
                .setTitle("Hey!")
                .setMessage("You gotta log in to see this content!")
                .setPositiveButton("OK", { _, i ->
                    startLoginActivity()
                    finish()
                })
                .setNegativeButton("No thanks", { _, i -> finish() })
                .create()
        aDialog.show()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun setUpAppBar(title: String) {
        appbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1
            internal var vinylTitle = title

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = vinylTitle
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "//there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })
    }

    private fun mapDetailToRelease(detailVinylRelease: DetailVinylRelease) {
        vinylRelease = VinylRelease()

        val mainArtist = detailVinylRelease.artists?.get(0)?.name
        val catNo = detailVinylRelease.labels?.get(0)?.catno
        val labels = detailVinylRelease.labels?.map { it.name!! }

        vinylRelease.thumb = detailVinylRelease.thumb
        vinylRelease.title = "$mainArtist - ${detailVinylRelease.title}"
        vinylRelease.id = detailVinylRelease.id
        vinylRelease.style = detailVinylRelease.styles
        vinylRelease.genre = detailVinylRelease.genres
        vinylRelease.year = detailVinylRelease.year.toString()
        vinylRelease.catno = catNo
        vinylRelease.label = labels
    }

    override fun showInfo(detailVinylRelease: DetailVinylRelease) {

        mapDetailToRelease(detailVinylRelease)

        releaseText.text = detailVinylRelease.title

        setUpAppBar(detailVinylRelease.title!!)

        detailVinylRelease.images?.let { showImageBackDrop(detailVinylRelease.images?.get(0)?.uri!!) }

        showDetailVinylInfo(detailVinylRelease)

        showTrackList(detailVinylRelease.tracklist)

        detailVinylRelease.videos?.let { if (detailVinylRelease.videos?.isNotEmpty()!!) showVideos(detailVinylRelease.videos!!) }

        showRating(detailVinylRelease.community?.rating?.average!!)
    }

    private fun showTrackList(trackList: List<Tracklist>?) {
        val listView = findViewById<LinearLayout>(R.id.listview)

        val adapter = TrackListAdapter(this, 0, trackList)
        (0 until adapter.count)
                .map { adapter.getView(it, null, listView) }
                .forEach { listView.addView(it) }
    }

    override fun setPresenter(presenter: VinylDetailContract.Presenter) {
        this.presenter = presenter
    }

    override fun showRating(starRating: Double) {
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.rating = starRating.toFloat()
    }

    override fun onClick(p0: View?) {
        presenter.addToFavourites(vinylRelease)
    }

    override fun showMessage(message: String?) {
        message?.let { Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).show() }
    }

    override fun editButtonColor(vinylIsInFavourites: Boolean) {
        if (vinylIsInFavourites)
            favouriteButton.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        else
            favouriteButton.backgroundTintList = resources.getColorStateList(R.color.com_facebook_button_background_color_disabled)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
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

    override fun onBackPressed() {
        if(intent.data!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.dispose()
    }

    private fun showImageBackDrop(imageUrl: String) {
        val imageView = findViewById<ImageView>(R.id.backdrop)

        Picasso.with(this).load(imageUrl).into(imageView)
    }


    private fun showDetailVinylInfo(detailVinylRelease: DetailVinylRelease) {

        val artistNames = detailVinylRelease.artists?.map { it.name }

        artistsText.text = commaSeparateList(artistNames)

        labelText.text = detailVinylRelease.labels?.get(0)?.name
        releaseDateText.text = detailVinylRelease.releasedFormatted
        genreText.text = detailVinylRelease.genres?.get(0)

        detailVinylRelease.styles?.let {
            stylesText.text = commaSeparateList(detailVinylRelease.styles)
        }
    }

    private fun showVideos(videos: List<Video>) {
        cardYouTube.visibility = View.VISIBLE
        val listView = findViewById<LinearLayout>(R.id.list_youtube_videos)

        val adapter = VideoAdapter(this, 0, videos)
        (0 until adapter.count)
                .map { adapter.getView(it, null, listView) }
                .forEach { listView.addView(it) }
    }

    private fun commaSeparateList(list: List<String>?): String {
        return android.text.TextUtils.join(", ", list)
    }
}