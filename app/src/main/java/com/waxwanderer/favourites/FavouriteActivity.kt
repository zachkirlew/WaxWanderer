package com.waxwanderer.favourites

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.waxwanderer.R
import com.waxwanderer.data.model.User
import com.waxwanderer.util.StringUtils


class FavouriteActivity : AppCompatActivity() {

    private val coordinatorLayout by lazy{findViewById<CoordinatorLayout>(R.id.coordinator_layout)}

    private val user by lazy { intent.getSerializableExtra("selected user") as User }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val firstName = user.name?.let { StringUtils.getFirstName(it) }

        title = "$firstName's Favourites"
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
}