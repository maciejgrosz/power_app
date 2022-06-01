package com.example.wpam_power_app;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReadThread extends Thread{
    BluetoothSocket socket;
    public volatile boolean isShutingDown;
    public volatile String test;
    ArrayList<String> data = new ArrayList<String>( );
    public ReadThread(BluetoothSocket socket){
        this.socket = socket;
    }
    public void run(){
        while(!isShutingDown){
            if (socket != null) {
                try { // Converting the string to bytes for transferring
                    data.add(convertStreamToString(socket.getInputStream()));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                data.add("997;");
                isShutingDown = true;
            }
        }
    }

    public String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int ch; (ch = is.read()) != -1; ) {
            i++;
            sb.append((char) ch);
            if(i==3){
                break;
            }
        }
        return sb.toString();
    }
    public ArrayList<String> getData(){
        return data;
    }
    public void shutDown(){
        isShutingDown = true;
    }
}
