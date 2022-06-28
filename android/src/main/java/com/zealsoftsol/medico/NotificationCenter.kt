package com.zealsoftsol.medico

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.zealsoftsol.medico.core.notifications.FirebaseMessaging
import com.zealsoftsol.medico.core.notifications.NotificationMessage
import com.zealsoftsol.medico.core.repository.UserRepo
import io.karn.notify.Notify

class NotificationCenter(
    private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,
) {

    init {
        firebaseMessaging.notificationMessage.observeOnUi {
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
            .header {
                icon = R.mipmap.ic_launcher_foreground
            }
            /*.alerting("medico") {
                channelImportance = Notify.IMPORTANCE_MAX
            }
            */.alerting("medico") {
                channelImportance = Notify.IMPORTANCE_NORMAL

                if (context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                        .getBoolean(UserRepo.ALERT_TOGGLE, false)
                ) {
                    sound = Uri.parse(
                        ContentResolver.SCHEME_ANDROID_RESOURCE
                                + "://" + context.packageName + "/raw/alert"
                    )
                }
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