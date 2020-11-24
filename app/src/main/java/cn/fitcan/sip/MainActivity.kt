package cn.fitcan.sip

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    lateinit var wsReceiver: WSReceiver

    private var callId = "null"
    private var localUri = "null"
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
        val ws_intent = Intent(this, WebSocketService::class.java)
        ws_intent.putExtra("msg_to_send", "no")
        startService(ws_intent)
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

        val intentFilter = IntentFilter()
        intentFilter.addAction("cn.fitcan.action.HANDLE_WS_MESSAGE")
        wsReceiver = WSReceiver()
        registerReceiver(wsReceiver, intentFilter)

        val channelLayout1 = findViewById<ChannelLayout>(R.id.col1)
//        channelLayout1.setChannelID("No.01")
//        channelLayout1.setNumber("9527")
        channelLayout1.setBColor(Color.parseColor("#000000"))
        channelLayout1.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            Log.d("NUMBER", channelLayout1.number)
            var msg_to_send: String = ""
            if (channelLayout1.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callId_needAnswer\": \"${channelLayout1.callID}\", \"callLocalUri_needAnswer\": \"${channelLayout1.number}\", \"callId_handler\": \"${callId}\", \"callLocalUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout1.findViewById<View>(R.id.btn_daiji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout1.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"MUTE CALL\", \"number\": \"${channelLayout1.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout1.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout1.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"HANG UP CALL\", \"number\": \"${channelLayout1.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
                channelLayout1.reset();
            }

        }


        val channelLayout2 = findViewById<ChannelLayout>(R.id.col2)
//        channelLayout2.setChannelID("No.02")
//        channelLayout2.setNumber("9528")
        channelLayout2.setBColor(Color.parseColor("#000000"))
        channelLayout2.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout2.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callId_needAnswer\": \"${channelLayout2.callID}\", \"callLocalUri_needAnswer\": \"${channelLayout2.number}\", \"callId_handler\": \"${callId}\", \"callLocalUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout2.findViewById<View>(R.id.btn_daiji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout2.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"MUTE CALL\", \"number\": \"${channelLayout2.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout2.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout2.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"HANG UP CALL\", \"number\": \"${channelLayout2.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
                channelLayout2.reset()
            }

        }


        val channelLayout3 = findViewById<ChannelLayout>(R.id.col3)
//        channelLayout3.setChannelID("No.03")
//        channelLayout3.setNumber("9529")
        channelLayout3.setBColor(Color.parseColor("#000000"))
        channelLayout3.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout3.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callId_needAnswer\": \"${channelLayout3.callID}\", \"callLocalUri_needAnswer\": \"${channelLayout3.number}\", \"callId_handler\": \"${callId}\", \"callLocalUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout3.findViewById<View>(R.id.btn_daiji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout3.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"MUTE CALL\", \"number\": \"${channelLayout3.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout3.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout3.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"HANG UP CALL\", \"number\": \"${channelLayout3.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
                channelLayout3.reset()
            }

        }


        val channelLayout4 = findViewById<ChannelLayout>(R.id.col4)
//        channelLayout4.setChannelID("No.04")
//        channelLayout4.setNumber("9530")
        channelLayout4.setBColor(Color.parseColor("#000000"))
        channelLayout4.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout4.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callId_needAnswer\": \"${channelLayout4.callID}\", \"callLocalUri_needAnswer\": \"${channelLayout4.number}\", \"callId_handler\": \"${callId}\", \"callLocalUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout4.findViewById<View>(R.id.btn_daiji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout4.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"MUTE CALL\", \"number\": \"${channelLayout4.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout4.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout4.number!=="0000") {
                msg_to_send = "{ \"msgType\": \"HANG UP CALL\", \"number\": \"${channelLayout4.number}\"}"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
                channelLayout4.reset()
            }

        }




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

    fun changeStatus() {
        findViewById<LinearLayout>(R.id.col0).setBackgroundColor(Color.GREEN);
    }

    inner class WSReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val x = intent.extras?.get("msg");
                Log.d("HANDLE MESSAGE PRE", x.toString())
                if(intent.extras?.get("msg") == "connected") {
                    Log.d("HANDLE MESSAGE", "connected...............")
                }else if(intent.extras?.get("msg") == "disconnected") {
//                    Log.d("HANDLE MESSAGE", "disconnected...............")
                }else if(intent.extras?.get("msg") == "callinfo") {
                    Log.d("ID", intent.extras?.get("callId").toString())
                    Log.d("LOCAL URI", intent.extras?.get("localUri").toString())
                    callId = intent.extras?.get("callId").toString()
                    localUri = intent.extras?.get("localUri").toString()
                }else {
                    val msg_json = JSONObject(intent.extras?.get("msg").toString())
                    when(msg_json.get("msgType").toString()) {
                        "NEW CALL" -> {
                            Log.d("HANDLE MESSAGE", "new call......................")
                            val channelLayout1 = findViewById<ChannelLayout>(R.id.col1)
                            val channelLayout2 = findViewById<ChannelLayout>(R.id.col2)
                            val channelLayout3 = findViewById<ChannelLayout>(R.id.col3)
                            val channelLayout4 = findViewById<ChannelLayout>(R.id.col4)
                            if (channelLayout1.number == "0000") {
                                channelLayout1.setChannelID(msg_json.get("id").toString())
                                channelLayout1.setNumber(msg_json.get("localUri").toString())
                                channelLayout1.setBColor(Color.parseColor("#FFDDDD"))
                            }else if(channelLayout2.number == "0000") {
                                channelLayout2.setChannelID(msg_json.get("id").toString())
                                channelLayout2.setNumber(msg_json.get("localUri").toString())
                                channelLayout2.setBColor(Color.parseColor("#DDDDDD"))
                            }else if(channelLayout3.number == "0000") {
                                channelLayout3.setChannelID(msg_json.get("id").toString())
                                channelLayout3.setNumber(msg_json.get("localUri").toString())
                                channelLayout3.setBColor(Color.parseColor("#FFDDDD"))
                            }else if(channelLayout4.number == "0000") {
                                channelLayout4.setChannelID(msg_json.get("id").toString())
                                channelLayout4.setNumber(msg_json.get("localUri").toString())
                                channelLayout4.setBColor(Color.parseColor("#DDDDDD"))
                            }
                        }
                        "ANSWER CALL ACK" -> {
                            Log.d("HANDLE MESSAGE", "answer call ack...............")
                        }
                        "HANG UP CALL ACK" -> {
                            Log.d("HANDLE MESSAGE", "hang up call ack..............")
                        }
                    }
                }
            }
        }
    }
}