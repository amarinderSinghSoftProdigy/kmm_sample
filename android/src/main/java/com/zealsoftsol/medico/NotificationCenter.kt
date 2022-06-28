package com.zealsoftsol.medico

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Vibrator
import android.util.Log
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
        val mediaPlayer = MediaPlayer.create(context, R.raw.alert)
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

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
                    this.sound =
                        Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alert)
                    Log.e("sound ", " " + this.sound)

                }
            }.content {
                title = message.title
                text = message.body
            }.show()
        if (context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getBoolean(UserRepo.ALERT_TOGGLE, false)
        ) {
            vibrator.vibrate(200)
            mediaPlayer.start()
        }
    }

    companion object {
        const val DISMISS_NOTIFICATION_ID = "dismiss_not"
    }
}