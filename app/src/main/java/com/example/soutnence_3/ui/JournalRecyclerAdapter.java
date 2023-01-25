package com.example.soutnence_3.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soutnence_3.R;
import com.example.soutnence_3.model.Journal;

import java.util.List;

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

       public ViewHolder(@NonNull View itemView, Context ctx) {
           super(itemView);

              context = ctx;
              title = itemView.findViewById(R.id.journal_title_list);
              thoughts = itemView.findViewById(R.id.journal_thought_list);
              timestamp = itemView.findViewById(R.id.journal_timestamp_list);
              image = itemView.findViewById(R.id.journal_image_list);
              name = itemView.findViewById(R.id.journal_row_username);
              shareButton = itemView.findViewById(R.id.journal_row_share_button);
              shareButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

//                Sharing the post

                  }
              });
       }
   }
}
