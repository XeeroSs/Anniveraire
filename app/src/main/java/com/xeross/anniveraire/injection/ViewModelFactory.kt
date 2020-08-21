package com.xeross.anniveraire.injection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xeross.anniveraire.controller.event.BirthdayViewModel
import com.xeross.anniveraire.database.BirthdayDataRepository
import com.xeross.anniveraire.database.BirthdayDatabase
import java.util.concurrent.Executors

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel?> create(modelClass: Class<VM>): VM {
        if (modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
            return BirthdayViewModel(BirthdayDataRepository(BirthdayDatabase.getInstance(context)?.birthdayDAO()),
                    Executors.newSingleThreadExecutor()) as VM
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}