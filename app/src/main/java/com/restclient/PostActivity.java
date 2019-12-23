package com.restclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restclient.containers.Comment;
import com.restclient.containers.Post;
import com.restclient.containers.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.restclient.remote.RemoteResourceDictionary.COMMENTS_URL;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";

    private RequestQueue queue;
    private Post post;
    private ArrayList<Comment> comments;
    private LinearLayout pageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pageLayout = findViewById(R.id.page_layout);
        queue = Volley.newRequestQueue(this);
        comments = new ArrayList<>();
        post = getIntent().getParcelableExtra("post");


        TextView title = findViewById(R.id.title_text);
        title.setText(post.getTitle());

        TextView body = findViewById(R.id.body_text);
        body.setText(post.getBody());

        TextView author = findViewById(R.id.author_text);
        final User user = post.getUser();
        author.setText(String.format("%s\n(@%s)", user.getName(), user.getUsername()));
        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        fetchComments();
    }

    private void fetchComments() {
        JsonArrayRequest userRequest = new JsonArrayRequest(String.format(COMMENTS_URL, post.getId()),
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject commentObj = response.getJSONObject(i);

                            int id = commentObj.getInt("id");
                            String title = commentObj.getString("name");
                            String body = commentObj.getString("body");
                            String email = commentObj.getString("email");
                            comments.add(new Comment(post, id, title, body, email));
                        } catch (JSONException e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }

                    displayCommentSection();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        queue.add(userRequest);
    }

    private void displayCommentSection() {
        TextView commentHeader = findViewById(R.id.comment_header_text);
        commentHeader.setText(String.format("Comments (%s)", comments.size()));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Comment comment : comments) {
            View commentView = inflater.inflate(R.layout.layout_comment, pageLayout, false);
            TextView title = commentView.findViewById(R.id.title_text);
            TextView body = commentView.findViewById(R.id.body_text);
            TextView email = commentView.findViewById(R.id.email_text);

            title.setText(comment.getTitle());
            body.setText(comment.getBody());
            email.setText(comment.getEmail());

            pageLayout.addView(commentView);
        }

        final View makeCommentView = inflater.inflate(R.layout.layout_make_comment, pageLayout, false);

        Button submitButton = makeCommentView.findViewById(R.id.comment_submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailInput = makeCommentView.findViewById(R.id.email_input_text);
                String email = emailInput.getText().toString();

                EditText nameInput = makeCommentView.findViewById(R.id.name_input_text);
                String name = nameInput.getText().toString();

                EditText bodyInput = makeCommentView.findViewById(R.id.body_input_text);
                String body = bodyInput.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(body)) {
                   Toast.makeText(getApplicationContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    emailInput.setText("");
                    nameInput.setText("");
                    bodyInput.setText("");
                    addComment(email, name, body);
                }
            }
        });

        pageLayout.addView(makeCommentView);
    }

    private void addComment(final String email, final String name, final String body) {
        try {
            JSONObject commentInfo = new JSONObject()
                .put("email", email)
                .put("name", name)
                .put("body", body);

            JsonObjectRequest commentRequest = new JsonObjectRequest(COMMENTS_URL, commentInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("id");
                            Comment comment = new Comment(post, id, name, body, email);
                            comments.add(comment);

                            View commentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_comment, pageLayout, false);
                            TextView title = commentView.findViewById(R.id.title_text);
                            TextView body = commentView.findViewById(R.id.body_text);
                            TextView email = commentView.findViewById(R.id.email_text);

                            title.setText(comment.getTitle());
                            body.setText(comment.getBody());
                            email.setText(comment.getEmail());

                            pageLayout.addView(commentView, pageLayout.getChildCount() - 1);

                            TextView commentHeader = findViewById(R.id.comment_header_text);
                            commentHeader.setText(String.format("Comments (%s)", comments.size()));
                        } catch (JSONException e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Unable to make comment", Toast.LENGTH_SHORT).show();
                    }
                }
            );

            queue.add(commentRequest);

        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
