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

public class RegisterFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText register_email, register_password, register_repass, reg_email, reg_password, pass_reapeted;
    private Button registerbtn;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        register_email = view.findViewById(R.id.et_email);
        register_password = view.findViewById(R.id.et_password);
        register_repass = view.findViewById(R.id.et_repassword);
        registerbtn = view.findViewById(R.id.btn_register);
        // Inflate the layout for this fragment

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
        return view;
    }

    private void Register() {
        String user = register_email.getText().toString().trim();
        String pass = register_password.getText().toString().trim();
        String repass = register_repass.getText().toString().trim();
        if(user.isEmpty()){
            register_email.setError("Email cannot be empty");
        }
        if(pass.isEmpty() || repass.isEmpty()){
            register_password.setError("Password cannot be empty");
        }
        if(!pass.equals(repass)){
            register_repass.setError("Passwords are different");
        }
        else{
            mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "User created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), Profiles.class));
                    }else{
                        Toast.makeText(getActivity(), "Registration failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

}