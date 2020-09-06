package com.junhyeokdev.ddingdongapp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketReader extends Thread {
    Socket socket;
    CommuActivity parentActivity;

    public SocketReader(Socket sock, CommuActivity parent) {
        socket = sock;
        parentActivity = parent;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            final byte[] data = new byte[1024];
            input.read(data);

            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parentActivity.txtStt.setText(new String(data));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SOCKET_ERR", "Receive data failure.");
        }
    }
}
