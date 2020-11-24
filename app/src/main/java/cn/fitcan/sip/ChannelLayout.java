package cn.fitcan.sip;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

public class ChannelLayout extends LinearLayout {

    private String callID;

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String number = "0000";
    private String name;

    public ChannelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.channel_column, this);
    }

    public ChannelLayout(Context context) {
        super(context);
    }

    public void reset() {
        this.name = "0000";
        this.setChannelID("0000");
        this.setNumber("0000");
        this.setBColor(Color.parseColor("#000000"));
    }

    public void setChannelID(String new_id) {
        this.callID = new_id;
        TextView channel_id = this.findViewById(R.id.channel_id);
        channel_id.setText(new_id);
    }

    public void setNumber(String new_number) {
        this.number = new_number;
        TextView channel_number = this.findViewById(R.id.channel_number);
        channel_number.setText(new_number);
    }

    public void setBColor(int color) {
        LinearLayout linearLayout = this.findViewById(R.id.channel_layout);
        linearLayout.setBackgroundColor(color);
    }
}
