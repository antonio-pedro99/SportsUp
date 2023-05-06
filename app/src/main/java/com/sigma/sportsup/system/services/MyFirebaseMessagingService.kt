package com.sigma.sportsup.system.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sigma.sportsup.R
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessagingService"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelId: String = "com.sigma.sportsup"
        val title: String = message.notification?.title!!
        val body: String = message.notification?.body!!
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "SportsUp", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        notificationManager.notify(Random(100).nextInt(), notificationBuilder.build())

        Log.d(TAG, title.toString())

    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, token)
        val db = Firebase.firestore

        val currentUserId =  getUserCurrentId()

        db.collection("users").document(currentUserId!!).update("token", token)
            .addOnSuccessListener { Log.d(TAG, "Token updated") }.addOnFailureListener {
            Log.d(TAG, "Token update failed")
        }


    }
    private fun getUserCurrentId(): String? {
        val prefs = getSharedPreferences("USER_ID", MODE_PRIVATE)
        val restoredText = prefs.getString("currentuser", null)
        return if (restoredText != null) {
            prefs.getString("currentuser", "No name defined")
        } else null
    }

}