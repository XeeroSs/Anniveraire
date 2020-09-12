package com.xeross.anniveraire.injection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xeross.anniveraire.controller.discussion.DiscussionViewModel
import com.xeross.anniveraire.controller.event.BirthdayViewModel
import com.xeross.anniveraire.controller.login.LoginViewModel
import com.xeross.anniveraire.controller.messages.MessageViewModel
import com.xeross.anniveraire.database.BirthdayDataRepository
import com.xeross.anniveraire.database.BirthdayDatabase
import java.util.concurrent.Executors

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel?> create(modelClass: Class<VM>): VM {
        val executor = Executors.newSingleThreadExecutor()
        return when {
            getViewModel<BirthdayViewModel>(modelClass) -> {
                BirthdayViewModel(BirthdayDataRepository(
                        BirthdayDatabase.getInstance(context)?.birthdayDAO()), executor)
            }
            getViewModel<DiscussionViewModel>(modelClass) -> DiscussionViewModel(executor)
            getViewModel<LoginViewModel>(modelClass) -> LoginViewModel(executor)
            getViewModel<MessageViewModel>(modelClass) -> MessageViewModel(executor)
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        } as VM
    }

    private inline fun <reified T> getViewModel(modelClass: Class<*>): Boolean {
        return modelClass.isAssignableFrom(T::class.java)
    }
}