package cn.fitcan.sip

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlin.concurrent.thread
import okhttp3.*;
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebSocketService : Service() {
//    private val ws_url: String = "ws://192.168.1.32:3000";
    private val ws_url: String = "ws://192.168.1.31:1333";
    private var webSocket: WebSocket? = null;
    private val client: OkHttpClient = OkHttpClient().newBuilder().pingInterval(1, TimeUnit.SECONDS).build();
    private val request: Request = Request.Builder().url(ws_url).build();

    private var online: Boolean = false

    internal inner class WSListener : WebSocketListener() {

        fun reconnect() {
//            Log.d("WebSocketService", "reconnect reconnect..........................")
            webSocket = client.newWebSocket(request, WSListener())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("WebSocketService", "WebSocketService on closed!!!!!!!!!!!!!!")
            online = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
//            Log.d("WebSocketService", "WebSocketService on failure!!!!!!!!!!!!!!")
            online = false
            client.dispatcher.cancelAll();
            client.connectionPool.evictAll();
            val intent = Intent("cn.fitcan.action.HANDLE_WS_MESSAGE")
            intent.putExtra("msg", "disconnected")
            intent.setPackage(packageName)
            sendBroadcast(intent);
            reconnect()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d("WebSocketService", text)
            handleMessage(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            Log.d("WebSocketService", "byte string..................")
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
//            Log.d("WebSocketService", "WebSocketService on open!!!!!!!!!!!!!!")
            online = true
            webSocket.send("{ \"msgType\": \"REQ CALL LIST\" }")
//            webSocket.send("{ \"msgType\": \"ANSWER CALL\", \"callId_needAnswer\": \"0\", \"callLocalUri_needAnswer\": \"\"8101\" <sip:8101@192.168.1.32>\", \"callId_handler\": \"${callId}\", \"callLocalUri_handler\": \"${localUri}\" }")
            val intent = Intent("cn.fitcan.action.HANDLE_WS_MESSAGE")
            intent.putExtra("msg", "connected")
            intent.setPackage(packageName)
            sendBroadcast(intent);
        }
    }

    fun handleMessage(msg: String) {
        val intent = Intent("cn.fitcan.action.HANDLE_WS_MESSAGE")
        intent.putExtra("msg", msg)
        intent.setPackage(packageName)
        sendBroadcast(intent);
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("WebSocketService", "WebSocketService creted!!!!!!!!!!!!!!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("WebSocetService", "websocket service started!!!")
        val msg_to_send = intent?.extras?.get("msg_to_send").toString()

        Log.d("AAAAAAA", msg_to_send);

        if(msg_to_send=="no") {
            Log.d("AAAAAAA", "gogogogogogo");
            thread {
                webSocket = client.newWebSocket(request, WSListener())
            }
        }else {
            webSocket?.send(msg_to_send)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}