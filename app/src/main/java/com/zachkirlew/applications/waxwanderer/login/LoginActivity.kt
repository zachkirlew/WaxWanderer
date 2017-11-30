package com.zachkirlew.applications.waxwanderer.login

import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.explore.ExploreActivity


class LoginActivity : AppCompatActivity(), LoginContract.View, GoogleApiClient.OnConnectionFailedListener {
    override fun showLoginConfirmation() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mGoogleApiClient: GoogleApiClient? = null

    private val SIGN_IN_REQUEST_CODE = 888

    private var presenter: LoginPresenter? = null

    private val progressSignIn by lazy{findViewById<ProgressBar>(R.id.sign_in_progress_bar)}
    private val buttonSignIn by lazy{findViewById<SignInButton>(R.id.sign_in_button)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        buttonSignIn.setOnClickListener { signIn() }
    }

    public override fun onStart() {
        super.onStart()
        presenter?.setAuthListener()
    }

    public override fun onStop() {
        super.onStop()
        presenter?.removeAuthListener()
    }

    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult.errorMessage!!)
    }

    private fun signIn() {
        showProgressBar(true)
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
    }

    private fun showProgressBar(show: Boolean) {
        progressSignIn.visibility = if (show) View.VISIBLE else View.GONE

        buttonSignIn?.visibility = if (show) View.GONE else View.VISIBLE
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                presenter?.logInWithFirebase(result.signInAccount!!)
            }
        }
    }

    override fun startExploreActivity() {
        val intent = Intent(this, ExploreActivity::class.java)
        startActivity(intent)

        showProgressBar(false)
    }

    override fun showFirebaseAuthenticationFailedMessage() {
        Toast.makeText(this@LoginActivity, "Authentication failed.",
                Toast.LENGTH_SHORT).show()

        showProgressBar(false)
    }

    companion object {

        private val TAG = LoginActivity::class.java.simpleName
    }
}