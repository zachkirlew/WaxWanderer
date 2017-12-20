package com.zachkirlew.applications.waxwanderer.styles

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.ExploreActivity
import android.widget.ArrayAdapter
import android.widget.Spinner



class StylesActivity : AppCompatActivity(), StylesContract.View, AdapterView.OnItemSelectedListener {

    private lateinit var presenter: StylesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_styles)

        presenter = StylesPresenter(this)

    }

    public override fun onStart() {
        super.onStart()
        presenter.loadGenres()
    }

    override fun showGenres(genres : List<String>) {

        val spinner = findViewById<Spinner>(R.id.spinner_genres)

        spinner.onItemSelectedListener = this

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = dataAdapter
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }



    override fun startExploreActivity() {
        val intent = Intent(this, ExploreActivity::class.java)
        startActivity(intent)
    }


    companion object {

        private val TAG = StylesActivity::class.java.simpleName
    }
}