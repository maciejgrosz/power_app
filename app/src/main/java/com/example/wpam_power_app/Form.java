package com.example.wpam_power_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Form extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // return super.onCreateOptionsMenu(menu);
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
        Button cancel_form_btn = (Button) findViewById(R.id.cancel_form_btn);
        cancel_form_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_form();
            }
        });
    }

    public void cancel_form(){
        Intent intent = new Intent(this, Profiles.class);
        startActivity(intent);
    }
}