package com.xeross.anniveraire.controller

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
import com.xeross.anniveraire.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_date,
                        R.id.navigation_event,
                        R.id.navigation_social
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        baseFragment?.let {
            when (item.itemId) {
                R.id.toolbar_add -> {
                    it.createPopupForChoiceEvent(this)
                }
                R.id.toolbar_search -> {
                    val searchView = item.actionView as SearchView
                    it.searchEvent(searchView)
                }
                R.id.toolbar_sort -> {
                    it.sortEvents(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendErrorMessage() {
        Toast.makeText(this, getString(R.string.an_error_has_occurred),
                Toast.LENGTH_SHORT).show()
    }

    fun sendMissingInformationMessage() {
        Toast.makeText(this, getString(R.string.missing_information),
                Toast.LENGTH_SHORT).show()
    }
}
