package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class JoinActivity extends BaseActivity {
    EditText editText;
    Button bjoin;
    static boolean  launch;
    Socket client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreencall();
        setContentView(R.layout.activity_join);
        launch=false;
        editText = (EditText) findViewById(R.id.ipfield);

        bjoin = (Button) findViewById(R.id.bjoin);
        String s="";
        bjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               LaunchClient l = new LaunchClient(editText.getText().toString());
                l.execute();
            }
        });
    }

    public class LaunchClient extends AsyncTask<Void, Void, Void> {
        String host;

        public LaunchClient (String host)
        {
            this.host=host;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                afficherAdresse();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            BazarStatic.host =false;
            Connect_activity.socket = Client.connect(host, getApplicationContext());
            return null;
        }
    }

    public void afficherAdresse() throws SocketException {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            Enumeration<InetAddress> i = e.nextElement().getInetAddresses();
            while (i.hasMoreElements()) {
                InetAddress a = i.nextElement();
                System.out.println(a.getHostName() + " -> " + a.getHostAddress() +
                        "\n\t isloopback? " + a.isLoopbackAddress() +
                        "\n\t isSiteLocalAddress? " + a.isSiteLocalAddress() +
                        "\n\t isIPV6? " + (a instanceof Inet6Address)
                );
            }
        }
    }
    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}


