package com.example.soutnence_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyJournal extends AppCompatActivity {

    TextView currentuser;

//  FireBase Connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Users");

//  Firebase Auth
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journal);

        currentuser = findViewById(R.id.welcomeuser);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String currentUserId = user.getUid();

        collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                for (QueryDocumentSnapshot snapshot : value) {
                    String username = snapshot.getString("username");
                    currentuser.setText("Welcome "+username);
                }
            }
        });
    }
}