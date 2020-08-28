package com.example.myapplication;

import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketMgr extends Thread {
    String ipaddr;
    int portnum;
    CommuActivity motherActivity;

    Socket socket;

    public SocketMgr(String ip, int num, CommuActivity activity) {
        ipaddr = ip;
        portnum = num;
        motherActivity = activity;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ipaddr, portnum);
            motherActivity.setIsSocketConnected(true);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SOCKET_ERR", "Connection failed.");
        }
    }

    public void sendData(String textdata) {
        byte[] data = textdata.getBytes();

        try {
            OutputStream output = socket.getOutputStream();
            output.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SOCKET_ERR", "Send data failure.");
        }
    }

    public void readData() {
        SocketReader socketReader = new SocketReader(socket, motherActivity);
        socketReader.start();
    }
}
