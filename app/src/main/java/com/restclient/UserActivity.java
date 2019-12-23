package com.restclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.restclient.containers.Post;
import com.restclient.containers.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static com.restclient.remote.RemoteResourceDictionary.USER_POSTS_URL;

/**
 * UserActivity displays user info, list of posts made by the user, and
 */
public class UserActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "UserActivity";

    private RequestQueue queue;
    private User user;
    private ArrayList<Post> posts;
    private LinearLayout pageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = getIntent().getParcelableExtra("user");
        queue = Volley.newRequestQueue(this);
        posts = new ArrayList<>();

        // The main LinearLayout that holds the page content, user's posts are dynamically loaded into views which are appended to this
        pageLayout = findViewById(R.id.page_layout);

        TextView name = findViewById(R.id.name_text);
        name.setText(user.getName());

        TextView username = findViewById(R.id.username_text);
        username.setText(String.format("(@%s)", user.getUsername()));

        TextView email = findViewById(R.id.email_text);
        email.setText(user.getEmail());

        TextView phoneNumber = findViewById(R.id.phone_text);
        phoneNumber.setText(user.getPhoneNumber());

        TextView website = findViewById(R.id.website_text);
        website.setText(user.getWebsite());

        fetchPosts();

        // Load the google map
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    /**
     * Asynchronously fetch posts made by a particular user
     * Stores the posts in the posts arraylist
     */
    private void fetchPosts() {
        JsonArrayRequest request = new JsonArrayRequest(String.format(USER_POSTS_URL, user.getUserId()),
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject postObj = response.getJSONObject(i);

                            int id = postObj.getInt("id");
                            String title = postObj.getString("title");
                            String body = postObj.getString("body");

                            Post post = new Post(id, title, body);
                            post.setUser(user);
                            posts.add(post);
                        } catch (JSONException e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }

                    displayPosts();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        queue.add(request);
    }

    /**
     * Displays the fetched posts by inflating views as needed and appending them to the main page layout
     */
    private void displayPosts() {
        TextView postsHeader = findViewById(R.id.posts_header_text);
        postsHeader.setText(String.format("Posts (%s)", posts.size()));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (final Post post : posts) {
            final View postView = inflater.inflate(R.layout.layout_post, pageLayout, false);

            // Populate inflated views with each post's info
            TextView authorText = postView.findViewById(R.id.author_text);
            authorText.setText(String.format("%s (@%s)", user.getName(), user.getUsername()));
            authorText.setTextSize(COMPLEX_UNIT_DIP, 15);

            TextView title = postView.findViewById(R.id.title_text);
            title.setText(post.getTitle());
            title.setTextSize(COMPLEX_UNIT_DIP, 20);

            // Each post is clickable to take the user into the PostActivity screen
            postView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(postView.getContext(), PostActivity.class);
                    intent.putExtra("post", post);
                    startActivity(intent);
                }
            });

            pageLayout.addView(postView);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(user.getLocation()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user.getLocation(), 2));

        initMapScrollFix();
    }

    private void initMapScrollFix() {
        final ScrollView scrollView = findViewById(R.id.scroll_view);
        ImageView transparentImage = findViewById(R.id.transparent_image);

        transparentImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }
}
