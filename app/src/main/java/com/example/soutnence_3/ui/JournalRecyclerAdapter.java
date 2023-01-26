package com.example.soutnence_3.ui;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soutnence_3.R;
import com.example.soutnence_3.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import util.JournalUser;

/**
 * This class is the adapter for the recycler view in the JournalListAll class.
 * It is used to display the journal entries in the recycler view.
 */
public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Journal journal = journalList.get(position);
        String imageUrl;

        holder.title.setText(journal.getTitle());
        holder.thoughts.setText(journal.getThoughts());
        holder.name.setText(journal.getUserName());
        imageUrl = journal.getImageUrl();

//        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
//        holder.timestamp.setText(timeAgo);

//     Use Glide to get the image
        Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title, thoughts, name, timestamp;
        public ImageView image;
        public ImageView shareButton;
        String userId;
        String username;

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


       public ViewHolder(@NonNull View itemView, Context ctx) {
           super(itemView);

              context = ctx;
              title = itemView.findViewById(R.id.journal_title_list);
              thoughts = itemView.findViewById(R.id.journal_thought_list);
//            timestamp = itemView.findViewById(R.id.journal_timestamp_list);
              image = itemView.findViewById(R.id.journal_image_list);
              name = itemView.findViewById(R.id.journal_row_username);
              shareButton = itemView.findViewById(R.id.journal_row_share_button);

           //        Getting the current user
           if(JournalUser.getInstance() != null){
               userId = JournalUser.getInstance().getUserId();
           }

           authStateListener = firebaseAuth -> {
               currentUser = firebaseAuth.getCurrentUser();
               if(currentUser != null){
                   //user is logged in
               }else{
                   //user is not logged in
               }
           };

           shareButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
//                Delete the post
//                      deleteJournal();
                      Toast.makeText(ctx, "Button clicked", Toast.LENGTH_SHORT).show();
                  }
              });
       }

            private void deleteJournal() {
//                  Delete the post
                if(currentUser != null && firebaseAuth.getCurrentUser() != null) {
                    collectionReferenceJournal.document(userId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    journalList.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Journal", "onFailure: " + e.getMessage());
                                }
                            });
                }
            }
   }
}
