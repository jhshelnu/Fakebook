package com.restclient.containers;

/**
 * Container class to store comment info
 */
public class Comment {
    private final Post post;
    private final int id;

    private final String title;
    private final String body;
    private final String email;

    public Comment(Post post, int id, String title, String body, String email) {
        this.post = post;
        this.id = id;
        this.title = title;
        this.body = body;
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getEmail() {
        return email;
    }
}
