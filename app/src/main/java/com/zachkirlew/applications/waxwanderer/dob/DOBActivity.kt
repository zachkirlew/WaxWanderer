package com.zachkirlew.applications.waxwanderer.dob

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import android.widget.Toast
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.styles.StylesActivity
import java.util.*

class DOBActivity : AppCompatActivity(), DOBContract.View, DatePicker.OnDateChangedListener {

    private lateinit var presenter: DOBPresenter

    private val datePicker by lazy{findViewById<DatePicker>(R.id.date_picker)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dob)

        presenter = DOBPresenter(this)

        initDatePicker()
    }

    private fun initDatePicker(){

        val  c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        datePicker.init(mYear,mMonth,mDay,this)
    }

    override fun startStylesActivity() {
        val intent = Intent(this, StylesActivity::class.java)
        startActivity(intent)
    }

    override fun showDOBErrorMessage(message: String) {
    }

    override fun onDateChanged(view: DatePicker?, year: Int, month: Int, day: Int) {
        presenter.submitDOB(year,month,day)
    }

    override fun  showCreateUserFailedMessage(message: String){
        Toast.makeText(this@DOBActivity, message,
                Toast.LENGTH_SHORT).show()
    }

    companion object {

        private val TAG = DOBActivity::class.java.simpleName
    }
}