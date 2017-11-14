package com.example.myapplication.view.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.myapplication.R
import com.example.myapplication.view.login.presenter.LoginContract
import com.example.myapplication.view.login.presenter.LoginPresenter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginContract.View {

    private val RC_SIGN_IN: Int = 1000      // Internet Request Id
    private var mPresenter: LoginPresenter? = null


    override fun onStart() {
        super.onStart()
        mPresenter?.addFirebaseAuthStateListener()

    }

    override fun onStop() {
        super.onStop()
        mPresenter?.removeFirebaseAuthStateListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mPresenter = LoginPresenter(this, this)

        btn_email_sign_in.setOnClickListener(btnEmailSignInClickListener)
        btn_google_sign_in.setOnClickListener(btnGoogleSignInClickListener)
        btn_facebook_sign_in.setReadPermissions("email", "public_profile")
        btn_facebook_sign_in.registerCallback(mPresenter?.mFacebookCallbackManager, mPresenter?.mFacebookCallback)
    }

    override fun getUserMail() : String = et_email.text.toString()

    override fun getUserPassword() : String = et_password.text.toString()

    val btnGoogleSignInClickListener: View.OnClickListener = View.OnClickListener {
        val signInIntent: Intent = Auth.GoogleSignInApi.getSignInIntent(mPresenter?.mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    val btnEmailSignInClickListener: View.OnClickListener = View.OnClickListener {
        mPresenter?.registerEmailAndSignIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("LoginActivity", "requestCode = $requestCode, resultCode = $resultCode")
        mPresenter?.mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

                Log.d("LoginActivity", "result = ${result.isSuccess}")
                if (result.isSuccess) {
                    val account: GoogleSignInAccount? = result.signInAccount
                    mPresenter?.sendGoogleInfoToFirebaseAuth(account)

                }
            }
        }
    }
}
