package com.example.wpam_power_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class Profiles extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Button btnlogout, add_new_profile_btn;
    private ListView profilesList;
    private ArrayList<String> profilesNames = new ArrayList<>();
    private String uid;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        mAuth = FirebaseAuth.getInstance();
        btnlogout = findViewById(R.id.logout_btn);
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app");
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, profilesNames);

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (currentUser != null ) {
                    uid = currentUser.getUid();
                    reference = database.getReference("users").child(uid);
                    add_new_profile_btn = (Button) findViewById(R.id.add_new_profile_btn);


                    btnlogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            logout();
                        }
                    });

                    add_new_profile_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openForm();
                        }
                    });

                    profilesList = (ListView) findViewById(R.id.profile_list);
                    profilesList.setAdapter(adapter);
                    profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            openMainPage();
                        }
                    });
                    reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            profilesNames.add(getProfileName(dataSnapshot));
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            profilesNames.remove(getProfileName(dataSnapshot));
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
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
        mAuth.signOut();
        startActivity(new Intent(this, StartScreen.class));
    }

    public String getProfileName(DataSnapshot dataSnapshot){
        Map<String, Object> profileData = (Map<String, Object>) dataSnapshot.getValue();
        String surname = (String) profileData.get("surname");
        String name = (String) profileData.get("name");
        String weight = (String) profileData.get("weight");
        String height = (String) profileData.get("height");
        return String.format("Surname: %s\nName: %s\nWeight: %s kg\nHeight: %s cm", surname, name, weight, height);
    }

    public void openMainPage(){
        startActivity(new Intent(this, Form.class));
    }
}