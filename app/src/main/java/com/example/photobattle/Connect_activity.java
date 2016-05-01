package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Connect_activity extends BaseActivity {
    TextView ipLocale;
    TextView ipGlobale;
    TextView statusCo;
    static TextView logConnexion;
    Server s;
    static Button play;
    String pictureName;
    static Socket socket;
    final static int PORT = 3297;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_activity);
        FullScreencall();
        ipGlobale = (TextView) findViewById(R.id.ip_glob);
        ipLocale = (TextView) findViewById(R.id.ip_loc);
        statusCo =(TextView) findViewById(R.id.co);
        logConnexion =(TextView) findViewById(R.id.log);
        ipLocale.setText("Mon IP Locale : " + getIPLoc());
        ipGlobale.setText(getIPGlob());
        /*LaunchClient l =new LaunchClient(this);
        l.execute();*/
        play =(Button) findViewById(R.id.bstart);
        play.setText("En attente d'une connection...");
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.launch(socket);
                BazarStatic.onLine = true;
                Intent intentMyAccount = new Intent(getApplicationContext(), Game.class);
                startActivity(intentMyAccount);
            }
        });
        play.setClickable(false);
    }

    public class LaunchClient extends AsyncTask<Void, Void, Void> {


        Activity activity;
        LaunchClient(Activity activity)
        {
            this.activity = activity;
        }
        protected Void doInBackground(Void... params) {
            BazarStatic.map =new Map(BazarStatic.nomMap);
            s = new Server(PORT,activity);
            s.start();
            socket = Client.connect("localhost", getApplicationContext(), Connect_activity.this);
            System.out.println("pictureaName");
            BazarStatic.host =true;
            return null;
        }

    }


    private String getIPLoc()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        /*
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            // Log.e(Constants.LOG_TAG, e.getMessage(), e);
        }
        return null;*/
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

    private String getIPGlob()
    {
        return "d";
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

    public static void connexionSucessful()
    {
        play.setText("Connecté...");
    }

    public static void addToLog(String s)
    {
        logConnexion.setText(logConnexion.getText()+"\n" + s);
    }

    public static void permitLaunch()
    {
        play.setClickable(true);
        play.setText("Lancer le jeu");
    }

    @Override
    public void onResume() {
        super.onResume();
        logConnexion.setText("");
        play.setText("En attente d'une connection...");
        LaunchClient l =new LaunchClient(this);
        l.execute();
    }
}
