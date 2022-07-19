package com.example.energyapp.classes;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyUtilsClass {
    public static void preiaCost(String uid,MyCallback myCallback) {
        if (uid != null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("utilizatori").child(uid).child("costKWh");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!(snapshot.getValue(String.class).isEmpty())) {
                        float cost = Float.parseFloat(snapshot.getValue(String.class));
                        myCallback.onCallbackForCost(cost);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

}