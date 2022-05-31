package com.example.wpam_power_app;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;

public class ReadThread extends Thread{
    BluetoothSocket socket;
    public ReadThread(BluetoothSocket socket){
        this.socket = socket;
    }
    public void run(){
        if (socket != null) {
            try { // Converting the string to bytes for transferring
                convertStreamToStiring(socket.getInputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String convertStreamToStiring(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        Integer i = 0;
        for (int ch; (ch = is.read()) != -1; ) {
            i++;
            sb.append((char) ch);
            if(i==3) {
                break;
            }
        }
        return sb.toString();
    }
}
