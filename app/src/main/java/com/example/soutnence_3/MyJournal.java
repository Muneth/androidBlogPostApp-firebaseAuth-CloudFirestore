package com.example.soutnence_3;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soutnence_3.model.Journal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

import util.JournalUser;

public class MyJournal extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private TextView welcomeUser;
    private Button postSave, backToDashboard;
    private ProgressBar progressBar;
    private ImageView addImageButton, imageView;
    private EditText title, thoughts;

//    User Id & Username
    private String currentJournalUserId;
    private String currentUsername;

//  Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

//  FireBase Connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private final CollectionReference collectionReference = db.collection("Users");
    private final CollectionReference collectionReferenceJournal = db.collection("Journal");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journal);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        welcomeUser = findViewById(R.id.welcomeuser);
        progressBar = findViewById(R.id.post_progressBar);
        imageView = findViewById(R.id.post_imageview);
        title = findViewById(R.id.post_title_edittext);
        thoughts = findViewById(R.id.post_description_edittext);
        postSave = findViewById(R.id.post_save_button);
        addImageButton = findViewById(R.id.postCameraButton);
        backToDashboard = findViewById(R.id.backtodashboard);

        progressBar.setVisibility(View.INVISIBLE);

//        Getting the current user
        if(JournalUser.getInstance() != null){
            currentJournalUserId = JournalUser.getInstance().getUserId();
            currentUsername = JournalUser.getInstance().getUsername();
            welcomeUser.setText("Bienvenue  "+ currentUsername.toUpperCase());
        }

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null){
                //user is logged in
            }else{
                //user is not logged in
            }
        };

        backToDashboard.setOnClickListener(v -> {
            startActivity(new Intent(MyJournal.this, JournalList.class));
            finish();
        });

//      Save Journal Post to FireStore
        postSave.setOnClickListener(v -> {
            saveJournal();
        });

//      Add image from gallery to imageview
        addImageButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        });
    }

    private void saveJournal() {
        progressBar.setVisibility(View.VISIBLE);
        final String title = this.title.getText().toString().trim();
        final String thought = this.thoughts.getText().toString().trim();

        if (!title.isEmpty() && !thought.isEmpty() && imageUri != null) {
//          Saving the image path to the firebase database
            final StorageReference filepath = storageReference.child("journal_images").child("my_image_" + Timestamp.now().getSeconds());
//          Upload the image
            filepath.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    Journal journal = new Journal();
                    journal.setTitle(title);
                    journal.setThoughts(thought);
                    journal.setImageUrl(downloadUri.toString());
                    journal.setTimeAdded(String.valueOf(new Timestamp(new Date())));
                    journal.setUserName(currentUsername);
                    journal.setUserId(currentJournalUserId);

//          Saving the journal to the database
                    collectionReferenceJournal.add(journal).addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(MyJournal.this, JournalList.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                } else {
                    Toast.makeText(this, "Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Toast.makeText(this, "Empty Fields not allowed", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}