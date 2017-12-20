package com.zachkirlew.applications.waxwanderer.sign_up

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.ExploreActivity
import com.zachkirlew.applications.waxwanderer.styles.StylesActivity
import java.text.DateFormat
import java.util.*


class SignUpActivity : AppCompatActivity(), SignUpContract.View, DatePickerDialog.OnDateSetListener {

    private lateinit var presenter: SignUpPresenter

    private var chosenDate: Date? = null

    private val buttonSignUp by lazy {findViewById<Button>(R.id.btn_sign_up)}
    private val editTextName by lazy {findViewById<EditText>(R.id.input_name)}
    private val editTextEmail by lazy {findViewById<EditText>(R.id.input_email)}
    private val editTextPassword by lazy {findViewById<EditText>(R.id.input_password)}

    private val editTextDOB by lazy{findViewById<EditText>(R.id.input_dob)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        presenter = SignUpPresenter(this)

        buttonSignUp.setOnClickListener {getSignUpCreds()}

        editTextDOB.setOnFocusChangeListener { view, hasFocus -> if (hasFocus) showDateDialog() }
    }



    override fun startStylesActivity() {
        val intent = Intent(this, StylesActivity::class.java)
        startActivity(intent)
    }

    private fun getSignUpCreds (){

        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        val dob = chosenDate!!

        presenter.signUp(name,email,dob,password)
    }

    private fun showDateDialog() {

        val  c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog (this, this, mYear, mMonth, mDay).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {

        // Create a Date variable/object with user chosen date
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal.set(year, month, day, 0, 0, 0)

        chosenDate = cal.time

        val dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        val dateFormatted = dfMediumUK.format(chosenDate)

        editTextDOB.setText(dateFormatted)
    }

    override fun  showCreateUserFailedMessage(){
        Toast.makeText(this@SignUpActivity, "Failed to sign up",
                Toast.LENGTH_SHORT).show()
    }

    public override fun onStart() {
        super.onStart()
        presenter.setAuthListener()
    }

    public override fun onStop() {
        super.onStop()
        presenter.removeAuthListener()
    }


    companion object {

        private val TAG = SignUpActivity::class.java.simpleName
    }
}