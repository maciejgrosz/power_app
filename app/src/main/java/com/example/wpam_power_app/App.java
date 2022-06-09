package com.example.wpam_power_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class App extends AppCompatActivity {
    private Button connectBtn, backBtn, saveBtn, startBtn, stopBtn, disconnectBtn;
    private TextView statusBt, powerVal, accelVal, veloVal;
    String dataString;
    double J = 0.000193152;
    double r = 0.0625;
    double g = 9.81;
    double fLeash = 150.0/1000 * g;
    double C = fLeash * r;
    private boolean isBtConnected = false;
    BluetoothSocket btSocket = null;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app/");
    private DatabaseReference root = db.getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ProfileModel profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        profile = (ProfileModel) getIntent().getSerializableExtra("Profile");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        startBtn = findViewById(R.id.btnStart2);
        stopBtn = findViewById(R.id.btnStop);
        connectBtn = findViewById(R.id.btnConnect);
        disconnectBtn = findViewById(R.id.btnDisconnect);
        saveBtn = findViewById(R.id.btnSave);
        backBtn = findViewById(R.id.backBtn2);
        statusBt = findViewById(R.id.statusBt);
        powerVal = findViewById(R.id.powerVal);
        veloVal = findViewById(R.id.veloVal);
        accelVal = findViewById(R.id.accelVal);
        stopBtn.setEnabled(false);
        disconnectBtn.setEnabled(false);
        startBtn.setEnabled(false);
        saveBtn.setEnabled(false);


        ReadThread readThread = new ReadThread();




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
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(readThread);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop(readThread);
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                disconnect();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                exportValues();
            }
        });
    }

    private void start(ReadThread readThread){
        if(!readThread.isAlive()) {
            readThread.start();
            Toast.makeText(getBaseContext(), "Start!", Toast.LENGTH_SHORT).show();
        }
        else if(!isBtConnected){
            Toast.makeText(getBaseContext(), "Check bt connections", Toast.LENGTH_SHORT).show();

        }
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        saveBtn.setEnabled(false);
    }

    private void stop(ReadThread readThread){
        ArrayList<Integer> raw = readThread.getRaw();
        readThread.stopThread();
        ArrayList<Double> powers = readThread.getPowers();
        ArrayList<Double> velocities= readThread.getVelocities();
        ArrayList<Double> accelerations = readThread.getAccelerations();
        ArrayList<Double> shifts = readThread.getShifts();
        fakeGenerator();
        saveBtn.setEnabled(true);
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }
    private void back() {
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
        disconnect();
    }

    private void connect() {
        if(!isBtConnected) {
            new ConnectBT().execute();
        }
        disconnectBtn.setEnabled(true);
        startBtn.setEnabled(true);
        connectBtn.setEnabled(false);
    }

    private void disconnect(){
        if(isBtConnected){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            statusBt.setText("Disconnected");
            isBtConnected = false;
            disconnectBtn.setEnabled(false);
            connectBtn.setEnabled(true);
            startBtn.setEnabled(false);
        }
    }

    private void exportValues(){
        String power = powerVal.getText().toString();
        String accel = accelVal.getText().toString();
        String velo = veloVal.getText().toString();
        ResultModel results = new ResultModel(power, accel, velo);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String id = formatter.format(date).replaceAll("[^0-9]", "");
        root.child("users").child(uid).child(profile.getId()).child("results").child(id).setValue(results);
    }

    private void fakeGenerator(){
        powerVal.setText("999");
        accelVal.setText("999");
        veloVal.setText("999");
    }

    public class ConnectBT extends AsyncTask<Void, Void, Void> {
        final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private ProgressDialog progress;
        String address = "98:D3:71:F6:39:3C";
        BluetoothAdapter myBluetooth = null;
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(App.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (!isBtConnected) {
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
                statusBt.setText("Connection Failed");
            } else {
                statusBt.setText("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    public class ReadThread extends Thread{

        ArrayList<Double> times = new ArrayList<>();
        ArrayList<Double> powers = new ArrayList<>();
        ArrayList<Double> velocities = new ArrayList<>();
        ArrayList<Double> accelerations = new ArrayList<>();
        ArrayList<Double> shifts = new ArrayList<>();
        ArrayList<Integer> raw = new ArrayList<>();


        private Thread worker;
        private final AtomicBoolean running = new AtomicBoolean(true);
        ProfileModel Profile = (ProfileModel) getIntent().getSerializableExtra("Profile");
        int w = Integer.parseInt(Profile.getWeight());
        int h = Integer.parseInt(Profile.getHeight());
        int wBarbell = 100; // TODO have to be passed from text box
        double wSum = 7.4484 + 0.76694 * w - 0.05192 * h + wBarbell;
        double A = J + (wSum * Math.pow(r, 2));
        double B = wSum * g * r;
        double fi0=0, fi1=0, fi2=0, t0=0, t1=0, ts=0.01, tc=0, v=0, v1=0, a1=0, a=0;
        double fiPrim, fiBis, pLift, shift;

        public void start() {
            worker = new Thread(this);
            worker.start();
        }

        public void stopThread() {
            running.set(false);

        }

        public void run(){

            while(running.get()){
                if (btSocket != null) {
                    try {
                        dataString = convertStreamToString(btSocket.getInputStream());
                        String[] pack = dataString.split("-");

                        if(pack.length <2 | pack[0].equals("")){
                            continue;
                        }
                        double[] values = calculateValues(pack);


                        powers.add(values[0]);
                        accelerations.add(values[1]);
                        velocities.add(values[2]);
                        shifts.add(values[3]);
                        times.add(values[4]);
                        raw.add(Integer.parseInt(pack[0]));

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
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

        public ArrayList<Integer> getRaw() {
            return raw;
        }

        private double[] calculateValues(String[] pack){
            String encoder = pack[0].replaceAll("[^0-9]", "");
            String time = pack[1].replaceAll("[^0-9]", "");
            fi2 = fi1;
            fi1 = fi0;
            fi0 = calculateFi(Integer.parseInt(encoder));

            t1 = t0;
            t0 = Double.parseDouble(time)/1000;
            ts = t0 - t1;
            if(ts>1 | ts==0 | ts < 0.0001){
                ts=0.01;
            }
            v1 = v;
            v = (fi0 - fi1) * r/ts;

            a1 = a;
            a = (v-v1)/ts;
            tc = tc + ts;

            fiPrim = (fi0 - fi1)/ts;
            fiBis = (fi0 - 2*fi1 + fi2)/(Math.pow(ts, 2));
            pLift = A * fiPrim * fiBis + B * fiPrim + C * fiPrim;
            shift = fi0 * r;
            return new double[] {pLift, a, v, shift, tc};
        }

        private float calculateFi(int a){
            return (float) (((a-1000)/200) * 3.14);
        }

    }
}