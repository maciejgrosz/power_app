package com.example.wpam_power_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        ViewPager2 sampleViewPager = findViewById(R.id.sampleViewPager);
        SampleAdapter adapter = new SampleAdapter(this);
        adapter.addFragment(new LoginFragment());
        adapter.addFragment(new RegisterFragment());
        sampleViewPager.setAdapter(adapter);
    }



    static class SampleAdapter extends FragmentStateAdapter{
        private final ArrayList<Fragment> fragmentsList = new ArrayList<>();

        public SampleAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        void addFragment(Fragment fragment) {
            fragmentsList.add(fragment);
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentsList.size();
        }

    }
}

