package com.zachkirlew.applications.waxwanderer.detail_vinyl

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.remote.VinylsRemoteSource
import com.zachkirlew.applications.waxwanderer.login.LoginActivity


class VinylDetailActivity : AppCompatActivity(), VinylDetailContract.View {

    private val vinyl by lazy{intent.getSerializableExtra("selected vinyl") as VinylRelease}
    private lateinit var presenter: VinylDetailPresenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vinyl_detail)

        presenter = VinylDetailPresenter(VinylRepository.getInstance(VinylsRemoteSource.instance),this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbar.title = vinyl.title

        presenter.loadVinylRelease(vinyl.id.toString())
    }

    override fun showImageBackDrop(imageUrl: String) {
        val imageView = findViewById<ImageView>(R.id.backdrop) as ImageView

        Picasso.with(this).load(imageUrl).into(imageView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    companion object {

        private val TAG = LoginActivity::class.java.simpleName
    }
}