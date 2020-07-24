package com.xeross.anniveraire.controller

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xeross.anniveraire.controller.date.DateFragment
import com.xeross.anniveraire.controller.event.BirthdayFragment
import com.xeross.anniveraire.controller.social.SocialFragment
import java.util.*

abstract class BaseFragment : Fragment() {

    protected lateinit var main: MainActivity

    abstract fun getFragmentId(): Int

    abstract fun setFragment(): BaseFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(getFragmentId(), container, false)
        (activity as MainActivity).let {
            main = it
            it.setBaseFragment(setFragment())
        }
        /*  eventViewModel =
                  ViewModelProviders.of(this).get(EventViewModel::class.java)
          val textView: TextView = root.findViewById(R.id.text_home)
          eventViewModel.text.observe(viewLifecycleOwner, Observer {
              textView.text = it
          })*/
        return root
    }

    private fun getSocialFragment() = getFragment<SocialFragment>()
    private fun getDateFragment() = getFragment<DateFragment>()
    protected fun getEventFragment() = getFragment<BirthdayFragment>()

    private inline fun <reified T : BaseFragment> getFragment() = (setFragment() as? T) ?: run {
        main.sendErrorMessage()
        null
    }
}