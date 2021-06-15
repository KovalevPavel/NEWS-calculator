package com.github.newscalculator.notification

/**
 * Класс, описывающий каналы оповещений
 * @param channelName имя канала
 * @param channelType тип приходящего сообщения
 * @param channelId id канала
 * @param channelPriority приоритет канала
 * @param channelDescription описание канала
 */
class NotifChannel(
    val channelName: String,
    val channelType: NotificationType,
    val channelId: String,
    val channelPriority: Int,
    val channelDescription: String
)