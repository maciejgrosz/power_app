package com.example.wpam_power_app;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class App extends AppCompatActivity {
    private Button connectBtn, backBtn;
    private TextView userDetails;
    String deviceName = "HC-05";

    String address = "98:D3:71:F6:39:3C";

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);


        connectBtn = findViewById(R.id.connectBtn);
        backBtn = findViewById(R.id.backBtn);
        userDetails = findViewById(R.id.userDetails);
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
                    new App.ConnectBT().execute();

                }


            }
        }
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
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
                finish();
            } else {
                userDetails.setText("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }

}
