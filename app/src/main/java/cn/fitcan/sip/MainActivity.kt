package cn.fitcan.sip

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE, Manifest.permission.USE_SIP, Manifest.permission.CAMERA)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()
        val intent = Intent(this, SipService::class.java)
        startService(intent)
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val a: Activity = this
        supportActionBar!!.hide()
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        val channelLayout1 = findViewById<ChannelLayout>(R.id.col1)
        channelLayout1.setChannelID("No.01")
        channelLayout1.setNumber("9527")
        channelLayout1.setBColor(Color.parseColor("#DDDDDD"))
        channelLayout1.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") //SIP拨号阻塞？
        }
        val channelLayout2 = findViewById<ChannelLayout>(R.id.col2)
        channelLayout2.setChannelID("No.02")
        channelLayout2.setNumber("9528")
        channelLayout2.setBColor(Color.parseColor("#FFDDDD"))
        val channelLayout3 = findViewById<ChannelLayout>(R.id.col3)
        channelLayout3.setChannelID("No.03")
        channelLayout3.setNumber("9529")
        channelLayout3.setBColor(Color.parseColor("#DDDDDD"))
        val channelLayout4 = findViewById<ChannelLayout>(R.id.col4)
        channelLayout4.setChannelID("No.04")
        channelLayout4.setNumber("9530")
        channelLayout4.setBColor(Color.parseColor("#FFDDDD"))
        val btn_startsvc = findViewById<View>(R.id.btn_startsvc) as Button
        btn_startsvc.setOnClickListener {
            if (!permissionToRecordAccepted) finish()
            Log.d("MainActivity", "start service now!!!")
            Log.d("MainActivity", "Thread is " + Thread.currentThread().name)
            val intent = Intent(a, SipService::class.java)
            startService(intent)
        }
        val btn_stopsvc = findViewById<View>(R.id.btn_stopsvc) as Button
        btn_stopsvc.setOnClickListener {
            println("stop service now!!!")
            val intent = Intent(a, SipService::class.java)
            stopService(intent)
            val pid = Process.myPid()
            Process.killProcess(pid)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if(event.keyCode==KeyEvent.KEYCODE_BACK) {
                if(isTaskRoot()) {
                    moveTaskToBack(false);
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        println("Destroy Destroy Destroy Destroy Destroy Destroy")
        super.onDestroy()
        val intent = Intent(this, SipService::class.java)
        stopService(intent)
        val pid = Process.myPid()
        Process.killProcess(pid)
    }
}