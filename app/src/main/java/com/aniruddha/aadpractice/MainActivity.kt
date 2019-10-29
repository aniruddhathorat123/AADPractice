package com.aniruddha.aadpractice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var jobScheduler : JobScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationButton.setOnClickListener(this)
        scheduleJob.setOnClickListener(this)
        cancelJob.setOnClickListener(this)
        idleSwitch.setOnClickListener(this)
        chargingSwitch.setOnClickListener(this)

        // SeekBar sets the deadline to execute the task between 0 to 100.
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress > 0) {
                    seekBarProgress.text = progress.toString()
                } else {
                    seekBarProgress.text = "Not Set"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Calls custom toast that was created
        customToast("Action !!!")

        // Create Notification channel
        createNotificationChannel()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.notificationButton -> startNotification()
            R.id.scheduleJob -> scheduleJob()
            R.id.cancelJob -> cancelJob()
        }
    }

    /**
     * function to create the notification with given channel and had different parameters.
     * you can remove setStyle to see download like notification.
     */
    private fun startNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this,0,intent,0)

        var currentPregress = AADConstants.PROGRESS_CURRENT

        val builder = NotificationCompat.Builder(this,AADConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notifcation_detail))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setOnlyAlertOnce(true)
            .setProgress(AADConstants.PROGRESS_MAX, AADConstants.PROGRESS_CURRENT, false)
            .setStyle(NotificationCompat.MessagingStyle("Me")
                .setConversationTitle("Group Chat")
                .addMessage("Hi Guys", 1L, "Donald Trump")
                .addMessage("Hey Trump, What's Going on?", 2L, "Narendra Modi")
                .addMessage("Yo Modi, We done with Bagdadi", 3L, "Donald Trump")
                .addMessage("Now it's your time to finish POK matter",
                    4L, "Donald Trump")
                .addMessage("Yup, Sure...", 5L, "Narendra Modi"))

        NotificationManagerCompat.from(this).apply {
            notify(AADConstants.NOTIFICATION_ID, builder.build())
            Thread(Runnable {
                while (currentPregress != 100) {
                    SystemClock.sleep(1000)
                    currentPregress += 5
                    builder.setProgress(AADConstants.PROGRESS_MAX, currentPregress, false)
                    notify(AADConstants.NOTIFICATION_ID, builder.build())
                }
            }).start()
            builder.setProgress(0, 0, false)
            notify(AADConstants.NOTIFICATION_ID, builder.build())
        }
    }

    /**
     * create custom toast with given string description to show.
     * Should be public, so other classes can use this.
     */
    fun customToast(data: String) {
        val inflater = layoutInflater
        val layout : ViewGroup = inflater.inflate(R.layout.custom_toast,
            findViewById(R.id.customToastContainer)) as ViewGroup
        val toastData : TextView = layout.findViewById(R.id.toastText)
        toastData.text = data
        with (Toast(applicationContext)) {
            setGravity(Gravity.CENTER_HORIZONTAL, 0,0)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    /**
     * create the channel for the notification.
     * channel actually created only once, and used again if called again.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sample channel"
            val detailText = "description for sample channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(AADConstants.CHANNEL_ID, name, importance)
                .apply {
                    description = detailText
                    enableVibration(true)
                    enableLights(true)
                    lightColor = Color.RED
                }

            val notificationManager : NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Creates the JobInfo object which gives the set of conditions to trigger the job.
     * Also create JobScheduler which schedule the job based on this constraints.
     */
    private fun scheduleJob() {
        val networkOption = networkOption as RadioGroup
        var selectedNetOpt = JobInfo.NETWORK_TYPE_NONE
        val seekBarValue = seekBar.progress
            jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        when (networkOption.checkedRadioButtonId) {
            R.id.noNetwork -> selectedNetOpt = JobInfo.NETWORK_TYPE_NONE
            R.id.anyNetwork -> selectedNetOpt = JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> selectedNetOpt = JobInfo.NETWORK_TYPE_UNMETERED
        }

        // ComponentName is used to associate the JobService with the JobInfo object.
        val componentName = ComponentName(packageName, NotificationJobService::class.java.name)

        val jobBuilder = JobInfo.Builder(AADConstants.SCHEDULE_JOB_ID, componentName)
            .setRequiredNetworkType(selectedNetOpt)
            .setRequiresDeviceIdle(idleSwitch.isChecked)
            .setRequiresCharging(chargingSwitch.isChecked)

        if (seekBarValue > 0) {
            // setOverrideDeadline parameter is in milliseconds.
            jobBuilder.setOverrideDeadline(seekBarValue * 1000L)
        }

        if (selectedNetOpt != JobInfo.NETWORK_TYPE_NONE || idleSwitch.isChecked
            || chargingSwitch.isChecked || seekBarValue > 0) {
            jobScheduler.schedule(jobBuilder.build())
            customToast("Job Scheduled, job will run when the constraints are met.")
        }
        else {
            customToast("please Set at least one constrain...")
        }
    }

    /**
     * Cancel all the jobs if there is any.
     */
    private fun cancelJob() {
        jobScheduler.cancelAll()
        customToast("All Jobs Cancelled...")
    }
}
