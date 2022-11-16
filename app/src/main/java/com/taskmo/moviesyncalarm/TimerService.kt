package com.taskmo.moviesyncalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

class TimerService : Service() {
    private val CHANNEL_ID = "TimerService Kotlin"
    private val mBinder: IBinder = MyBinder()
    val sdf = SimpleDateFormat("hh:mm:ss")


    companion object {

        val mutableLiveData:MutableLiveData<Long> = MutableLiveData()

        fun startService(context: Context,message: String)
        {
            val startIntent = Intent(context,TimerService::class.java)
            startIntent.putExtra("input",message)
            ContextCompat.startForegroundService(context,startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, TimerService::class.java)
            context.stopService(stopIntent)

        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("input")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TimerService Kotlin")
            .setContentText(input)
            .setSmallIcon(androidx.loader.R.drawable.notification_bg)
            .setContentIntent(pendingIntent)
            .build()

        val timer = object: CountDownTimer(7200000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mutableLiveData.postValue(millisUntilFinished)

            }

            override fun onFinish() {

            }

        }
        timer.start()


        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {

        return mBinder
    }

    private fun sendData(time: String,) {
        Log.e("Tag", "inside sendData")
        val intent = Intent()
        intent.action = "com.taskmo.moviesyncalarm"
        intent.putExtra("inputExtra", time)
        sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "TimerService Kotlin",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }



    inner class MyBinder : Binder() {
        val service: TimerService
            get() = this@TimerService
    }

}