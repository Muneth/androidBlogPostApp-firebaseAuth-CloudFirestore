package com.example.soutnence_3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    Button register,login;
    EditText email,password;

//    Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

//    FireBase Connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

//        Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(i);
        });

        login.setOnClickListener(v -> LoginEmailPasswordUser(email.getText().toString(),password.getText().toString()));
    }

    private void LoginEmailPasswordUser(String email, String password) {
        if(!email.isEmpty() && !password.isEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
                String currentUserId = user.getUid();

                collectionReference.whereEqualTo("userId",currentUserId).addSnapshotListener((value, error) -> {
                    if(error != null){
                        return;
                    }
                    assert value != null;
                    if(!value.isEmpty()) {
//      Getting all the data from the database
                        for (QueryDocumentSnapshot snapshot : value) {
                            String userId = snapshot.getString("userId");
                            String username = snapshot.getString("username");
                            Toast.makeText(MainActivity.this, "Welcome " + username, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, MyJournal.class));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No such user", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}