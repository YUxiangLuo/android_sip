package cn.fitcan.sip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.sip.SipManager;
import android.os.Bundle;

import org.pjsip.pjsua2.*;
// Subclass to extend the Account and get notifications etc.
class MyAccount extends Account {
    @Override
    public void onRegState(OnRegStateParam prm) {
        System.out.println("*** On registration state: " + prm.getCode() + prm.getReason());
    }
}

class MyCall extends Call {
    public MyCall(MyAccount myAccount, int id) {
        super(myAccount, id);
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        super.onCallState(prm);
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
    }
}


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private Endpoint ep;
    private MyAccount acc;
    private MyCall myCall;


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
            CallOpParam prm = new CallOpParam(true);
            myCall.makeCall("sip:4002@192.168.1.32", prm);
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
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroy Destroy Destroy Destroy Destroy Destroy");
        super.onDestroy();
        try {
            acc.delete();
            // Explicitly destroy and delete endpoint
            ep.libDestroy();
            ep.delete();
        }catch (Exception e) {
            System.out.println(e);
            return;
        }
    }
}