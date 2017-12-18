package com.zachkirlew.applications.waxwanderer.sign_up

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.ExploreActivity


class SignUpActivity : AppCompatActivity(), SignUpContract.View{


    private lateinit var presenter: SignUpPresenter

    private val buttonSignUp by lazy {findViewById<Button>(R.id.btn_sign_up)}
    private val editTextName by lazy {findViewById<EditText>(R.id.input_name)}
    private val editTextEmail by lazy {findViewById<EditText>(R.id.input_email)}
    private val editTextPassword by lazy {findViewById<EditText>(R.id.input_password)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        presenter = SignUpPresenter(this)

        buttonSignUp.setOnClickListener {getSignUpCreds()}
    }

    override fun startExploreActivity() {
        val intent = Intent(this, ExploreActivity::class.java)
        startActivity(intent)
    }

    private fun getSignUpCreds (){

        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        presenter.signUp(name,email,password)
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