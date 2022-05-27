package com.example.wpam_power_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
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

    private void setUserDetails(ProfileModel profile){
        String details = String.format("USER DETAILS: \n%s %s, \nWeight: %s, \nHeight: %s", profile.getSurname(), profile.getName(), profile.getWeight(), profile.getHeight());
        userDetails.setText(details);
    }

    private void back(){
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }

    private void connect(){
        
    }

}