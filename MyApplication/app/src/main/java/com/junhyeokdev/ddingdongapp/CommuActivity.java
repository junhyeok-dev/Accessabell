package com.junhyeokdev.ddingdongapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CommuActivity extends AppCompatActivity {
    private Boolean isSocketConnected = false;

    private EditText txtTts;
    public EditText txtStt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu);

        txtTts = findViewById(R.id.tts);
        txtStt = findViewById(R.id.stt);

        final SocketMgr socketMgr = new SocketMgr("192.168.1.112", 2723, this);
        socketMgr.start();

        Button imageButton = (Button) findViewById(R.id.buttoncommu);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button btnTts = findViewById(R.id.tts_button);
        btnTts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSocketConnected) {
                    socketMgr.sendData(txtTts.getText().toString());
                    socketMgr.readData();
                } else {
                    Log.d("SOCKET", "Socket is not connected.");
                }
            }
        });
    }

    public void setIsSocketConnected(Boolean val) {
        isSocketConnected = val;
    }
}