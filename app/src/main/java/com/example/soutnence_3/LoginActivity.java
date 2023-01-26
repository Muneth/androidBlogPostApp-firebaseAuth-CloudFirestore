package com.example.soutnence_3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import util.JournalUser;

/**
 * A login screen that offers login via email/password.
 * This class is responsible for the login screen and the login functionality.
 */

public class LoginActivity extends AppCompatActivity {

    TextView logintextview;
    Button toRegister,login;
    EditText email,password;
    Animation animate_btn,animate_txt, animate_txt2;

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
        setContentView(R.layout.activity_login);

        logintextview = findViewById(R.id.logintextview);
        toRegister = findViewById(R.id.toregister);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

//        Load the animation from the xml file
        animate_txt = AnimationUtils.loadAnimation(this, R.anim.animate_texts);
        animate_txt2 = AnimationUtils.loadAnimation(this, R.anim.animate_texts2);
        animate_btn = AnimationUtils.loadAnimation(this, R.anim.animate_btn);
//        Start the animation
        logintextview.startAnimation(animate_txt);
        email.startAnimation(animate_txt2);
        password.startAnimation(animate_txt2);
        login.startAnimation(animate_btn);
        toRegister.startAnimation(animate_btn);

//        Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        toRegister.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(i);
        });

        login.setOnClickListener(v -> LoginEmailPasswordUser(email.getText().toString(),password.getText().toString()));
    }

    /**
     * This method is responsible for the login functionality.
     * This method is called when the user clicks on the login button and it verify the user in the database.
     * @param email
     * @param password
     */
    private void LoginEmailPasswordUser(String email, String password) {
        if(!email.isEmpty() && !password.isEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
                final String currentUserId = user.getUid();

                collectionReference.whereEqualTo("userId",currentUserId).addSnapshotListener((value, error) -> {
                    if(error != null){
                        return;
                    }
                    assert value != null;
                    if(!value.isEmpty()) {
//      Getting all the data from the database
                        for (QueryDocumentSnapshot snapshot : value) {
                            JournalUser journalUser = JournalUser.getInstance();
                            journalUser.setUsername(snapshot.getString("username"));
                            journalUser.setUserId(snapshot.getString("userId"));

//                            startActivity(new Intent(MainActivity.this, MyJournal.class));
                            startActivity(new Intent(LoginActivity.this, JournalListAll.class));
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "No such user", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }
    }
}