package com.xeross.anniveraire.controller.base

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.login.LoginActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        if (getCurrentUser() == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }
        supportActionBar?.let {
            it.title = getToolBarTitle()
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    protected fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    protected fun sendMissingInformationMessage() {
        Toast.makeText(this, getString(R.string.missing_information), Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun getToolBarTitle(): String

    protected fun RecyclerView.setRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>, isCustom: Boolean = false) {
        setHasFixedSize(true)
        if (!isCustom) layoutManager = LinearLayoutManager(context)
        itemAnimator = DefaultItemAnimator()
        this.adapter = adapter
    }

    protected fun createBSD(view: View) = BottomSheetDialog(this).apply {
        setContentView(view)
        show()
    }
}