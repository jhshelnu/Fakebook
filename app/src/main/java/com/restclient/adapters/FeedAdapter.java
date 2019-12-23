package com.restclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restclient.PostActivity;
import com.restclient.R;
import com.restclient.containers.Post;
import com.restclient.containers.User;

import java.util.ArrayList;

/**
 * RecyclerView Adapter for the main FeedActivity to display all fetched posts in an efficient manner
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostViewHolder> {
    private static final String TAG = "FeedAdapter";

    private final Context ctx;
    private final ArrayList<Post> feed;

    public FeedAdapter(Context ctx, ArrayList<Post> feed) {
        this.ctx = ctx;
        this.feed = feed;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        final Post post = feed.get(position);

        User user = post.getUser();
        if (user != null) { // User info is fetched in separate HTTP requests, so this will be null initially
            holder.author.setText(String.format("%s (@%s)", user.getName(), user.getUsername()));
        }

        // Store post info in each viewholder, and make the view clickable to launch the PostActivity
        holder.title.setText(post.getTitle());
        holder.postId.setText(String.valueOf(post.getId()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, PostActivity.class);
                intent.putExtra("post", post);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feed.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView author;
        final TextView postId;
        final View itemView;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.title_text);
            this.author = itemView.findViewById(R.id.author_text);
            this.postId = itemView.findViewById(R.id.post_id);
        }
    }
}
