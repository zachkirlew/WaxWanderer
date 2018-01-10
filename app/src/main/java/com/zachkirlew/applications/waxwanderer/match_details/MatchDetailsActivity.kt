package com.zachkirlew.applications.waxwanderer.match_details

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.styles.StylesActivity
import java.util.*

class MatchDetailsActivity : AppCompatActivity(), MatchDetailsContract.View,DatePickerDialog.OnDateSetListener {

    private lateinit var presenter: MatchDetailsPresenter

    private val editTextDOB by lazy{findViewById<EditText>(R.id.input_dob)}
    private val inputLayoutDOB by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_dob) }

    private val editTextCity by lazy{findViewById<EditText>(R.id.input_city)}
    private val inputLayoutCity by lazy{findViewById<TextInputLayout>(R.id.text_input_layout_city)}

    private val textMatchAge by lazy {findViewById<TextView>(R.id.input_match_age)}

    private val userGenderSpinner by lazy{findViewById<Spinner>(R.id.user_gender_spinner)}
    private val  matchGenderSpinner by lazy{findViewById<Spinner>(R.id.match_gender_spinner)}

    private val submitButton by lazy {findViewById<Button>(R.id.button_submit_details)}

    private val fromAgePicker by lazy{findViewById<NumberPicker>(R.id.age_from) as NumberPicker}

    private val toAgePicker by lazy{findViewById<NumberPicker>(R.id.age_to) as NumberPicker}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_details)

        presenter = MatchDetailsPresenter(this)

        val userGendersAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, resources.getStringArray(R.array.spinner_user_genders))

        userGendersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        userGenderSpinner.adapter = userGendersAdapter

        val matchGenderAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, resources.getStringArray(R.array.spinner_matching_genders))

        matchGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        matchGenderSpinner.adapter = matchGenderAdapter

        editTextDOB.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) showDateDialog() }

        submitButton.setOnClickListener{getDetails()}


        fromAgePicker.minValue = 18
        fromAgePicker.maxValue = 100
        
        fromAgePicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->

            toAgePicker.minValue = newVal


            val oldAgeRange = textMatchAge.text.toString()

            val split = oldAgeRange.split(" - ")



            if(newVal > Integer.parseInt(split[1])){
                textMatchAge.text = newVal.toString() + " - " + newVal
            }

            else{
                textMatchAge.text = newVal.toString() + " - " + split[1]
            }
        }

        toAgePicker.setOnValueChangedListener { numberPicker, oldVal, newVal ->

            val oldAgeRange = textMatchAge.text.toString()

            val split = oldAgeRange.split(" - ")

            val newAgeRange = split[0] + " - " + newVal.toString()

            textMatchAge.text = newAgeRange
        }
        

        toAgePicker.minValue = 18
        toAgePicker.maxValue = 100
    }

    override fun showDOBErrorMessage(message: String) {
        inputLayoutDOB.error = message
    }

    fun getDetails(){

        inputLayoutCity.isErrorEnabled = false
        inputLayoutDOB.isErrorEnabled = false

        val userGender = userGenderSpinner.selectedItem.toString()
        val userLocation = editTextCity.text.toString()

        val matchGender = matchGenderSpinner.selectedItem.toString()

        val matchAge = textMatchAge.text.toString()

        presenter.submitDetails(userGender,userLocation,matchGender,matchAge)
    }

    private fun showDateDialog() {

        val  c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog (this, this, mYear, mMonth, mDay).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        presenter.getFormattedDate(year, month, day)
    }


    override fun showDateFormatted(date: String) {
        editTextDOB.setText(date)
    }


    override fun startStylesActivity() {
        val intent = Intent(this, StylesActivity::class.java)
        startActivity(intent)
    }


    override fun  showCreateUserFailedMessage(message: String){
        Toast.makeText(this@MatchDetailsActivity, message,
                Toast.LENGTH_SHORT).show()
    }

    override fun showLocationErrorMessage(message: String) {
        inputLayoutCity.error = message
    }



    companion object {

        private val TAG = MatchDetailsActivity::class.java.simpleName
    }
}