package com.sigma.sportsup.system

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.RemoteMessage

class NotificationUtils {
    fun sendNotificationToUser(token: String, title: String, body: String) {
        val message = RemoteMessage.Builder(token)
            .addData("title", title)
            .addData("body", body)
            .build()

        Firebase.messaging.send(message)
    }
    fun sendNotificationToTopic(topic: String, title: String, body: String) {
        val message = RemoteMessage.Builder(topic)
            .addData("title", title)
            .addData("body", body)
            .build()

        Firebase.messaging.send(message)
    }
}

enum class NotificationType {
    NEW_EVENT,
    EVENT_UPDATE ,
    EVENT_CANCEL  ,
    EVENT_JOIN  ,
    EVENT_LEAVE ,
    EVENT_COMMENT,
    EVENT_COMMENT_REPLY,
    EVENT_COMMENT_LIKE ,
    EVENT_COMMENT_REPLY_LIKE ,
    EVENT_COMMENT_REPLY_REPLY,
    EVENT_COMMENT_REPLY_REPLY_LIKE

}