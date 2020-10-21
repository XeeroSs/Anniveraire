package com.xeross.anniveraire.controller.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.model.User
import com.xeross.anniveraire.utils.Constants.RC_SIGN_IN
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.coordinator.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Login"
        login_activity_button_login_google.setOnClickListener {
            startSignInActivity(AuthUI.IdpConfig.GoogleBuilder().build())
        }
    }

    // Start user sign in
    private fun startSignInActivity(login: AuthUI.IdpConfig) {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                        listOf(login)) // GOOGLE
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_launcher_background)
                .build(), RC_SIGN_IN)
    }

    // Get current user from FirebaseAuth
    private fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    private fun createUserInFirebase() {
        getCurrentUser()?.let {
            LoginHelper.getUser(it.uid).addOnSuccessListener { ds ->
                val user = ds.toObject(User::class.java)
                val email = it.email
                val username = user?.userName ?: it.displayName
                val urlPicture = if (user == null) if (it.photoUrl != null) it.photoUrl.toString() else null else user.urlImage
                val uid = it.uid
                val discussionId = user?.discussionsId ?: ArrayList()
                val galleriesId = user?.galleriesId ?: ArrayList()
                val discussionRequestId = user?.discussionsRequestId ?: ArrayList()
                val galleriesRequestId = user?.galleriesRequestId ?: ArrayList()
                LoginHelper.createUser(uid, email, username, urlPicture, discussionId = discussionId, discussionRequestId = discussionRequestId, galleriesId = galleriesId, galleriesRequestId = galleriesRequestId)
                finish()
            }
        }
    }

    // Get login response
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let { this.responseLogin(requestCode, resultCode, it) }
    }

    // Login response, create an account if the response is right.
    private fun responseLogin(requestCode: Int, resultCode: Int, data: Intent) {
        val response = IdpResponse.fromResultIntent(data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                this.createUserInFirebase()
            } else {
                if (response != null) {
                    this.showSnackBar(getString(
                            if (response.error?.errorCode == ErrorCodes.NO_NETWORK)
                                R.string.error_no_internet else R.string.error_unknown_error))
                }
            }
        } else {
            return
        }
    }

    private fun showSnackBar(message: String?) {
        Snackbar.make(coordinator, message!!, Snackbar.LENGTH_SHORT).show()
    }
}