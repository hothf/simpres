package de.ka.simpres.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.net.Uri
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.work.*
import de.ka.simpres.R
import de.ka.simpres.repo.model.SubjectItem
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * A manager for notification trigger tasks.
 */
class NotificationWorkManager(private val context: Context) {

    /**
     * Enqueues the task of notification of a subject item, if wanted.
     * Will replace the current notification if one was already scheduled.
     *
     *@param subject the subject to notify of
     */
    fun enqueueNotificationWorkFor(subject: SubjectItem) {

        val workTag = "notificationWork${subject.id}"

        val inputData = Data.Builder().putLong("subjectId", subject.id).build()

        if (!subject.pushEnabled) {
            return
        }

        // TODO and improve!

        val delay = (subject.date + 5_000) - System.currentTimeMillis()
        Timber.e("Time! $delay")

        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(workTag)
            .build()

        WorkManager.getInstance(context).beginUniqueWork(workTag, ExistingWorkPolicy.REPLACE, notificationWork)
            .enqueue()
    }

    /**
     * Stops all enqueued notification tasks of a subject.
     *
     * @param subject the subject to stop notifications of
     */
    fun cancelNotificationWorkFor(subject: SubjectItem) {

        val workTag = "notificationWork${subject.id}"

        WorkManager.getInstance(context).cancelAllWorkByTag(workTag)
    }

}

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @NonNull
    override fun doWork(): Result {
        triggerNotification(applicationContext, inputData.getLong("subjectId", -1L))

        return Result.success()
    }

    private fun triggerNotification(context: Context, subjectId: Long) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notificationIntent = buildDeeplinkIntent(subjectId)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "Daily Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Daily Notification"
            nm?.createNotificationChannel(channel)
        }
        val b = NotificationCompat.Builder(context, "default")
        b.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_present)
            .setTicker("{Time to watch some cool stuff!}")
            .setContentTitle("My Cool App")
            .setContentText("Time to watch some cool stuff!")
            .setContentInfo("INFO")
            .setContentIntent(pendingIntent)

        nm?.notify(1, b.build()) // TODO Improve please
    }

    private fun buildDeeplinkIntent(subjectId: Long): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse("https://simpres.com/$subjectId"))
    }
}