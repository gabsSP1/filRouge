package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.Socket;

public class JoinActivity extends BaseActivity {
    EditText editText;
    Button bjoin;
    static boolean  launch;
    Socket client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            BazarStatic.host =false;
            Connect_activity.socket = Client.connect(host, Connect_activity.PORT, getApplicationContext());
            return null;
        }
    }


}
