package com.aniruddha.aadpractice

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Class that implements the job to run under the conditions set by JobInfo.
 */
class NotificationJobService : JobService() {

    /**
     * Called when job gets schedule.
     * Returns true if job need to be run on separate thread, otherwise false.
     * if true then explicitly call onStopJob.
     */
    override fun onStartJob(params: JobParameters?): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent : PendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, AADConstants.CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_detail))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).apply {

            notify(0, builder.build())
        }
        return false
    }

    /**
     * Returns true, because if job fails then it reschedule job.
     * if returns false then job gets dropped.
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}