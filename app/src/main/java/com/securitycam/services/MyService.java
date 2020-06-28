package com.securitycam.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.securitycam.BellActivity;
import com.securitycam.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseApp.initializeApp(this);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int safety = Integer.parseInt(String.valueOf(dataSnapshot.child("safety").getValue()));
                String bell = String.valueOf(dataSnapshot.child("bell").getValue());
                String data_image = String.valueOf(dataSnapshot.child("data_image").getValue());
                String security = String.valueOf(dataSnapshot.child("data_image").getValue());

                if (bell.equals("on")){
                    Intent intent = new Intent(getApplicationContext(), BellActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
