package com.darshan.notificity.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationHandler by lazy { NotificationHandler(applicationContext) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val notificationContent = NotificationContent.fromRemoteMessage(remoteMessage) ?: return

        CoroutineScope(Dispatchers.Default).launch {
            notificationHandler.handleNotification(notificationContent)
        }
    }

    override fun onNewToken(token: String) {}
}
