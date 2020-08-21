package com.xeross.anniveraire.controller.social

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.BaseFragment
import com.xeross.anniveraire.utils.Constants.LOGIN_REQUEST_CODE
import kotlinx.android.synthetic.main.fragment_social.*


class SocialFragment : BaseFragment() {

    private val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

    private lateinit var socialViewModel: SocialViewModel
    override fun getFragmentId() = R.layout.fragment_social
    override fun setFragment() = this

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeOnClick()
    }

    private fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    private fun getUserLogin() = getCurrentUser() != null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // do stuff ..
            } else response?.let {
                this.showSnackBar(getString(if (it.error?.errorCode == ErrorCodes.NO_NETWORK) R.string.error_no_internet else R.string.error_unknown_error))
            }
        }
    }

    private fun showSnackBar(message: String?) {
        Snackbar.make(coordinator, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private fun initializeOnClick() {
        fragment_social_login_google.setOnClickListener {
            if (getUserLogin()) {
                val intent = Intent(context, SocialActivity::class.java)
                startActivity(intent)
                return@setOnClickListener
            }
            startLogInActivity(providers[0])
        }
    }

    private fun startLogInActivity(login: AuthUI.IdpConfig) {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                        listOf(login))
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_launcher_background)
                .build(), LOGIN_REQUEST_CODE);
    }
}
