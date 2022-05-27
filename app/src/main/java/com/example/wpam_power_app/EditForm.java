package com.example.wpam_power_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class EditForm extends AppCompatActivity {
    private EditText mName, mSurname, mWeight, mHeight;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app/");
    private DatabaseReference root = db.getReference();
    private String name, surname, weight, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Button cancelFormBtn = findViewById(R.id.cancel_form_btn);
        Button editFormBtn = findViewById(R.id.btnSaveForm);
        editFormBtn.setText("Edit profile");
        ProfileModel Profile = (ProfileModel) getIntent().getSerializableExtra("Profile");
        int id = getIntent().getIntExtra("id",0);
        name = Profile.getName();
        surname = Profile.getSurname();
        weight = Profile.getWeight();
        height = Profile.getHeight();

        mName = findViewById(R.id.etFirstName);
        mName.setText(name);
        mSurname = findViewById(R.id.etLastName);
        mSurname.setText(surname);
        mWeight = findViewById(R.id.etWeight);
        mWeight.setText(weight);
        mHeight = findViewById(R.id.etHeight);
        mHeight.setText(height);

        cancelFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelForm();
            }
        });

        editFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_name = mName.getText().toString();
                String new_surname = mSurname.getText().toString();
                String new_weight = mWeight.getText().toString();
                String new_height = mHeight.getText().toString();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ProfileModel profile = new ProfileModel(new_name, new_surname, new_weight, new_height);
                updateFormToDB(uid, profile, id);
            }
        });
    }

    public void cancelForm(){
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);

    }

    public void updateFormToDB(String userID, ProfileModel profile, int id){

        root.child("users").child(userID).child(String.format("Profile_%d",id)).setValue(profile);
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }
}