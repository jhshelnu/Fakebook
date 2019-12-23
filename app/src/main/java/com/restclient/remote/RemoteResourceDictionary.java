package com.restclient.remote;

/**
 * Dictionary class that contains all URLs that HTTP requests are sent to
 */
public class RemoteResourceDictionary {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static final String FEED_URL = BASE_URL + "/posts";
    public static final String COMMENTS_URL = BASE_URL + "/comments?postId=%s";
    public static final String USER_URL = BASE_URL + "/users/%s";
    public static final String USER_POSTS_URL = FEED_URL + "?userId=%s";
}
