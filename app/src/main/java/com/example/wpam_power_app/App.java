package com.example.wpam_power_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class App extends AppCompatActivity {
    private Button connectBtn, backBtn;
    private TextView userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);


        connectBtn = findViewById(R.id.connectBtn);
        backBtn = findViewById(R.id.backBtn);
        userDetails = findViewById(R.id.userDetails);
        ProfileModel Profile = (ProfileModel) getIntent().getSerializableExtra("Profile");
        setUserDetails(Profile);
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



    private void connect() {
// hc-05 address: 98D3:71:F6393C
// hc-05 pin: "1234"/ name: "HC-05"
//        if (ActivityCompat.checkSelfPermission(this,) {
//
//            return;
//        }
        ArrayList<String> devices = new ArrayList<String>();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!btAdapter.isEnabled()){
            userDetails.setText("BT is not enabled");
        }
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:71:F6:39:3C");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            userDetails.setText("permission problem");
        }
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                devices.add(deviceName);
            }
        }
        userDetails.setText("no permission problem" + devices.size());



    }

}