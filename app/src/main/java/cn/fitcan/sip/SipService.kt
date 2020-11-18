package cn.fitcan.sip

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class SipService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Sip Service", "onCreate executed")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sip_service", "前台servie通知", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel);
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0);
        val notification = NotificationCompat.Builder(this, "sip_service")
                .setContentTitle("This is a content title")
                .setContentText("This is content text")
                .setContentIntent(pi)
                .build()
        startForeground(1, notification);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Sip Service", "onStartCommand executed");
        thread {
            Log.d("SipService", "Thread is ${Thread.currentThread().name}")
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Sip Service", "onDestroy executed");
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}