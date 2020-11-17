package cn.fitcan.sip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.sip.SipManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.pjsip.pjsua2.*;
import org.w3c.dom.Text;

import java.util.logging.Logger;

import static org.pjsip.pjsua2.pjmedia_type.PJMEDIA_TYPE_AUDIO;

// Subclass to extend the Account and get notifications etc.
class MyAccount extends Account {
    @Override
    public void onRegState(OnRegStateParam prm) {
        System.out.println("*** On registration state: " + prm.getCode() + prm.getReason());
    }
}

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK, Manifest.permission.VIBRATE, Manifest.permission.USE_SIP, Manifest.permission.CAMERA};

    private Endpoint ep;
    private MyAccount acc;

    class MyCall extends Call {
        public MyCall(MyAccount myAccount, int id) {
            super(myAccount, id);
        }

        @Override
        public void onCallState(OnCallStateParam prm) {
            super.onCallState(prm);
            try {
                CallInfo callInfo = getInfo();

                int role = callInfo.getRole();
                if(role == pjsip_role_e.PJSIP_ROLE_UAC) {
                    System.out.println("==============呼出：状态变更====================");
                }else if(role == pjsip_role_e.PJSIP_ROLE_UAS) {
                    System.out.println("==============呼入：状态变更====================");
                }

                int state = callInfo.getState();
                if (state == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
                    System.out.println("==============正在呼出====================");
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    System.out.println("==============对方正在响铃====================");
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                    System.out.println("==============连接成功====================");
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    System.out.println("==============通话中====================");
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                    System.out.println("==============挂断====================");
                }

            }catch (Exception e) {
                System.out.println(e);
                return;
            }
        }

        @Override
        public void onCallMediaState(OnCallMediaStateParam prm) {
            System.out.println("..............................on call media state.............................");


            CallInfo info;
            try {
                info = getInfo();
            } catch (Exception exc) {
                System.out.println("onCallMediaState: error while getting call info");
                return;
            }

            for (int i = 0; i < info.getMedia().size(); i++) {
                Media media = getMedia(i);
                CallMediaInfo mediaInfo = info.getMedia().get(i);

                if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                        && media != null
                        && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {

                    handleAudioMedia(media);

                } else if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO
                        && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE
                        && mediaInfo.getVideoIncomingWindowId() != pjsua2.INVALID_ID) {

                    handleVideoMedia(mediaInfo);
                }
            }

        }
    }
    private MyCall myCall;

    private void handleAudioMedia(Media media) {
        AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

        // connect the call audio media to sound device
        try {
            AudDevManager audDevManager = ep.audDevManager();
            if (audioMedia != null) {
                try {
                    audioMedia.adjustRxLevel((float) 1.5);
                    audioMedia.adjustTxLevel((float) 1.5);
                } catch (Exception exc) {
                    System.out.println("Error while adjusting levels");
                }

                audioMedia.startTransmit(audDevManager.getPlaybackDevMedia());
                audDevManager.getCaptureDevMedia().startTransmit(audioMedia);
            }
        } catch (Exception exc) {
            System.out.println("Error while connecting audio media to sound device");
        }
    }

    private void handleVideoMedia(CallMediaInfo mediaInfo) {
        System.out.println("================Handle VIDEO MEDIA============");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
        try {
            // Create endpoint
            ep = new Endpoint();
            ep.libCreate();
            // Initialize endpoint
            EpConfig epConfig = new EpConfig();
            ep.libInit( epConfig );
            // Create SIP transport. Error handling sample is shown
            TransportConfig sipTpConfig = new TransportConfig();
            sipTpConfig.setPort(5080);
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
            // Start the library
            ep.libStart();

            AccountConfig acfg = new AccountConfig();
            acfg.setIdUri("sip:9527@192.168.1.32");
            acfg.getRegConfig().setRegistrarUri("sip:192.168.1.32");
            AuthCredInfo cred = new AuthCredInfo("digest", "*", "9527", 0, "9527");
            acfg.getSipConfig().getAuthCreds().add( cred );
            // Create the account
            acc = new MyAccount();
            acc.create(acfg);
            myCall = new MyCall(acc, pjsua_invalid_id_const_.PJSUA_INVALID_ID);
            CallOpParam prm = new CallOpParam();
            CallSetting callSetting = prm.getOpt();
            callSetting.setAudioCount(1);
            callSetting.setVideoCount(0);
            callSetting.setFlag(pjsua_call_flag.PJSUA_CALL_INCLUDE_DISABLED_MEDIA);
            myCall.makeCall("sip:8101@192.168.1.32", prm);
            System.out.println("call call call call");
        }catch (Exception e) {
            System.out.println(e);
            return;
        }
    }

    static {
        System.loadLibrary("pjsua2");
        System.out.println("Library loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        ChannelLayout channelLayout1 = findViewById(R.id.col1);
        channelLayout1.setChannelID("No.01");
        channelLayout1.setNumber("9527");
        channelLayout1.setBColor(Color.parseColor("#DDDDDD"));
        channelLayout1.findViewById(R.id.btn_jieting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"); //SIP拨号阻塞？
            }
        });

        ChannelLayout channelLayout2 = findViewById(R.id.col2);
        channelLayout2.setChannelID("No.02");
        channelLayout2.setNumber("9528");
        channelLayout2.setBColor(Color.parseColor("#FFDDDD"));

        ChannelLayout channelLayout3 = findViewById(R.id.col3);
        channelLayout3.setChannelID("No.03");
        channelLayout3.setNumber("9529");
        channelLayout3.setBColor(Color.parseColor("#DDDDDD"));

        ChannelLayout channelLayout4 = findViewById(R.id.col4);
        channelLayout4.setChannelID("No.04");
        channelLayout4.setNumber("9530");
        channelLayout4.setBColor(Color.parseColor("#FFDDDD"));
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroy Destroy Destroy Destroy Destroy Destroy");
        super.onDestroy();
        try {
            myCall.delete();
            acc.delete();
            // Explicitly destroy and delete endpoint
            ep.libDestroy();
            ep.delete();
        }catch (Exception e) {
            System.out.println(e);
            return;
        }

        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}