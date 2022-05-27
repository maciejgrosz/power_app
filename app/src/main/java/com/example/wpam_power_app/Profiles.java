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
import android.widget.RadioButton;
import android.widget.Toast;

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
    private Button btnlogout, add_new_profile_btn, btnDeleteProfile, btnEditProfile, btnStart;
    private ListView profilesList;
    private ArrayList<ProfileModel> profiles = new ArrayList<ProfileModel>();
    private String uid;
    private FirebaseUser currentUser;
    private RadioButton isSelected;
    private int listViewPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        listViewPosition = -1;
        mAuth = FirebaseAuth.getInstance();
        btnlogout = findViewById(R.id.logout_btn);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnStart = findViewById(R.id.btnStart);
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app");
        final ProfileAdapter adapter=new ProfileAdapter(this, 0, profiles);

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

                    btnStart.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            startApp();
                        }
                    });
                    btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            delete(adapter, reference);
                        }
                    });

                    btnEditProfile.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){editForm();}
                    });
                    profilesList = (ListView) findViewById(R.id.profile_list);
                    profilesList.setAdapter(adapter);
                    profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            listViewPosition = position;
                        }

                    });
//                    Is it necessary?
                    reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            profiles.add(createProfile(dataSnapshot));
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            profiles.remove(createProfile(dataSnapshot));
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
        startActivity(new Intent(this, Form.class).putExtra("id", profiles.size()));
    }

    public void editForm(){
        if(listViewPosition == -1){
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < profiles.size(); i++) {
            if (i == listViewPosition) {
                Intent intent = new Intent(this, EditForm.class);
                intent.putExtra("Profile", profiles.get(i));
                intent.putExtra("id", i);
                startActivity(intent);
            }
        }
        listViewPosition = -1;
    }

    public void logout(){
        mAuth.signOut();
        startActivity(new Intent(this, StartScreen.class));
    }

    public void delete(ProfileAdapter adapter, DatabaseReference reference){
        if(listViewPosition == -1){
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < profiles.size(); i++) {
            if (i == listViewPosition) {
                reference.child(String.format("Profile_%d",i)).removeValue();
                profiles.remove(i);
            }
            adapter.notifyDataSetChanged();
        }
        listViewPosition = -1;
    }

    public ProfileModel createProfile(DataSnapshot dataSnapshot){
        Map<String, Object> profileData = (Map<String, Object>) dataSnapshot.getValue();
        String surname = (String) profileData.get("surname");
        String name = (String) profileData.get("name");
        String weight = (String) profileData.get("weight");
        String height = (String) profileData.get("height");
        return new ProfileModel(name, surname, weight, height);
    }

    public void startApp(){
        if(listViewPosition == -1){
            Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < profiles.size(); i++) {
            if (i == listViewPosition) {
                Intent intent = new Intent(this, App.class);
                intent.putExtra("Profile", profiles.get(i));
                startActivity(intent);
            }
        }
        listViewPosition = -1;

    }
}