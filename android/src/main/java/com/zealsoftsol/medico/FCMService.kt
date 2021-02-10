package com.zealsoftsol.medico

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        (application as MedicoApp).messaging.handleMessage(message.data)
    }

    override fun onNewToken(token: String) {
        (application as MedicoApp).messaging.handleNewToken(token)
    }
}