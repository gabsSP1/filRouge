package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

public class Connect_activity extends Activity {
    TextView ipLocale;
    TextView ipGlobale;
    TextView statusCo;
    Server s;
    Button play;
    String pictureName;
    static Socket socket;
    final static int PORT = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_activity);

        ipGlobale = (TextView) findViewById(R.id.ip_glob);
        ipLocale = (TextView) findViewById(R.id.ip_loc);
        statusCo =(TextView) findViewById(R.id.co);

        s = new Server(PORT);
        s.start();
        ipLocale.setText(getIPLoc());
        ipGlobale.setText(getIPGlob());
        LaunchClient l =new LaunchClient();
        l.execute();
        Intent intent = getIntent();
        if (intent != null) {

            pictureName = intent.getStringExtra("selected_file");

        }
        play =(Button) findViewById(R.id.bstart);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.lauch(socket);
                Intent intentMyAccount = new Intent(getApplicationContext(), Game.class);
                intentMyAccount.putExtra("selected_file", pictureName);
                startActivity(intentMyAccount);
            }
        });

    }

    public class LaunchClient extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            socket = Client.connect("localhost", PORT, getApplicationContext());
            Map map = new Map(pictureName);
            MainGamePanel.map =map;
            BazarStatic.host =true;
            Client.sendMap(map, socket);
            try {
                afficherAdresse();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private String getIPLoc()
    {
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
        return null;
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
}
