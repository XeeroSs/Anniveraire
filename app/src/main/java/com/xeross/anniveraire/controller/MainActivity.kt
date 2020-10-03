package com.xeross.anniveraire.controller

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.event.BirthdayViewModel
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
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration =
                AppBarConfiguration(setOf(R.id.navigation_galleries,
                        R.id.navigation_discussions, R.id.navigation_birthday))
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
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
