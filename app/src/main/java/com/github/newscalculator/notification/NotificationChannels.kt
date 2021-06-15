package com.github.newscalculator.notification

import androidx.core.app.NotificationManagerCompat

/**
 * Объект, содержащий данные для каналов оповещения
 */
object NotificationChannels {
    val CHANNELS = listOf(
        NotifChannel(
            "Update notifications",
            NotificationType.UPDATE_MESSAGE,
            "update",
            NotificationManagerCompat.IMPORTANCE_HIGH,
            "Канал оповещения о новых версиях приложения",
        )
    )
}