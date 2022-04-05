package com.example.wpam_power_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Profiles extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        Button add_new_profile_btn = (Button) findViewById(R.id.add_new_profile_btn);
        add_new_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForm();
            }
        });
    }

    public void openForm() {
        Intent intent = new Intent(this, Form.class);
        startActivity(intent);
    }
}