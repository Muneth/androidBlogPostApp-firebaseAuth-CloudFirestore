package com.example.soutnence_3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.soutnence_3.model.Journal;
import com.example.soutnence_3.ui.JournalRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import util.JournalUser;

/**
 * This class is the activity for the list of all journal entries from all users.
 */

public class JournalListAll extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Journal");
    private TextView noJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list_all);


/**
 * Firebase Authentication
 */
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

//      Widgets
        noJournalEntry = findViewById(R.id.list_no_posts_all);
        recyclerView = findViewById(R.id.journal_list_recycler_view_all);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Posts ArrayList
        journalList = new ArrayList<>();
    }

    //    Adding the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is for the menu items.
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
//      Take user to MyJournalActivity
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(JournalListAll.this,MyJournal.class));
                    finish();
                }
                break;
            case R.id.action_logout:
//      Signout user
                if(currentUser != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListAll.this,MainActivity.class));
                    finish();
                }
                break;
//      Dashboard
            case R.id.action_dashboard:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(JournalListAll.this,JournalList.class));
                    finish();
                }
                break;
//      All Posts
            case R.id.home:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(JournalListAll.this,JournalListAll.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is to get the data from the database and display it in the recycler view.
     */
    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(!task.getResult().isEmpty()){
                    for(QueryDocumentSnapshot journals : task.getResult()){
                        Journal journal = journals.toObject(Journal.class);
                        journalList.add(journal);
                    }
                    journalRecyclerAdapter = new JournalRecyclerAdapter(JournalListAll.this,journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();
                }else{
                    noJournalEntry.setVisibility(View.VISIBLE);
                }
            }else{
                noJournalEntry.setVisibility(View.VISIBLE);
            }
        });
    }
}