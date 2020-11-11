package cn.fitcan.sip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP)
                == PackageManager.PERMISSION_GRANTED){
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_SIP}, 0);
        }

        Boolean b1 = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SIP);
        Boolean b2 = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SIP_VOIP);

        System.out.println();

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
            acfg.setIdUri("sip:test@pjsip.org");
            acfg.getRegConfig().setRegistrarUri("sip:pjsip.org");
            AuthCredInfo cred = new AuthCredInfo("digest", "*", "test", 0, "secret");
            acfg.getSipConfig().getAuthCreds().add( cred );
            // Create the account
            MyAccount acc = new MyAccount();
            acc.create(acfg);
            // Here we don't have anything else to do..
            Thread.sleep(10000);
            /* Explicitly delete the account.
             * This is to avoid GC to delete the endpoint first before deleting
             * the account.
             */
            acc.delete();

            // Explicitly destroy and delete endpoint
            ep.libDestroy();
            ep.delete();

        } catch (Exception e) {
            System.out.println(e);
            return;
        }
    }
}