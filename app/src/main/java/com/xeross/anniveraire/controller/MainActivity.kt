package com.xeross.anniveraire.controller

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.base.BaseFragment
import com.xeross.anniveraire.controller.discussion.DiscussionsFragment
import com.xeross.anniveraire.controller.discussion.request.DiscussionRequestActivity
import com.xeross.anniveraire.controller.event.BirthdayFragment
import com.xeross.anniveraire.controller.gallery.GalleriesFragment
import com.xeross.anniveraire.controller.gallery.request.GalleryRequestActivity
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
        supportActionBar?.title = getString(R.string.app_name)
        val transaction = supportFragmentManager.beginTransaction()
        if (transaction.isEmpty) {
            transaction.replace(R.id.nav_host_fragment, DiscussionsFragment().setFragment())
            transaction.commit()
        }
        chat_action.setOnClickListener { initializeBottomMenuNavigation(R.id.chat_action) }
        gallery_action.setOnClickListener { initializeBottomMenuNavigation(R.id.gallery_action) }
        birthday_action.setOnClickListener { initializeBottomMenuNavigation(R.id.birthday_action) }
    }

    // logout user
    private fun signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.logout())
    }

    // start activity login
    private fun logout() = OnSuccessListener<Void?> {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    // update with animation the bottom menu navigation
    private fun initializeBottomMenuNavigation(id: Int) {
        TransitionManager.beginDelayedTransition(bottom_bar)
        val constraintSet = ConstraintSet()
        transaction(DiscussionsFragment().setFragment(),
                chat_icon_text, chat_action, constraintSet, id, R.id.chat_action)
        transaction(GalleriesFragment().setFragment(),
                gallery_icon_text, gallery_action, constraintSet, id, R.id.gallery_action)
        transaction(BirthdayFragment().setFragment(),
                birthday_icon_text, birthday_action, constraintSet, id, R.id.birthday_action)
    }

    // Fragment transaction
    private fun transaction(fragment: Fragment, appCompatTextView: AppCompatTextView, constraintLayout: ConstraintLayout, constraintSet: ConstraintSet, id: Int, idAction: Int) {
        constraintSet.clone(constraintLayout)
        if (id == idAction) {
            DrawableCompat.setTint(constraintLayout.background,
                    ContextCompat.getColor(this, R.color.colorWhiteLightGrey))
            constraintSet.setVisibility(appCompatTextView.id, ConstraintSet.VISIBLE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.commit()
        } else {
            DrawableCompat.setTint(constraintLayout.background,
                    ContextCompat.getColor(this, android.R.color.transparent))
            constraintSet.setVisibility(appCompatTextView.id, ConstraintSet.GONE)
        }
        constraintSet.applyTo(constraintLayout)
    }

    // Click Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        baseFragment?.let {
            when (item.itemId) {
                R.id.toolbar_add -> it.onAdd()
                R.id.toolbar_search -> it.onSearch(item.actionView as SearchView)
                R.id.toolbar_logout -> signOutUserFromFirebase()
                R.id.toolbar_requests_discussion -> startActivity(Intent(this, DiscussionRequestActivity::class.java))
                R.id.toolbar_requests_gallery -> startActivity(Intent(this, GalleryRequestActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
