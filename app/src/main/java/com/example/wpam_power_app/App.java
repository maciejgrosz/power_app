package com.example.wpam_power_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class App extends AppCompatActivity {
    private Button connectBtn, backBtn, readBtn, getDataBtn;
    private TextView userDetails;
    String deviceName = "HC-05";

    String address = "98:D3:71:F6:39:3C";

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayList<String> data;
//    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        readBtn = findViewById(R.id.readBtn);
        getDataBtn = findViewById(R.id.getDataBtn);
        connectBtn = findViewById(R.id.connectBtn);
        backBtn = findViewById(R.id.backBtn);
        userDetails = findViewById(R.id.userDetails);
        ReadThread readThread = new ReadThread();
//        context = App.this;
        ProfileModel Profile = (ProfileModel) getIntent().getSerializableExtra("Profile");
        setUserDetails(Profile);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();


            }
        });
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData(readThread);

            }
        });
        getDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDetails.setText(getData(readThread).get(0));
            }
        });
    }

    private void setUserDetails(ProfileModel profile) {
        String details = String.format("USER DETAILS: \n%s %s, \nWeight: %s, \nHeight: %s", profile.getSurname(), profile.getName(), profile.getWeight(), profile.getHeight());
        userDetails.setText(details);
    }

    private void back() {
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(App.this, Manifest.permission.BLUETOOTH_CONNECT)) {
                    return;
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void connect() {
//        Phone bluetooth address 3C:5A:B4:01:02:03
//        D0:97:FE:CD:6B:7C - nieznane
//        24:4B:81:BC:A8:12 - Galaxy s6

// hc-05 address: 98D3:71:F6393C
// hc-05 pin: "1234"/ name: "HC-05"
        BluetoothManager bluetoothManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager.class);
        }
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            userDetails.setText("Device doesn't support Bluetooth");
        }
        if (!bluetoothAdapter.isEnabled()) {
            userDetails.setText("Turn on your bluetooth");
        }


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                if (deviceName.equals(device.getName())) {
                    userDetails.setText("hc-05 is available");
                }
            }
            new ConnectBT().execute();
        }
    }


    private void readData(ReadThread readThread)
    {

        readThread.start();
    }

    private ArrayList<String> getData(ReadThread readThread){

        data = readThread.getData();
//        readThread.shutDown();
        return data;
    }
//    public static Context getContext(){
//        return context;
//    }

    public class ConnectBT extends AsyncTask<Void, Void, Void> {
        final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        //    BluetoothSocket btSocket = null;
        private ProgressDialog progress;
        String address = "98:D3:71:F6:39:3C";
        BluetoothAdapter myBluetooth = null;
        private boolean isBtConnected = false;


        private boolean ConnectSuccess = true;


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(App.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }
        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                userDetails.setText("Connection Failed. Is it a SPP Bluetooth? Try again.");
            } else {
                userDetails.setText("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }

    }

    public class ReadThread extends Thread{
        public volatile boolean isShutingDown;
        public volatile String test;
        ArrayList<String> data = new ArrayList<String>( );
        public void run(){
            while(!isShutingDown){
                if (btSocket != null) {
                    try { // Converting the string to bytes for transferring
                        data.add(convertStreamToString(btSocket.getInputStream()));
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

}