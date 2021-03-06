package com.example.wpam_power_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword, reg_email, reg_password, pass_reapeted;
    private Button loginbtn;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        loginEmail = view.findViewById(R.id.et_email);
        loginPassword = view.findViewById(R.id.et_password);
        loginbtn = view.findViewById(R.id.btn_login);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        return view;
    }

    private void login() {
        String user = loginEmail.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();
        if(user.isEmpty()){
            loginEmail.setError("Email cannot be empty");
        }
        if(pass.isEmpty()){
            loginPassword.setError("Password cannot be empty");
        }
        else{
            mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), Profiles.class));
                    }
                    else{
                        Toast.makeText(getActivity(), "Login Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



}

