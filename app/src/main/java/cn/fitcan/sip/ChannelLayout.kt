package cn.fitcan.sip

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView

class ChannelLayout : LinearLayout {
    var callID = "#"
        set(callID) {
            field = callID
            val channel_id = findViewById<TextView>(R.id.channel_id)
            channel_id.text = callID
        }
    var number = "0000"
        set(new_number) {
            field = new_number
            val channel_number = findViewById<TextView>(R.id.channel_number)
            channel_number.text = new_number
        }
    var callStatus = CallStatus.空闲
        set(callStatus) {
            field = callStatus
            val channel_status = findViewById<TextView>(R.id.channel_status)
            if (callStatus == CallStatus.空闲) {
                channel_status.setBackgroundColor(Color.GREEN)
                channel_status.setTextColor(Color.BLACK)
                channel_status.text = "空闲"
            } else if (callStatus == CallStatus.呼入) {
                channel_status.setBackgroundColor(Color.BLUE)
                channel_status.setTextColor(Color.WHITE)
                channel_status.text = "呼入"
            } else if (callStatus == CallStatus.待播) {
                channel_status.setBackgroundColor(Color.BLUE)
                channel_status.setTextColor(Color.WHITE)
                channel_status.text = "待播"
            } else if (callStatus == CallStatus.接听) {
                channel_status.setBackgroundColor(Color.YELLOW)
                channel_status.setTextColor(Color.BLACK)
                channel_status.text = "接听"
            } else if (callStatus == CallStatus.播出) {
                channel_status.setBackgroundColor(Color.GREEN)
                channel_status.setTextColor(Color.RED)
                channel_status.text = "播出"
            } else if (callStatus == CallStatus.挂断) {
                channel_status.setBackgroundColor(Color.RED)
                channel_status.setTextColor(Color.BLACK)
                channel_status.text = "挂断"
            }
        }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.channel_column, this)
    }

    constructor(context: Context?) : super(context) {}

    fun reset() {
        callStatus = CallStatus.空闲
        callID = "#"
        number = "0000"
        setBColor(Color.parseColor("#000000"))
    }

    fun setBColor(color: Int) {
        val linearLayout = findViewById<LinearLayout>(R.id.channel_layout)
        linearLayout.setBackgroundColor(color)
    }
}