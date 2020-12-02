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
import cn.fitcan.sip.CallStatus


class MainActivity : AppCompatActivity() {
    lateinit var wsReceiver: WSReceiver

    private var online = false
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

        var channelLayout1 = findViewById<ChannelLayout>(R.id.col1)
        channelLayout1.setBColor(Color.parseColor("#000000"))
        channelLayout1.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            Log.d("NUMBER", channelLayout1.number)
            var msg_to_send: String = ""
            if (channelLayout1.callStatus==CallStatus.呼入 || channelLayout1.callStatus==CallStatus.待播) {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callUri_needHandle\": \"${channelLayout1.number}\", \"callUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }
        }
        channelLayout1.findViewById<View>(R.id.btn_daibo).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout1.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"UPDATE CALL STATE\", \"callUri\": \"${channelLayout1.number}\", \"callState\": 3 }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout1.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout1.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"HANGUP CALL\", \"callUri\": \"${channelLayout1.number}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }
        }
//        channelLayout1.findViewById<View>(R.id.btn_kaimai).setOnClickListener {
//            var msg_to_send = ""
//            if (channelLayout1.callStatus==CallStatus.待播) {
//                msg_to_send = "{ \"msgType\": \"UPDATE CALL MIC STATE\", \"callUri\": \"${channelLayout1.number}\", \"micState\": 1 }"
//                val intent = Intent(this, WebSocketService::class.java)
//                intent.putExtra("msg_to_send", msg_to_send)
//                startService(intent)
//            }
//        }


        var channelLayout2 = findViewById<ChannelLayout>(R.id.col2)
        channelLayout2.setBColor(Color.parseColor("#000000"))
        channelLayout2.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout2.callStatus==CallStatus.呼入 || channelLayout2.callStatus==CallStatus.待播) {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callUri_needHandle\": \"${channelLayout2.number}\", \"callUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout2.findViewById<View>(R.id.btn_daibo).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout2.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"UPDATE CALL STATE\", \"callUri\": \"${channelLayout2.number}\", \"callState\": 3 }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout2.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout2.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"HANGUP CALL\", \"callUri\": \"${channelLayout2.number}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }
        }
//        channelLayout2.findViewById<View>(R.id.btn_kaimai).setOnClickListener {
//            var msg_to_send = ""
//            if (channelLayout2.callStatus==CallStatus.待播) {
//                msg_to_send = "{ \"msgType\": \"UPDATE CALL MIC STATE\", \"callUri\": \"${channelLayout2.number}\", \"micState\": 1 }"
//                val intent = Intent(this, WebSocketService::class.java)
//                intent.putExtra("msg_to_send", msg_to_send)
//                startService(intent)
//            }
//        }


        var channelLayout3 = findViewById<ChannelLayout>(R.id.col3)
        channelLayout3.setBColor(Color.parseColor("#000000"))
        channelLayout3.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout3.callStatus==CallStatus.呼入 || channelLayout3.callStatus==CallStatus.待播) {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callUri_needHandle\": \"${channelLayout3.number}\", \"callUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout3.findViewById<View>(R.id.btn_daibo).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout3.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"UPDATE CALL STATE\", \"callUri\": \"${channelLayout3.number}\", \"callState\": 3 }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout3.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout3.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"HANGUP CALL\", \"callUri\": \"${channelLayout3.number}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
//        channelLayout3.findViewById<View>(R.id.btn_kaimai).setOnClickListener {
//            var msg_to_send = ""
//            if (channelLayout3.callStatus==CallStatus.待播) {
//                msg_to_send = "{ \"msgType\": \"UPDATE CALL MIC STATE\", \"callUri\": \"${channelLayout3.number}\", \"micState\": 1 }"
//                val intent = Intent(this, WebSocketService::class.java)
//                intent.putExtra("msg_to_send", msg_to_send)
//                startService(intent)
//            }
//        }


        var channelLayout4 = findViewById<ChannelLayout>(R.id.col4)
        channelLayout4.setBColor(Color.parseColor("#000000"))
        channelLayout4.findViewById<View>(R.id.btn_jieting).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout4.callStatus==CallStatus.呼入 || channelLayout4.callStatus==CallStatus.待播) {
                msg_to_send = "{ \"msgType\": \"ANSWER CALL\", \"callUri_needHandle\": \"${channelLayout4.number}\", \"callUri_handler\": \"${localUri}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout4.findViewById<View>(R.id.btn_daibo).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout4.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"UPDATE CALL STATE\", \"callUri\": \"${channelLayout4.number}\", \"callState\": 3 }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }

        }
        channelLayout4.findViewById<View>(R.id.btn_guaji).setOnClickListener {
            var msg_to_send = ""
            if (channelLayout4.callStatus!=CallStatus.空闲) {
                msg_to_send = "{ \"msgType\": \"HANGUP CALL\", \"callUri\": \"${channelLayout4.number}\" }"
                val intent = Intent(this, WebSocketService::class.java)
                intent.putExtra("msg_to_send", msg_to_send)
                startService(intent)
            }
        }
//        channelLayout4.findViewById<View>(R.id.btn_kaimai).setOnClickListener {
//            var msg_to_send = ""
//            if (channelLayout4.callStatus==CallStatus.待播) {
//                msg_to_send = "{ \"msgType\": \"UPDATE CALL MIC STATE\", \"callUri\": \"${channelLayout4.number}\", \"micState\": 1 }"
//                val intent = Intent(this, WebSocketService::class.java)
//                intent.putExtra("msg_to_send", msg_to_send)
//                startService(intent)
//            }
//        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if(event.keyCode==KeyEvent.KEYCODE_BACK) {
//                if(isTaskRoot()) {
//                    moveTaskToBack(false);
//                    return true
//                }
                val intent = Intent(this, SipService::class.java)
                stopService(intent)
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

    inner class WSReceiver: BroadcastReceiver() {
        var channelLayout1 = findViewById<ChannelLayout>(R.id.col1)
        var channelLayout2 = findViewById<ChannelLayout>(R.id.col2)
        var channelLayout3 = findViewById<ChannelLayout>(R.id.col3)
        var channelLayout4 = findViewById<ChannelLayout>(R.id.col4)

        fun findChannelLayoutByCallUri(callUri: String): ChannelLayout? {
            if(channelLayout1.number == callUri) {
                return channelLayout1
            }else if(channelLayout2.number == callUri) {
                return channelLayout2
            }else if(channelLayout3.number == callUri) {
                return channelLayout3
            }else if(channelLayout4.number == callUri) {
                return channelLayout4
            }else {
                return null
            }
        }

        fun setStatusJieTingByCallUri(callUri: String) {
            if(channelLayout1.number == callUri) {
                channelLayout1.callStatus = CallStatus.接听
                channelLayout2.disableAllButtons()
                channelLayout3.disableAllButtons()
                channelLayout4.disableAllButtons()
            }else if(channelLayout2.number == callUri) {
                channelLayout2.callStatus = CallStatus.接听
                channelLayout1.disableAllButtons()
                channelLayout3.disableAllButtons()
                channelLayout4.disableAllButtons()
            }else if(channelLayout3.number == callUri) {
                channelLayout3.callStatus = CallStatus.接听
                channelLayout1.disableAllButtons()
                channelLayout2.disableAllButtons()
                channelLayout4.disableAllButtons()
            }else if(channelLayout4.number == callUri) {
                channelLayout4.callStatus = CallStatus.接听
                channelLayout1.disableAllButtons()
                channelLayout2.disableAllButtons()
                channelLayout3.disableAllButtons()
            }
        }

        fun setStatusDaiBoByCallUri(callUri: String, micState: Int) {
            if(channelLayout1.number == callUri) {
                if (micState == 0) channelLayout1.callStatus = CallStatus.待播
                else channelLayout1.callStatus = CallStatus.播出
                channelLayout2.enableAllButtons()
                channelLayout3.enableAllButtons()
                channelLayout4.enableAllButtons()
            }else if(channelLayout2.number == callUri) {
                if (micState == 0) channelLayout2.callStatus = CallStatus.待播
                else channelLayout2.callStatus = CallStatus.播出
                channelLayout1.enableAllButtons()
                channelLayout3.enableAllButtons()
                channelLayout4.enableAllButtons()
            }else if(channelLayout3.number == callUri) {
                if (micState == 0) channelLayout3.callStatus = CallStatus.待播
                else channelLayout3.callStatus = CallStatus.播出
                channelLayout1.enableAllButtons()
                channelLayout2.enableAllButtons()
                channelLayout4.enableAllButtons()
            }else if(channelLayout4.number == callUri) {
                if (micState == 0) channelLayout4.callStatus = CallStatus.待播
                else channelLayout4.callStatus = CallStatus.播出
                channelLayout1.enableAllButtons()
                channelLayout2.enableAllButtons()
                channelLayout3.enableAllButtons()
            }
        }

        fun setStatusHuruByIndex(index: Int, callUri: String) {
            var isInJieTing = false
            if(channelLayout1.callStatus==CallStatus.接听 || channelLayout2.callStatus==CallStatus.接听 || channelLayout3.callStatus==CallStatus.接听 || channelLayout4.callStatus==CallStatus.接听) {
                isInJieTing = true
            }
            when(index) {
                1 -> {
                    channelLayout1.number = callUri
                    channelLayout1.callStatus = (CallStatus.呼入);
                    channelLayout1.setBColor(Color.parseColor("#FFFFFF"))
                    if(!isInJieTing) channelLayout1.enableAllButtons()
                }
                2 -> {
                    channelLayout2.number = callUri
                    channelLayout2.callStatus = (CallStatus.呼入);
                    channelLayout2.setBColor(Color.parseColor("#FFFFFF"))
                    if(!isInJieTing) channelLayout2.enableAllButtons()
                }
                3 -> {
                    channelLayout3.number = callUri
                    channelLayout3.callStatus = (CallStatus.呼入);
                    channelLayout3.setBColor(Color.parseColor("#FFFFFF"))
                    if(!isInJieTing) channelLayout3.enableAllButtons()
                }
                4 -> {
                    channelLayout4.number = callUri
                    channelLayout4.callStatus = (CallStatus.呼入);
                    channelLayout4.setBColor(Color.parseColor("#FFFFFF"))
                    if(!isInJieTing) channelLayout4.enableAllButtons()
                }
            }
        }

        fun setStatusHuruByCallUri(callUri: String) {
            var isInJieTing = false
            if(channelLayout1.callStatus==CallStatus.接听 || channelLayout2.callStatus==CallStatus.接听 || channelLayout3.callStatus==CallStatus.接听 || channelLayout4.callStatus==CallStatus.接听) {
                isInJieTing = true
            }
            if(channelLayout1.number == callUri) {
                channelLayout1.number = callUri
                channelLayout1.callStatus = (CallStatus.呼入);
                channelLayout1.setBColor(Color.parseColor("#FFFFFF"))
                if(!isInJieTing) channelLayout1.enableAllButtons()
            }else if(channelLayout2.number == callUri) {
                channelLayout2.number = callUri
                channelLayout2.callStatus = (CallStatus.呼入);
                channelLayout2.setBColor(Color.parseColor("#FFFFFF"))
                if(!isInJieTing) channelLayout2.enableAllButtons()
            }else if(channelLayout3.number == callUri) {
                channelLayout3.number = callUri
                channelLayout3.callStatus = (CallStatus.呼入);
                channelLayout3.setBColor(Color.parseColor("#FFFFFF"))
                if(!isInJieTing) channelLayout3.enableAllButtons()
            }else if(channelLayout4.number == callUri) {
                channelLayout4.number = callUri
                channelLayout4.callStatus = (CallStatus.呼入);
                channelLayout4.setBColor(Color.parseColor("#FFFFFF"))
                if(!isInJieTing) channelLayout4.enableAllButtons()
            }

        }

        fun setStatusGuaDuanByCallUri(callUri: String) {
            if(channelLayout1.number == callUri) {
                channelLayout1.reset()
                channelLayout2.enableAllButtons()
                channelLayout3.enableAllButtons()
                channelLayout4.enableAllButtons()
            }else if(channelLayout2.number == callUri) {
                channelLayout2.reset()
                channelLayout1.enableAllButtons()
                channelLayout3.enableAllButtons()
                channelLayout4.enableAllButtons()
            }else if(channelLayout3.number == callUri) {
                channelLayout3.reset()
                channelLayout1.enableAllButtons()
                channelLayout2.enableAllButtons()
                channelLayout4.enableAllButtons()
            }else if(channelLayout4.number == callUri) {
                channelLayout4.reset()
                channelLayout1.enableAllButtons()
                channelLayout2.enableAllButtons()
                channelLayout3.enableAllButtons()
            }
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val x = intent.extras?.get("msg");
//                Log.d("HANDLE MESSAGE PRE", x.toString())
                if(intent.extras?.get("msg") == "connected") {
                    if(!online) {
                        online = true
                        Log.d("HANDLE MESSAGE", "connected...............")
                    }
                }else if(intent.extras?.get("msg") == "disconnected") {
                    if(online) {
                        online = false
                        Log.d("HANDLE MESSAGE", "disconnected...............")
                        channelLayout1.reset()
                        channelLayout2.reset()
                        channelLayout3.reset()
                        channelLayout4.reset()
                    }
                }else if(intent.extras?.get("msg") == "callinfo") {
                    Log.d("ID", intent.extras?.get("callId").toString())
                    Log.d("LOCAL URI", intent.extras?.get("localUri").toString())
                    callId = intent.extras?.get("callId").toString()
                    localUri = intent.extras?.get("localUri").toString()
                }else {
                    val msg_json = JSONObject(intent.extras?.get("msg").toString())
                    val callUri = msg_json.get("callUri").toString()
                    when(msg_json.get("callState").toString().toInt()) {
                        1 -> {
                            Log.d("HANDLE MESSAGE", "新接入电话，待导播接听...............")
                            if(channelLayout1.callStatus==CallStatus.空闲) {
                                setStatusHuruByIndex(1, callUri)
                            }else if(channelLayout2.callStatus==CallStatus.空闲) {
                                setStatusHuruByIndex(2, callUri)
                            }else if(channelLayout3.callStatus==CallStatus.空闲) {
                                setStatusHuruByIndex(3, callUri)
                            }else if(channelLayout4.callStatus==CallStatus.空闲) {
                                setStatusHuruByIndex(4, callUri)
                            }else {
                                Log.d("HANDLE MESSAGE", "当前4路已满！...............")
                            }
                        }
                        2 -> {
                            Log.d("HANDLE MESSAGE", "导播已接入...............")
                            setStatusJieTingByCallUri(callUri)
                        }
                        3 -> {
                            Log.d("HANDLE MESSAGE", "主持人已接入，待播...............")
                            val micState = msg_json.get("micState").toString().toInt()
                            setStatusDaiBoByCallUri(callUri, micState)
                        }
                        4 -> {
                            Log.d("HANDLE MESSAGE", "等待导播接入，不过代表这不是第一次接入...............")
                            setStatusHuruByCallUri(callUri)
                        }
                        0 -> {
                            Log.d("HANDLE MESSAGE", "已挂机，须从界面删除...............")
                            setStatusGuaDuanByCallUri(callUri)
                        }
                    }
                }
            }
        }
    }
}