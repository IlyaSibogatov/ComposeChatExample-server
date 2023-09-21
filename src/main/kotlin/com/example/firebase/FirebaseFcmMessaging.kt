package com.example.firebase

import com.example.data.model.NotificationModel
import com.example.data.model.NotificationType
import com.example.utils.Constants.FAILED_TAG
import com.example.utils.Constants.SUCCESS_TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun sendMessage(
    notification: NotificationModel,
    id: String,
    type: NotificationType,
    senderId: String,
    senderName: String,
): Boolean {
    var result = false
    withContext(Dispatchers.IO) {
        println("TOKEN IS ---> ${notification.token}")
        val messageData = Message.builder()
            .putData("title", notification.title)
            .putData("body", notification.body)

            .putData("id", id)
            .putData("type", type.value)
            .putData("senderId", senderId)
            .putData("senderName", senderName)

            .setToken(notification.token)
        result = try {
            FirebaseMessaging.getInstance().send(messageData.build())
            println("CLOUD MESSAGE SEND SUCCESS $SUCCESS_TAG")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            println("CLOUD MESSAGE SEND FAILED $FAILED_TAG")
            false
        }
    }
    return result
}