package com.sameh.postnotificationwithworkmanager.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sameh.postnotificationwithworkmanager.MainActivity
import com.sameh.postnotificationwithworkmanager.R
import com.sameh.postnotificationwithworkmanager.data.DemoApi
import com.sameh.postnotificationwithworkmanager.toLogE
import com.sameh.postnotificationwithworkmanager.toLogW
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repo: DemoApi
) : CoroutineWorker(context, params) {

    companion object {
        val WORK_NAME: String = Companion::class.java.name
        const val TITLE_KEY = "title_key"
        const val MESSAGE_KEY = "message_key"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            val response = repo.getPosts()
            if (response.isNotEmpty()) {
                response.shuffle()
                withContext(Dispatchers.Main) {
                    showNotification(response.first().title, response.first().body)
                }
            }
        } catch (e: Exception) {
            "Exception: ${e.message.toString()}".toLogE()
            return if (runAttemptCount > 5) {
                "worker success".toLogW()
                Result.success()
            } else {
                "worker retry".toLogW()
                Result.retry()
            }
        }
        return Result.success()
    }

    private fun getBigCustomDesign(
        title: String?,
        message: String?
    ): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification_custom_layout_big
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.drawable.ic_funny
        )
        return remoteViews
    }

    /**
     * Method to get an Small Notification custom Design for the display of notification.
     */
    private fun getSmallCustomDesign(
        title: String?,
    ): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification_custom_layout_small
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.drawable.ic_funny
        )
        return remoteViews
    }

    // Method to display the notifications
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(
        title: String,
        message: String
    ) {
        // Pass the intent to switch to the MainActivity
        val intent = Intent(context, MainActivity::class.java)
            .putExtra(TITLE_KEY, title)
            .putExtra(MESSAGE_KEY, message)
        // Assign channel ID
        val channelId = "notification_channel"
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            (PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        )
        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setSmallIcon(R.drawable.ic_funny)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder
            .setCustomContentView(getSmallCustomDesign(title))
            .setCustomBigContentView(getBigCustomDesign(title, message))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        // Check if the Android Version is greater than Oreo
        val notificationChannel = NotificationChannel(
            channelId, "channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(
            notificationChannel
        )
        notificationManager.notify(0, builder.build())
    }
}