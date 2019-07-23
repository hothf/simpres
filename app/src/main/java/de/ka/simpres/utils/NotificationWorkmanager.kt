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
import de.ka.simpres.utils.NotificationWorker.Constants.workerTagFor
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
     * If the subject does not want pushes or the reminder date is beyond the current time, no work will be scheduled.
     *
     *@param subject the subject to notify of
     */
    fun enqueueNotificationWorkFor(subject: SubjectItem) {
        if (!subject.pushEnabled) {
            return
        }

        val workerTag = workerTagFor(subject)

        val delay = subject.date - System.currentTimeMillis()
        Timber.i("Sending a notification in: $delay")

        val inputData = Data.Builder()
            .putLong(NotificationWorker.Constants.KEY_FOR_SUBJECT_ID, subject.id)
            .putString(NotificationWorker.Constants.KEY_FOR_SUBJECT_TITLE, subject.title)
            .build()

        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(workerTag)
            .build()

        WorkManager
            .getInstance(context)
            .beginUniqueWork(workerTag, ExistingWorkPolicy.REPLACE, notificationWork)
            .enqueue()
    }

    /**
     * Stops all enqueued notification tasks of a subject.
     *
     * @param subject the subject to stop notifications of
     */
    fun cancelNotificationWorkFor(subject: SubjectItem) {
        WorkManager.getInstance(context).cancelAllWorkByTag(workerTagFor(subject))
    }

}

/**
 * A worker dedicated to trigger notifications.
 */
class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @NonNull
    override fun doWork(): Result {
        triggerSubjectNotification(
            applicationContext,
            inputData.getLong(Constants.KEY_FOR_SUBJECT_ID, -1L),
            inputData.getString(Constants.KEY_FOR_SUBJECT_TITLE)
        )

        return Result.success()
    }

    private fun triggerSubjectNotification(context: Context, subjectId: Long, subjectName: String? = "") {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val channelId = context.getString(R.string.notifications_channel_date)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.notifications_channel_date_info),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = context.getString(R.string.notifications_channel_date_description) }
            notificationManager?.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, channelId).setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_present)
            .setContentTitle(context.getString(R.string.notifications_date_content_title, subjectName))
            .setContentText(context.getString(R.string.notifications_date_content_text))
            .setContentIntent(PendingIntent.getActivity(context, 0, buildDeeplinkIntent(subjectId), 0))
            .build()
        notificationManager?.notify(
            Constants.NOTIFICATION_ID_DATES, notification
        )
    }

    private fun buildDeeplinkIntent(subjectId: Long): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse("https://simpres.com/$subjectId"))
    }

    object Constants {
        const val NOTIFICATION_ID_DATES: Int = 101

        const val KEY_FOR_SUBJECT_ID = "k_sub_id"
        const val KEY_FOR_SUBJECT_TITLE = "k_sub_title"

        /**
         * Retrieves a worker tag for the given subject.
         */
        fun workerTagFor(subject: SubjectItem) = "notificationWork${subject.id}"
    }
}