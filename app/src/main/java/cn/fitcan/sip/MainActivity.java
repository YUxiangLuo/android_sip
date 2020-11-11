package cn.fitcan.sip;

import androidx.appcompat.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("pjsua2");
        System.out.println("Library loaded");
    }

    public SipManager sipManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Create endpoint
            Endpoint ep = new Endpoint();
            ep.libCreate();
            // Initialize endpoint
            EpConfig epConfig = new EpConfig();
            ep.libInit( epConfig );
            // Create SIP transport. Error handling sample is shown
            TransportConfig sipTpConfig = new TransportConfig();
            sipTpConfig.setPort(5060);
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
            // Start the library
            ep.libStart();

            AccountConfig acfg = new AccountConfig();
            acfg.setIdUri("sip:9527@192.168.42.90");
            acfg.getRegConfig().setRegistrarUri("sip:192.168.42.90");
            AuthCredInfo cred = new AuthCredInfo("digest", "*", "9527", 0, "9527");
            acfg.getSipConfig().getAuthCreds().add( cred );
            // Create the account
            MyAccount acc = new MyAccount();
            acc.create(acfg);

        } catch (Exception e) {
            System.out.println(e);
            return;
        }
    }
}