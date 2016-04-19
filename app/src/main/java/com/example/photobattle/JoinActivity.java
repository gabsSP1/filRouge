package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.Socket;

public class JoinActivity extends Activity {
    EditText editText;
    Button bjoin;
    Socket client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
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
            client = Client.connect(host, Connect_activity.PORT);
            while(MainGamePanel.map == null);
            if( client != null)
            {
                Intent intentMyAccount = new Intent(getApplicationContext(), Game.class);
                startActivity(intentMyAccount);
            }
            return null;
        }
    }

}
