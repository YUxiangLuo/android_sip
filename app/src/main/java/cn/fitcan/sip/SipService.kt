package cn.fitcan.sip

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat
import org.pjsip.pjsua2.*
import kotlin.concurrent.thread

// Subclass to extend the Account and get notifications etc.
internal class MyAccount : Account() {
    override fun onRegState(prm: OnRegStateParam) {
        println("*** On registration state: " + prm.code + prm.reason)
    }
}

class SipService : Service() {
    private var ep: Endpoint? = null
    private var acc: MyAccount? = null

    internal inner class MyCall(myAccount: MyAccount?, id: Int) : Call(myAccount, id) {
        override fun onCallState(prm: OnCallStateParam) {
            super.onCallState(prm)
            try {
                val callInfo = info
                val role = callInfo.role
                if (role == pjsip_role_e.PJSIP_ROLE_UAC) {
                    println("==============呼出：状态变更====================")
                } else if (role == pjsip_role_e.PJSIP_ROLE_UAS) {
                    println("==============呼入：状态变更====================")
                }
                val state = callInfo.state
                if (state == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
//                    Log.d("ID", callInfo.callIdString)
//                    Log.d("LOCAL URI", callInfo.localUri)
                    val intent = Intent("cn.fitcan.action.HANDLE_WS_MESSAGE")
                    intent.putExtra("msg", "callinfo")
                    intent.putExtra("callId", callInfo.callIdString)
                    intent.putExtra("localUri", callInfo.localUri)
                    intent.setPackage(packageName)
                    sendBroadcast(intent);
                    println("==============正在呼出====================")
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    println("==============对方正在响铃====================")
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                    println("==============连接成功====================")
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    println("==============通话中====================")
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                    println("==============挂断====================")
                }
            } catch (e: Exception) {
                println(e)
                return
            }
        }

        override fun onCallMediaState(prm: OnCallMediaStateParam) {
            println("..............................on call media state.............................")
            val info: CallInfo
            info = try {
                getInfo()
            } catch (exc: Exception) {
                println("onCallMediaState: error while getting call info")
                return
            }
            for (i in info.media.indices) {
                val media = getMedia(i.toLong())
                val mediaInfo = info.media[i]
                if (mediaInfo.type == pjmedia_type.PJMEDIA_TYPE_AUDIO && media != null && mediaInfo.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                    handleAudioMedia(media)
                } else if (mediaInfo.type == pjmedia_type.PJMEDIA_TYPE_VIDEO && mediaInfo.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE && mediaInfo.videoIncomingWindowId != pjsua2.INVALID_ID) {
                    handleVideoMedia(mediaInfo)
                }
            }
        }
    }

    private var myCall: MyCall? = null
    private fun handleAudioMedia(media: Media) {
        val audioMedia = AudioMedia.typecastFromMedia(media)

        // connect the call audio media to sound device
        try {
            val audDevManager = ep!!.audDevManager()
            if (audioMedia != null) {
                try {
                    audioMedia.adjustRxLevel(1.5.toFloat())
                    audioMedia.adjustTxLevel(1.5.toFloat())
                } catch (exc: Exception) {
                    println("Error while adjusting levels")
                }
                audioMedia.startTransmit(audDevManager.playbackDevMedia)
                audDevManager.captureDevMedia.startTransmit(audioMedia)
            }
        } catch (exc: Exception) {
            println("Error while connecting audio media to sound device")
        }
    }

    private fun handleVideoMedia(mediaInfo: CallMediaInfo) {
        println("================Handle VIDEO MEDIA============")
    }

    companion object {
        init {
            System.loadLibrary("pjsua2")
            println("Library loaded")
        }
    }

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
            try {
                // Create endpoint
                ep = Endpoint()
                ep!!.libCreate()
                // Initialize endpoint
                val epConfig = EpConfig()
                ep!!.libInit(epConfig)
                // Create SIP transport. Error handling sample is shown
                val sipTpConfig = TransportConfig()
                sipTpConfig.port = 5080
                ep!!.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig)
                // Start the library
                ep!!.libStart()
                val acfg = AccountConfig()
                acfg.idUri = "sip:9527@192.168.1.32"
                acfg.regConfig.registrarUri = "sip:192.168.1.32"
                val cred = AuthCredInfo("digest", "*", "9527", 0, "9527")
                acfg.sipConfig.authCreds.add(cred)
                // Create the account
                acc = MyAccount()
                acc!!.create(acfg)
                myCall = MyCall(acc, pjsua_invalid_id_const_.PJSUA_INVALID_ID)
                val prm = CallOpParam()
                val callSetting = prm.opt
                callSetting.audioCount = 1
                callSetting.videoCount = 0
                callSetting.flag = pjsua_call_flag.PJSUA_CALL_INCLUDE_DISABLED_MEDIA.toLong()
                myCall!!.makeCall("sip:8101@192.168.1.32", prm)
                println("call call call call")
            } catch (e: Exception) {
                println(e)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Sip Service", "onDestroy executed")
        thread {
            try {
                ep?.libRegisterThread(Thread.currentThread().name)
                myCall?.delete()
                acc?.delete()
                // Explicitly destroy and delete endpoint
                ep?.libDestroy()
                ep?.delete()
            } catch (e: Exception) {
                e.printStackTrace()
//            return
            }
            val pid = Process.myPid()
            Process.killProcess(pid)
        }

    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}