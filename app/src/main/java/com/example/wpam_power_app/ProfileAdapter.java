package com.example.wpam_power_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<ProfileModel> {
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://power-app-53a05-default-rtdb.europe-west1.firebasedatabase.app/");
    private DatabaseReference root = db.getReference();
    private ArrayList<ProfileModel> profileList;
    public ProfileAdapter(Context context, int resource, ArrayList<ProfileModel> profileList){
        super(context, resource, profileList);
        this.profileList = profileList;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ProfileModel profile = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView tvSurname = (TextView) convertView.findViewById(R.id.textViewSurname);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.textViewWeight);
        TextView tvHeight = (TextView) convertView.findViewById(R.id.textViewHeight);


        tvName.setText(String.format("Name: %s",profile.getName()));
        tvSurname.setText(String.format("Surname: %s",profile.getSurname()));
        tvWeight.setText(String.format("Weight: %s", profile.getWeight()));
        tvHeight.setText(String.format("Height: %s", profile.getHeight()));
        return convertView;
    }
}
