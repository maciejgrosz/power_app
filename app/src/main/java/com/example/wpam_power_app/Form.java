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

public class Form extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mName, mSurname, mWeight, mHeight;
    private Button addFormbtn;
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app/");
    private DatabaseReference root = db.getReference();
    @Override

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id==R.id.item_done){
            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
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
                saveFormToDB(uid, name, surname, weight, height);
            }
        });
    }

    public void cancelForm(){
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);

    }

    public void saveFormToDB(String userID, String name, String surname, String weight, String height){

        root.child("users").child(userID).child(surname+weight).child("name").setValue(name);
        root.child("users").child(userID).child(surname+weight).child("surname").setValue(surname);
        root.child("users").child(userID).child(surname+weight).child("weight").setValue(weight);
        root.child("users").child(userID).child(surname+weight).child("height").setValue(height);
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);


    }
}