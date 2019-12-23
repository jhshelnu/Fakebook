package com.restclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.restclient.adapters.FeedAdapter;
import com.restclient.containers.Post;
import com.restclient.containers.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.restclient.remote.RemoteResourceDictionary.FEED_URL;
import static com.restclient.remote.RemoteResourceDictionary.USER_URL;


/**
 * FeedActivity is the main view of the app, displaying all blog posts in a scrollable fashion
 * Each post is clickable and loads the PostActivity screen
 * Usernames are clickable and load the UserActivity
 */
public class FeedActivity extends AppCompatActivity {
    private static final String TAG = "FeedActivity";

    private TextView loadingText;
    private ArrayList<Post> feed;
    private RequestQueue queue;
    private FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        loadingText = findViewById(R.id.loading_text);
        feed = new ArrayList<>();
        adapter = new FeedAdapter(this, feed);
        queue = Volley.newRequestQueue(this);

        final RecyclerView recyclerView = findViewById(R.id.feed_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchPosts();
    }

    /**
     * Asynchronously fetches all posts and populates the recyclerview
     */
    private void fetchPosts() {
        JsonArrayRequest feedRequest = new JsonArrayRequest(FEED_URL,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    loadingText.setVisibility(View.GONE);
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            // Extract info from json objects in the response
                            JSONObject postObj = response.getJSONObject(i);

                            int id = postObj.getInt("id");
                            int userId = postObj.getInt("userId");
                            String title = postObj.getString("title");
                            String body = postObj.getString("body");

                            // Store data in a container object, then fetch associated user info
                            Post post = new Post(id, title, body);
                            feed.add(post);
                            fetchUser(post, userId);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error loading posts.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingText.setText("Failed to load posts.\nCheck your internet connection.");
                    Log.e(TAG, Log.getStackTraceString(error));
                }
            });

        queue.add(feedRequest);
    }

    /**
     * Asynchronously fetch the user info for a given post
     * @param post the post to store the User object into
     * @param userId the userId for which user info is requested
     */
    private void fetchUser(final Post post, final int userId) {
        JsonObjectRequest userRequest = new JsonObjectRequest(String.format(USER_URL, userId), null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Extract info from json object  response
                        String username = response.getString("username");
                        String name = response.getString("name");
                        String email = response.getString("email");
                        String phoneNumber = response.getString("phone");
                        String website = response.getString("website");

                        JSONObject geoInfo = response.getJSONObject("address").getJSONObject("geo");
                        double lat = Double.valueOf(geoInfo.getString("lat"));
                        double lng = Double.valueOf(geoInfo.getString("lng"));

                        post.setUser(new User(userId, username, name, email, phoneNumber, website, new LatLng(lat, lng)));
                        adapter.notifyItemChanged(feed.indexOf(post));
                    } catch (JSONException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        queue.add(userRequest);
    }

}
