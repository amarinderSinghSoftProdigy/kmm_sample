package com.zealsoftsol.medico

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.zealsoftsol.medico.core.notifications.FirebaseMessaging
import com.zealsoftsol.medico.core.notifications.NotificationMessage
import io.karn.notify.Notify

class NotificationCenter(
    private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,
) {

    init {
        firebaseMessaging.notifications.observeOnUi {
            if (it != null) showMessage(it)
        }
    }

    private fun showMessage(message: NotificationMessage) {
        Notify.with(context)
            .meta {
                clickIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java).apply {
                        putExtra(DISMISS_NOTIFICATION_ID, message.id)
                    },
                    0,
                )
            }
            .alerting("medico") {
                channelImportance = Notify.IMPORTANCE_MAX
            }
            .content {
                title = message.title
                text = message.body
            }.show()
    }

    companion object {
        const val DISMISS_NOTIFICATION_ID = "dismiss_not"
    }
}