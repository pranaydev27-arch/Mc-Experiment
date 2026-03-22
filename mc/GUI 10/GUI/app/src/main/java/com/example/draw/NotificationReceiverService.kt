package com.example.draw

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationReceiverService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: "No Title"
        val text = extras.getCharSequence("android.text")?.toString() ?: "No Content"

        // Ignore notifications from our own app to avoid infinite loops
        if (packageName == applicationContext.packageName) return

        Log.d("NotificationReceiver", "From: $packageName, Title: $title, Text: $text")

        // Trigger the Alert Activity
        val alertIntent = Intent(this, SmsAlertActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("sender", "$title ($packageName)")
            putExtra("body", text)
        }
        startActivity(alertIntent)
    }
}