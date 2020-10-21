@file:Suppress("DEPRECATION")

package com.xeross.anniveraire.controller.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.MainActivity
import com.xeross.anniveraire.injection.ViewModelFactory
import com.xeross.anniveraire.listener.ToolBarListener

abstract class BaseFragment : Fragment(), ToolBarListener {

    lateinit var main: MainActivity

    abstract fun getFragmentId(): Int
    abstract fun setFragment(): BaseFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(getFragmentId(), container, false)
        (activity as MainActivity).let {
            main = it
            it.setBaseFragment(setFragment())
        }
        return root
    }

    protected fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    protected fun sendMissingInformationMessage() {
        Toast.makeText(activity, getString(R.string.missing_information), Toast.LENGTH_SHORT).show()
    }

    // ViewModel for Fragment
    @Suppress("DEPRECATION")
    protected inline fun <reified VM : ViewModel> configureViewModel(): VM? {
        return ViewModelProviders.of(this, context?.let { ViewModelFactory(it) }).get(VM::class.java)
    }


    protected fun RecyclerView.setRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>, isCustom: Boolean = false) {
        setHasFixedSize(true)
        if (!isCustom) layoutManager = LinearLayoutManager(context)
        itemAnimator = DefaultItemAnimator()
        this.adapter = adapter
    }

    protected fun createBSD(view: View) = context?.let {
        BottomSheetDialog(it).apply {
            setContentView(view)
            show()
        }
    }
}