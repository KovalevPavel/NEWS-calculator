package com.github.newscalculator.notification

import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.newscalculator.R
import com.github.newscalculator.util.loggingDebug
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushService : FirebaseMessagingService() {
    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val TYPE = "messageType"
        private const val MESSAGE_REQUEST_CODE = 1
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        loggingDebug("new token: $newToken")
    }

    override fun onMessageReceived(newMessage: RemoteMessage) {
        super.onMessageReceived(newMessage)
        val messageData = newMessage.data
        showMessageNotification(messageData)
    }

    private fun showMessageNotification(messageData: Map<String, String>) {
        val packageName = this@PushService.packageName
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
        val pendingIntent = PendingIntent.getActivity(this, MESSAGE_REQUEST_CODE, intent, 0)
        val messageType =
            NotificationType.valueOf(messageData[TYPE] ?: error("Unexpected message type"))

        val notificationChannelId = NotificationChannels.CHANNELS.first {
            it.channelType == messageType
        }.channelId

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(messageData[TITLE])
            .setContentText(messageData[MESSAGE])
            .setVibrate(longArrayOf(100, 100, 100, 100, 500))
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageData[MESSAGE]))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this)
            .notify(0, notification)
    }
}