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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class App extends AppCompatActivity {
    private Button connectBtn, backBtn, readBtn, getDataBtn;
    private TextView userDetails;
    String deviceName = "HC-05";
    String address = "98:D3:71:F6:39:3C";
    String dataString;
    double J = 0.000193152;
    double r = 0.0625;
    double g = 9.81;
    double fLeash = 150.0/1000 * g;
    double C = fLeash * r;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayList<Double> data;

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
        ProfileModel Profile = (ProfileModel) getIntent().getSerializableExtra("Profile");

//        context = App.this;
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
                ArrayList<Double> powers = getData(readThread);
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

    private ArrayList<Double> getData(ReadThread readThread){
        readThread.shutDown();
        data = readThread.getPowers();
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
        ArrayList<String> times = new ArrayList<>(3);
        ArrayList<String> encoder = new ArrayList<>(3);
        ArrayList<Double> powers = new ArrayList<>();
        ArrayList<Double> velocities = new ArrayList<>();
        ArrayList<Double> accelerations = new ArrayList<>();
        ArrayList<Double> shifts = new ArrayList<>();
        int i=0;
        int tc = 0;

        public void run(){
            while(!isShutingDown){
                if (btSocket != null) {
                    try { // Converting the string to bytes for transferring
                        dataString = convertStreamToString(btSocket.getInputStream());
                        String[] pack = dataString.split("-");
                        encoder.add(pack[0]);

                        String time = pack[1].replaceAll("\\s+","");
                        times.add(time);
                        if(encoder.size() == 3){
                            powers.add(processingData(encoder, times, tc).getpLift());
                            velocities.add(processingData(encoder, times, tc).getV());
                            accelerations.add(processingData(encoder, times, tc).getA());
                            shifts.add(processingData(encoder, times, tc).getShift());

                            encoder.clear();
                            times.clear();
                        }
                        i++;


//                        if(measurements.length == 6) {
//                            measurements = new String[measurements.length];
//
//                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    isShutingDown = true;
                }
            }
        }

        public String convertStreamToString(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            for (int ch; (ch = is.read()) != -1; ) {
                sb.append((char) ch);
                if(ch == '\n'){
                    break;
                }
            }
            return sb.toString();
        }

        public ArrayList<Double> getPowers() {
            return powers;
        }

        public ArrayList<Double> getVelocities() {
            return velocities;
        }

        public ArrayList<Double> getAccelerations() {
            return accelerations;
        }

        public ArrayList<Double> getShifts() {
            return shifts;
        }

        public void shutDown(){
            isShutingDown = true;
        }
    }

    private DataModel processingData(ArrayList<String> encoder,ArrayList<String> times, double tc ){
        ProfileModel Profile = (ProfileModel) getIntent().getSerializableExtra("Profile");

        int w = Integer.parseInt(Profile.getWeight());
        int h = Integer.parseInt(Profile.getHeight());
        int wBarbell = 100; // TODO have to be passed from text box
        double wSum = 7.4484 + 0.76694 * w - 0.05192 * h + wBarbell;
        double A = J + (wSum * Math.pow(r, 2));
        double B = wSum * g * r;
        double fi0 = calculateFi(Integer.parseInt(encoder.get(0)));
        double fi1 = calculateFi(Integer.parseInt(encoder.get(1)));
        double fi2 = calculateFi(Integer.parseInt(encoder.get(2)));

        double t0 = Integer.parseInt(times.get(0))/1000.0;
        double t1 = Integer.parseInt(times.get(1))/1000.0;
        double t2 = Integer.parseInt(times.get(2))/1000.0;

        double ts0 = t1 - t0;

        if(ts0>1 | ts0==0 | ts0 < 0.0001) ts0 = 0.01;

        tc = tc + ts0;

        double v0 = (fi1 - fi0)/ts0;
        double v1 = (fi2 - fi1)/ts0;

        double a = (v1 - v0)/ts0; // could be a problem with ts1
        double fiPrim = (fi2 - fi1)/ts0;
        double fiBis = (fi2 - 2 * fi1 + fi0)/(Math.pow(ts0, 2));
        double pLift = A * fiPrim * fiBis + B * fiPrim + C *fiPrim;
        double shift = fi2 * r;
        return new DataModel(v1, a, pLift, tc, shift);
    }

    private float calculateFi(int a){
        return (float) (((a-1000)/200) * 3.14);
    }

}