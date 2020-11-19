package cn.fitcan.sip

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlin.concurrent.thread
import okhttp3.*;
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketService : Service() {
    private val ws_url: String = "ws://192.168.1.32:3000";
    private var webSocket: WebSocket? = null;
    private val client: OkHttpClient = OkHttpClient().newBuilder().pingInterval(1, TimeUnit.SECONDS).build();
    private val request: Request = Request.Builder().url(ws_url).build();

    private var online: Boolean = false

    internal inner class WSListener : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("WebSocketService", "WebSocketService on closed!!!!!!!!!!!!!!")
            online = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.d("WebSocketService", "WebSocketService on failure!!!!!!!!!!!!!!")
            online = false
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d("WebSocketService", text)
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d("WebSocketService", "WebSocketService on open!!!!!!!!!!!!!!")
            online = true
            webSocket.send("Hello from android~")
            val intent = Intent("cn.fitcan.action.HANDLE_WS_MESSAGE")
            intent.putExtra("type", "connect");
            intent.putExtra("data", "open");
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("WebSocketService", "WebSocketService creted!!!!!!!!!!!!!!1")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("WebSocetService", "websocket service started!!!")
        thread {
            webSocket = client.newWebSocket(request, WSListener())
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