package com.zachkirlew.applications.waxwanderer.sign_up

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.match_preferences.MatchPreferencesActivity


class SignUpActivity : AppCompatActivity(), SignUpContract.View {

    private lateinit var presenter: SignUpPresenter

    private val buttonSignUp by lazy {findViewById<Button>(R.id.btn_sign_up)}

    private val textLogInView by lazy {findViewById<TextView>(R.id.text_login_view)}

    private val editTextName by lazy {findViewById<EditText>(R.id.input_name)}
    private val editTextEmail by lazy {findViewById<EditText>(R.id.input_email)}
    private val editTextPassword by lazy {findViewById<EditText>(R.id.input_password)}

    private val inputLayoutName by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_name) }
    private val inputLayoutEmail by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_email) }
    private val inputLayoutPassword by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_password) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        presenter = SignUpPresenter(this)

        buttonSignUp.setOnClickListener {getSignUpCreds()}

        textLogInView.setOnClickListener{onBackPressed()}

    }

    override fun startMatchDetailsActivity() {
        val intent = Intent(this, MatchPreferencesActivity::class.java)
        startActivity(intent)
    }

    private fun getSignUpCreds (){

        inputLayoutName.isErrorEnabled = false
        inputLayoutEmail.isErrorEnabled = false
        inputLayoutPassword.isErrorEnabled = false

        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        presenter.signUp(name,email,password)
    }

    override fun showNameErrorMessage(message: String) {
        inputLayoutName.error = message
    }

    override fun showEmailErrorMessage(message: String) {
        inputLayoutEmail.error = message
    }

    override fun showPasswordErrorMessage(message: String) {
        inputLayoutPassword.error = message
    }



    override fun  showCreateUserFailedMessage(message: String){
        Toast.makeText(this@SignUpActivity, message,
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