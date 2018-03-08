package com.waxwanderer.login

import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.waxwanderer.R
import com.waxwanderer.main.MainActivity
import com.waxwanderer.match_preferences.MatchPreferencesActivity
import com.waxwanderer.sign_up.SignUpActivity
import com.waxwanderer.vinyl_preferences.VinylPreferencesActivity


class LoginActivity : AppCompatActivity(), LoginContract.View, GoogleApiClient.OnConnectionFailedListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

    private val SIGN_IN_REQUEST_CODE = 888

    private lateinit var presenter: LoginPresenter

    private val progressSignIn by lazy { findViewById<ProgressBar>(R.id.sign_in_progress_bar) }

    private val loginLayout by lazy{findViewById<LinearLayout>(R.id.layout_login)}


    private val buttonEmailSignIn by lazy { findViewById<Button>(R.id.button_login_email) }
    private val buttonGoogleSignIn by lazy { findViewById<SignInButton>(R.id.button_login_google) }
    private val buttonFacebookSignIn by lazy { findViewById<LoginButton>(R.id.button_login_facebook) }

    private val textViewSignUp by lazy {findViewById<TextView>(R.id.textview_sign_up)}

    private val editTextEmail by lazy {findViewById<EditText>(R.id.input_sign_in_email)}
    private val editTextPassword by lazy {findViewById<EditText>(R.id.input_sign_in_password)}


    private val inputLayoutEmail by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_email) }
    private val inputLayoutPassword by lazy { findViewById<TextInputLayout>(R.id.text_input_layout_password) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken( getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        buttonGoogleSignIn.setOnClickListener { signIn() }

        textViewSignUp.setOnClickListener{startSignUpActivity()}

        buttonEmailSignIn.setOnClickListener{getLogInCreds()}

        buttonFacebookSignIn.setReadPermissions("email", "public_profile")
        buttonFacebookSignIn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult)
                presenter.handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                showLoginView()
                hideProgressBar()
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                showLoginView()
                hideProgressBar()
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }

    private fun getLogInCreds() {

        hideLoginView()
        showProgressBar()

        inputLayoutEmail.isErrorEnabled = false
        inputLayoutPassword.isErrorEnabled = false

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        presenter.logInWithEmail(email,password)
    }


    public override fun onStart() {
        super.onStart()
        presenter.setAuthListener()
    }

    public override fun onStop() {
        super.onStop()
        presenter.removeAuthListener()
    }


    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult.errorMessage!!)
    }

    private fun signIn() {
        hideLoginView()
        showProgressBar()
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        //Google login
        if (requestCode == SIGN_IN_REQUEST_CODE) {

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess) {
                presenter.logInWithFirebase(result.signInAccount!!)
            }
        }
        //Facebook login
        else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    override fun showEmailErrorMessage(message: String) {
        inputLayoutEmail.error = message
    }

    override fun showPasswordErrorMessage(message: String) {
        inputLayoutPassword.error = message
    }

    override fun startMatchDetailsActivity() {
        val intent = Intent(this, MatchPreferencesActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showProgressBar() {
        progressSignIn.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressSignIn.visibility = View.GONE
    }

    override fun showLoginView() {
        loginLayout.visibility = View.VISIBLE
    }

    override fun hideLoginView() {
        loginLayout.visibility = View.GONE
    }

    override fun startExploreActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun startStylesActivity() {
        val intent = Intent(this, VinylPreferencesActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun startSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    override fun showMessage(message : String) {
        Toast.makeText(this@LoginActivity, message,
                Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {

        private val TAG = LoginActivity::class.java.simpleName
    }
}