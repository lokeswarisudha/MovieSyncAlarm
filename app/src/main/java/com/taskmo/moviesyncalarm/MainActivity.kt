package com.taskmo.moviesyncalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.media.MediaActionSound
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.taskmo.moviesyncalarm.TimerService.MyBinder
import com.taskmo.moviesyncalarm.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity(),BroadcastListener {
    lateinit var receiver: MyAlarm
     lateinit var binding: ActivityMainBinding
    var mServiceBound = false
    var mBoundService: TimerService? = null
    lateinit var text:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // registerReceiver()
        binding.startTimer.setOnClickListener {
            TimerService.startService(this, "Foreground Service is running...")
            var calendar: Calendar = Calendar.getInstance()
            setAlarm(calendar.timeInMillis)

        }

        TimerService.mutableLiveData.observe(this, androidx.lifecycle.Observer { millisUntilF->

            var millisUntilFinished =millisUntilF
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60

            val elapsedHours = millisUntilFinished / hoursInMilli
            millisUntilFinished = millisUntilFinished % hoursInMilli

            val elapsedMinutes = millisUntilFinished / minutesInMilli
            millisUntilFinished = millisUntilFinished % minutesInMilli
            val elapsedSeconds = millisUntilFinished / secondsInMilli
            val time = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes,elapsedSeconds)
            binding.timer.setText(""+time)
            Log.e("TAG", "live data ")

        })


        binding.stopTimer.setOnClickListener {

            stopService(Intent(this, TimerService::class.java))

                //TimerService.stopService(this)

          /*  if (mServiceBound) {
                unbindService(mServiceConnection)
                mServiceBound = false
            }
            val intent = Intent(
                this@MainActivity,
                TimerService::class.java
            )
            stopService(intent)*/
        }


    }


    private fun setAlarm(timeInMillis: Long) {

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val sound = MediaActionSound()
                 sound.play(MediaActionSound.START_VIDEO_RECORDING)

                Log.e("Alarm Bell", "alaram play ")

            }
        },30000,60000)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            timeInMillis,60000,
            pendingIntent
        )
    }


    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction("com.taskmo.moviesyncalarm")
        receiver = MyAlarm()
        registerReceiver(receiver, filter)
    }

    override fun onStart() {
        super.onStart()
       /* val intent = Intent(this, TimerService::class.java)
        startService(intent)*/
       // bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
    }




    override fun onResume() {
        super.onResume()




    }

    override fun onStop() {
        super.onStop()
        /*if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }*/
       // unregisterReceiver(receiver)
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mServiceBound = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as MyBinder
            mBoundService = myBinder.service
            mServiceBound = true
        }

    }


    class MyAlarm : BroadcastReceiver() {
        /*private var listeners: BroadcastListener? = null
        construtor (listeenr: BroadcastListener){
            this.listeners = listeenr
        }*/
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {

           // Log.e("Alarm Bell", "Alarm just fired")
            val sound = MediaActionSound()
           // sound.play(MediaActionSound.START_VIDEO_RECORDING)
            /* private fun playSound() {
             val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
             val ringtoneSound: Ringtone = RingtoneManager.getRingtone(MainActivity.this, ringtoneUri)
             ringtoneSound.play()
         }*/
        }

    }

    override fun updateUI(data: Long) {

    }

}


