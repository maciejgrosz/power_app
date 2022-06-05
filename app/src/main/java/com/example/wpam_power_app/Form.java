package com.example.wpam_power_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Form extends AppCompatActivity {
    private EditText mName, mSurname, mWeight, mHeight;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app/");
    private DatabaseReference root = db.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Button cancel_form_btn = findViewById(R.id.cancel_form_btn);
        Button saveFormBtn = findViewById(R.id.btnSaveForm);

        mName = findViewById(R.id.etFirstName);
        mSurname = findViewById(R.id.etLastName);
        mWeight = findViewById(R.id.etWeight);
        mHeight = findViewById(R.id.etHeight);

        cancel_form_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelForm();
            }
        });

        saveFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString();
                String surname = mSurname.getText().toString();
                String weight = mWeight.getText().toString();
                String height = mHeight.getText().toString();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String id = formatter.format(date).replaceAll("[^0-9]", "");
                ProfileModel profile = new ProfileModel(name, surname, weight, height, id);
                saveFormToDB(uid, profile, id);
            }
        });
    }

    public void cancelForm(){
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);

    }

    public void saveFormToDB(String userID, ProfileModel profile, String id){
        root.child("users").child(userID).child(id).setValue(profile);
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);


    }
}