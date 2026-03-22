package com.example.draw

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SmsAlertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sender = intent.getStringExtra("sender") ?: "Unknown"
        val body = intent.getStringExtra("body") ?: ""
        
        AlertDialog.Builder(this)
            .setTitle("New Message")
            .setMessage("From: $sender\n\n$body")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }
}