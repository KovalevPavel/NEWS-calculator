package com.github.newscalculator.notification

import com.github.newscalculator.util.loggingDebug
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushService: FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        loggingDebug("token: $newToken")
    }
    override fun onMessageReceived(newMessage: RemoteMessage) {
        super.onMessageReceived(newMessage)
        loggingDebug("message received: ${newMessage.data}")
    }
}