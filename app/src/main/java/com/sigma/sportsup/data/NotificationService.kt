package com.sigma.sportsup.data

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class NotificationService {
    private val client = OkHttpClient()

    fun sendNotificationToTopic(topic: String, title: String, message: String) {
        val url = "http://192.168.40.15:8882/send-push-notification"
        val json = JSONObject()
            .put("topic", topic)
            .put("title", title)
            .put("message", message)
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network errors here
                Log.d("SendNotification", "${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // Handle server errors here
                    Log.d("SendNotification", "Notification failed to send")
                } else {
                    Log.d("SendNotification", "Notification sent successfully")
                }
            }
        })
    }
}
