package com.zachkirlew.applications.waxwanderer.vinyl_detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Tracklist
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Video
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource


class VinylDetailActivity : AppCompatActivity(), VinylDetailContract.View, View.OnClickListener {

    private val vinyl by lazy { intent.getSerializableExtra("selected vinyl") as VinylRelease }
    private lateinit var presenter: VinylDetailContract.Presenter

    private val favouriteButton by lazy { findViewById<FloatingActionButton>(R.id.fab_favourite) }

    private val releaseText by lazy { findViewById<TextView>(R.id.title_release) }

    private val artistsText by lazy { findViewById<TextView>(R.id.text_artists) }
    private val labelText by lazy { findViewById<TextView>(R.id.text_label) }
    private val releaseDateText by lazy { findViewById<TextView>(R.id.text_release_date) }
    private val genreText by lazy { findViewById<TextView>(R.id.text_genre) }
    private val stylesText by lazy { findViewById<TextView>(R.id.text_styles) }

    private var isRemovedFromFavourites : Boolean = false

    private val coordinatorLayout by lazy{findViewById<CoordinatorLayout>(R.id.main_content)}

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vinyl_detail)

        setPresenter(VinylDetailPresenter((VinylsRemoteSource.instance),
                this, RecommenderImp(this)))

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        val appbar = findViewById<AppBarLayout>(R.id.appbar)

        appbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = vinyl.title
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "//there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })

        releaseText.text = vinyl.title

        favouriteButton.setOnClickListener(this)

        presenter.loadVinylRelease(vinyl.id.toString())
        presenter.checkInFavourites(vinyl.id.toString())
    }

    override fun showTrackList(trackList: List<Tracklist>?) {
        val listView = findViewById<LinearLayout>(R.id.listview) as LinearLayout

        val adapter = TrackListAdapter(this,0, trackList)
        (0 until adapter.count)
                .map { adapter.getView(it, null, listView) }
                .forEach { listView.addView(it) }
    }

    override fun setPresenter(presenter: VinylDetailContract.Presenter) {
        this.presenter = presenter
    }

    override fun onBackPressed() {
        println(isRemovedFromFavourites)
        if(isRemovedFromFavourites){
            val data = Intent()
            data.putExtra("deletedVinyl",vinyl)
            setResult(Activity.RESULT_OK,data)
            finish()
        }
        else{
            super.onBackPressed()
        }
    }



    override fun addRemovedResult(isRemoved: Boolean) {
        this.isRemovedFromFavourites = isRemoved
    }

    override fun showVideos(videos: List<Video>?) {
        val listView = findViewById<LinearLayout>(R.id.list_youtube_videos) as LinearLayout

        val adapter = VideoAdapter(this,0, videos)
        (0 until adapter.count)
                .map { adapter.getView(it, null, listView) }
                .forEach { listView.addView(it) }
    }

    override fun showImageBackDrop(imageUrl: String) {
        val imageView = findViewById<ImageView>(R.id.backdrop) as ImageView

        Picasso.with(this).load(imageUrl).into(imageView)
    }

    override fun showMessage(message: String?) {
        message?.let { Snackbar.make(coordinatorLayout, it, Snackbar.LENGTH_LONG).show() }
    }

    override fun editButtonColor(vinylIsInFavourites: Boolean) {
        if(vinylIsInFavourites)
            favouriteButton.backgroundTintList = resources.getColorStateList(R.color.colorAccent)
        else
            favouriteButton.backgroundTintList = resources.getColorStateList(R.color.com_facebook_button_background_color_disabled)
    }

    override fun showDetailVinylInfo(detailVinylRelease: DetailVinylRelease) {

        val artistNames = detailVinylRelease.artists?.map { it.name }

        artistsText.text = commaSeparateList(artistNames)

        labelText.text = detailVinylRelease.labels?.get(0)?.name
        releaseDateText.text = detailVinylRelease.releasedFormatted
        genreText.text = detailVinylRelease.genres?.get(0)

        detailVinylRelease.styles?.let{
            stylesText.text = commaSeparateList(detailVinylRelease.styles)
        }
    }

    override fun showRating(starRating: Double) {
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.rating = starRating.toFloat()
    }

    override fun onClick(p0: View?) {
        presenter.addToFavourites(vinyl)
    }

    private fun commaSeparateList(list: List<String>?): String {
        return android.text.TextUtils.join(", ", list)
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

    override fun onStop() {
        super.onStop()
        presenter.dispose()
    }
}