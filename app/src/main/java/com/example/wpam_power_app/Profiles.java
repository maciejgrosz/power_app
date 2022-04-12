package com.example.wpam_power_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profiles extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnlogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        mAuth = FirebaseAuth.getInstance();
        btnlogout = findViewById(R.id.logout_btn);

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        Button add_new_profile_btn = (Button) findViewById(R.id.add_new_profile_btn);
        add_new_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForm();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            startActivity(new Intent(this, StartScreen.class));
        }
    }

    public void openForm() {
        startActivity(new Intent(this, Form.class));
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, StartScreen.class));
    }
}