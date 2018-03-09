package com.waxwanderer.match_preferences

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.waxwanderer.R
import com.waxwanderer.data.local.UserPreferences
import com.waxwanderer.vinyl_preferences.VinylPreferencesActivity
import java.util.*

class MatchPreferencesActivity : AppCompatActivity(), MatchPreferencesContract.View,DatePickerDialog.OnDateSetListener {

    private lateinit var presenter: MatchPreferencesPresenter

    private val editTextDOB by lazy{findViewById<EditText>(R.id.input_dob)}
    private val inputLayoutDOB by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_dob) }

    private val editTextCity by lazy{findViewById<EditText>(R.id.input_city)}
    private val inputLayoutCity by lazy{findViewById<TextInputLayout>(R.id.text_input_layout_city)}

    private val textMatchAge by lazy {findViewById<TextView>(R.id.input_match_age)}

    private val userGenderSpinner by lazy{findViewById<Spinner>(R.id.user_gender_spinner)}
    private val  matchGenderSpinner by lazy{findViewById<Spinner>(R.id.match_gender_spinner)}

    private val submitButton by lazy {findViewById<Button>(R.id.button_submit_details)}

    private val fromAgePicker by lazy{ findViewById<NumberPicker>(R.id.age_from) }
    private val toAgePicker by lazy{ findViewById<NumberPicker>(R.id.age_to) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_preferences)

        presenter = MatchPreferencesPresenter(this, UserPreferences())

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
        
        fromAgePicker.setOnValueChangedListener { _, _, newVal ->

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

        toAgePicker.setOnValueChangedListener { _, _, newVal ->

            val oldAgeRange = textMatchAge.text.toString()

            val split = oldAgeRange.split(" - ")

            val newAgeRange = split[0] + " - " + newVal.toString()

            textMatchAge.text = newAgeRange
        }

        toAgePicker.minValue = 18
        toAgePicker.maxValue = 100
        toAgePicker.value = 28
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

        val minMatchAge = fromAgePicker.value
        val maxMatchAge = toAgePicker.value

        presenter.submitDetails(userGender,userLocation,matchGender,minMatchAge,maxMatchAge)
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
        val intent = Intent(this, VinylPreferencesActivity::class.java)
        startActivity(intent)
    }


    override fun  showCreateUserFailedMessage(message: String){
        Toast.makeText(this@MatchPreferencesActivity, message,
                Toast.LENGTH_SHORT).show()
    }

    override fun showLocationErrorMessage(message: String) {
        inputLayoutCity.error = message
    }



    companion object {

        private val TAG = MatchPreferencesActivity::class.java.simpleName
    }
}