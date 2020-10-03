package com.xeross.anniveraire.controller

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.xeross.anniveraire.utils.AlarmReceiver
import java.util.*

class App : Application() {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate() {
        super.onCreate()
        // J'appelle l'alarmManager pour les notifications uniquement lors du lancement de l'application
        initializeAlarmManager()
    }

    private fun initializeAlarmManager() {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
        }

        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        alarmMgr?.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent)
    }

}