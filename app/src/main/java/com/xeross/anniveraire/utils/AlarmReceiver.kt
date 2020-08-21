package com.xeross.anniveraire.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.MainActivity
import com.xeross.anniveraire.utils.Constants.CHANNEL_DESCRIPTION
import com.xeross.anniveraire.utils.Constants.CHANNEL_ID
import com.xeross.anniveraire.utils.Constants.CHANNEL_NAME
import com.xeross.anniveraire.utils.Constants.TEST_ID

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //do stuff with room...
        context?.let {
            // TEST
            sendNotification("test", "C'est l'anniversaire de x, il a 29 ans", it)
        }
    }

    private fun sendNotification(title: String, contentText: String, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_birthday_cake)
            setContentTitle(title)
            setContentText(contentText)
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            setContentIntent(pendingIntent)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        NotificationManagerCompat.from(context).notify(/*id room*/TEST_ID, builder.build())
    }
}
