package com.example.snapchatclone;


import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageLists {

    static ArrayList<String> emails;
    static int success=1;
    ManageLists(){
        emails=new ArrayList<>();
    }

    public void addUser(String Uid,String email){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Uid);
        ArrayList<String> uid=new ArrayList<>();
        ArrayList<String> sender=new ArrayList<>();
        ArrayList<String> file=new ArrayList<>();
        uid.add("start");
        sender.add("start");
        file.add("start");
        myRef.child("uid").setValue(uid);
        myRef.child("sender").setValue(sender);
        myRef.child("file").setValue(file);
        DatabaseReference ref = database.getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> users = (ArrayList<String>) snapshot.getValue();
                users.add(Uid+"~"+email);
                ref.setValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }

}
