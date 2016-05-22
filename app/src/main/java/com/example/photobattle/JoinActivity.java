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
import android.widget.TextView;

import org.andengine.entity.text.Text;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class JoinActivity extends BaseActivity {
    EditText editText;
    Button bjoin;
    TextView etatCo;
    static boolean  launch;
    Socket client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreencall();
        setContentView(R.layout.activity_join);
        launch=false;
        etatCo = (TextView) findViewById(R.id.etatCo);
        editText = (EditText) findViewById(R.id.ipfield);


        ( (Button) findViewById(R.id.back_join)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinActivity.this.finish();
            }
        });

        bjoin = (Button) findViewById(R.id.bjoin);
        String s="";
        bjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.quit();
               LaunchClient l = new LaunchClient(editText.getText().toString());
                l.execute();
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        etatCo.setText("");
        Client.quit();
    }

    public void onDestroy()
    {
        super.onDestroy();
        Client.quit();
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
            BazarStatic.onLine =true;
            Connect_activity.socket = Client.connect(host, getApplicationContext(),null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(Connect_activity.socket == null)
                    {
                        etatCo.setText("Connection failed, retry");
                        Client.quit();
                    }
                    else
                    {
                        etatCo.setText("Connection success !");
                    }
                }
            });

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

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }
}


