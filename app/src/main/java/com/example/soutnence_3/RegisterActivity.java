package com.example.soutnence_3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalUser;

public class RegisterActivity extends AppCompatActivity {

    TextView registertextview;
    EditText password_create, email_create, username_create;
    Button register, backToLogin;
    Animation animate_btn,animate_txt, animate_txt2;

//  Firebase Auth
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

//  FireBase Connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registertextview = findViewById(R.id.registertextview);
        register = findViewById(R.id.register);
        backToLogin = findViewById(R.id.backtologin);
        password_create = findViewById(R.id.password_create);
        email_create = findViewById(R.id.email_create);
        username_create = findViewById(R.id.username_create);

//        Load the animation from the xml file
        animate_txt = AnimationUtils.loadAnimation(this, R.anim.animate_texts);
        animate_txt2 = AnimationUtils.loadAnimation(this, R.anim.animate_texts2);
        animate_btn = AnimationUtils.loadAnimation(this, R.anim.animate_btn);
//        Start the animation
        registertextview.startAnimation(animate_txt);
        email_create.startAnimation(animate_txt2);
        password_create.startAnimation(animate_txt2);
        username_create.startAnimation(animate_txt2);
        register.startAnimation(animate_btn);
        backToLogin.startAnimation(animate_btn);

//      Authentication
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null){
                //user is already logged in
            }else{
                //no user yet
            }
        };
//      To Login Activity
        backToLogin.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });

//      Register
        register.setOnClickListener(v -> {
            if(!email_create.getText().toString().isEmpty() && !password_create.getText().toString().isEmpty()){
                String email = email_create.getText().toString().trim();
                String password = password_create.getText().toString().trim();
                String username = username_create.getText().toString().trim();

//      Create User Account
                createUserEmailAccount(email,password,username);
            } else {
                Toast.makeText(RegisterActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserEmailAccount(String email, String password, String username) {
        if(!email.isEmpty() && !password.isEmpty() && !username.isEmpty()){
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

//  Create a User Map so we can create a user in the user Collection in Firestore Database
                            Map<String,String> userObj = new HashMap<>();
                            userObj.put("userId",currentUserId);
                            userObj.put("username",username);

//  Save to our Firestore Database
                            collectionReference.add(userObj)
                                    .addOnSuccessListener(documentReference -> documentReference.get()
                                            .addOnCompleteListener(task1 -> {
                                                if(Objects.requireNonNull(task1.getResult()).exists()){
                                                    String name = task1.getResult().getString("username");

// if the user is registered successfully then we start the MyJournal Activity
// Getting the Global Journal user
                                                    JournalUser journalUser = JournalUser.getInstance();
                                                    journalUser.setUserId(currentUserId);
                                                    journalUser.setUsername(name);

                                                    Toast.makeText(RegisterActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(RegisterActivity.this, MyJournal.class);
                                                    intent.putExtra("username",name);
                                                    intent.putExtra("userId",currentUserId);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show()));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Could not register", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}