package com.xeross.anniveraire.controller

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.discussion.DiscussionsFragment
import com.xeross.anniveraire.controller.event.BirthdayFragment
import com.xeross.anniveraire.controller.gallery.GalleriesFragment
import com.xeross.anniveraire.controller.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var baseFragment: BaseFragment? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    fun setBaseFragment(baseFragment: BaseFragment) {
        this.baseFragment = baseFragment
    }

    private fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (getCurrentUser() == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }
        home_action.setOnClickListener { select(R.id.home_action) }
        likes_action.setOnClickListener { select(R.id.likes_action) }
        profile_action.setOnClickListener { select(R.id.profile_action) }
        setSupportActionBar(toolbar)

       /* val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration =
                AppBarConfiguration(setOf(R.id.navigation_galleries,
                        R.id.navigation_discussions, R.id.navigation_birthday))
        setupActionBarWithNavController(navController, appBarConfiguration)*/
    }

    private fun select(id: Int) {
        TransitionManager.beginDelayedTransition(bottom_bar)
        val cs = ConstraintSet()
        cs.clone(home_action)
        if (id == R.id.home_action) {
            DrawableCompat.setTint(
                    home_action.background,
                    ContextCompat.getColor(this, R.color.colorWhiteLightGrey)
            )
            cs.setVisibility(home_icon_text.id, ConstraintSet.VISIBLE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, DiscussionsFragment().setFragment())
            transaction.commit()
        } else {
            DrawableCompat.setTint(
                    home_action.background,
                    ContextCompat.getColor(this, android.R.color.transparent)
            )
            cs.setVisibility(home_icon_text.id, ConstraintSet.GONE)
        }
        cs.applyTo(home_action)

        cs.clone(likes_action)
        if (id == R.id.likes_action) {
            DrawableCompat.setTint(
                    likes_action.background,
                    ContextCompat.getColor(this, R.color.colorWhiteLightGrey)
            )
            cs.setVisibility(likes_icon_text.id, ConstraintSet.VISIBLE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, GalleriesFragment().setFragment())
            transaction.commit()
        } else {
            DrawableCompat.setTint(
                    likes_action.background,
                    ContextCompat.getColor(this, android.R.color.transparent)
            )
            cs.setVisibility(likes_icon_text.id, ConstraintSet.GONE)
        }
        cs.applyTo(likes_action)

        cs.clone(profile_action)
        if (id == R.id.profile_action) {
            DrawableCompat.setTint(
                    profile_action.background,
                    ContextCompat.getColor(this, R.color.colorWhiteLightGrey)
            )
            cs.setVisibility(profile_icon_text.id, ConstraintSet.VISIBLE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, BirthdayFragment().setFragment())
            transaction.commit()
        } else {
            DrawableCompat.setTint(
                    profile_action.background,
                    ContextCompat.getColor(this, android.R.color.transparent)
            )
            cs.setVisibility(profile_icon_text.id, ConstraintSet.GONE)
        }
        cs.applyTo(profile_action)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        baseFragment?.let {
            when (item.itemId) {
                R.id.toolbar_add -> it.onAdd()
                R.id.toolbar_search -> it.onSearch(item.actionView as SearchView)
                R.id.toolbar_requests -> it.onRequest()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendErrorMessage() {
        Toast.makeText(this, getString(R.string.an_error_has_occurred), Toast.LENGTH_SHORT
        ).show()
    }

    fun sendMissingInformationMessage() {
        Toast.makeText(this, getString(R.string.missing_information), Toast.LENGTH_SHORT
        ).show()
    }
}
