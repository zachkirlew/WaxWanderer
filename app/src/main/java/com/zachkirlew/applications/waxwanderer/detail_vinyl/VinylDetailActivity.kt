package com.zachkirlew.applications.waxwanderer.detail_vinyl

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Tracklist
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Video
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.login.LoginActivity
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Rating


class VinylDetailActivity : AppCompatActivity(), VinylDetailContract.View {

    private val vinyl by lazy { intent.getSerializableExtra("selected vinyl") as VinylRelease }
    private lateinit var presenter: VinylDetailPresenter

    private val releaseText by lazy { findViewById<TextView>(R.id.title_release) }


    private val artistsText by lazy { findViewById<TextView>(R.id.text_artists) }
    private val labelText by lazy { findViewById<TextView>(R.id.text_label) }
    private val releaseDateText by lazy { findViewById<TextView>(R.id.text_release_date) }
    private val genreText by lazy { findViewById<TextView>(R.id.text_genre) }
    private val stylesText by lazy { findViewById<TextView>(R.id.text_styles) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vinyl_detail)

        presenter = VinylDetailPresenter(VinylRepository.getInstance(VinylsRemoteSource.instance), this)

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

        presenter.loadVinylRelease(vinyl.id.toString())
    }

    override fun showTrackList(trackList: List<Tracklist>?) {
        val listView = findViewById<LinearLayout>(R.id.listview) as LinearLayout

        val adapter = TrackListAdapter(this,0, trackList)
        (0 until adapter.count)
                .map { adapter.getView(it, null, listView) }
                .forEach { listView.addView(it) }
    }

    override fun showVideos(videos: List<Video>?) {
        val listView = findViewById<LinearLayout>(R.id.list_youtube_videos) as LinearLayout

        val adapter = VideosAdapter(this,0, videos)
        (0 until adapter.count)
                .map { adapter.getView(it, null, listView) }
                .forEach { listView.addView(it) }
    }

    override fun showImageBackDrop(imageUrl: String) {
        val imageView = findViewById<ImageView>(R.id.backdrop) as ImageView

        Picasso.with(this).load(imageUrl).into(imageView)
    }

    override fun showDetailVinylInfo(detailVinylRelease: DetailVinylRelease) {

        val artistNames = detailVinylRelease.artists?.map { it.name }

        artistsText.text = commaSeparateList(artistNames)

        labelText.text = detailVinylRelease.labels?.get(0)?.name
        releaseDateText.text = detailVinylRelease.releasedFormatted
        genreText.text = detailVinylRelease.genres?.get(0)

        stylesText.text = commaSeparateList(detailVinylRelease.styles)
    }

    override fun showRating(starRating: Double) {

        println(starRating)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.rating = starRating.toFloat()
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

    companion object {

        private val TAG = LoginActivity::class.java.simpleName
    }

    class TrackListAdapter
    (context: Context, resource: Int, tracklist: List<Tracklist>?) : ArrayAdapter<Tracklist>(context, resource, tracklist) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var convertView = convertView

            // Get the data item for this position
            val track = getItem(position)

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.tracklist_item, parent, false)
            }
            // Lookup view for data population
            val sideText = convertView?.findViewById<TextView>(R.id.text_track_side) as TextView
            val trackText = convertView.findViewById<TextView>(R.id.text_track_name) as TextView
            // Populate the data into the template view using the data object

            sideText.text = track?.position
            trackText.text = track?.title

            // Return the completed view to render on screen
            return convertView
        }
    }


    class VideosAdapter
    (context: Context, resource: Int, videos: List<Video>?) : ArrayAdapter<Video>(context, resource, videos) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var convertView = convertView

            // Get the data item for this position
            val video = getItem(position)

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
            }
            // Lookup view for data population
            val titleText = convertView?.findViewById<TextView>(R.id.text_title) as TextView
            val durationText = convertView.findViewById<TextView>(R.id.text_duration) as TextView
            // Populate the data into the template view using the data object


            titleText.text = video?.title
            durationText.text = formatDuration(video?.duration!!)

            convertView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(video.uri))
                convertView?.context?.startActivity(browserIntent)
            }

            // Return the completed view to render on screen
            return convertView
        }

        private fun formatDuration(duration : Int): String {

            val minutes = duration / 60
            val seconds = duration % 60

            val disMinu = (if (minutes < 10) "0" else "") + minutes
            val disSec = (if (seconds < 10) "0" else "") + seconds

            return disMinu + ":" + disSec
        }

    }


}