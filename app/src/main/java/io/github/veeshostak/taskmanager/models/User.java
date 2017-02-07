package io.github.veeshostak.taskmanager.models;

/**
 * Created by vladshostak on 1/16/17.
 */

public class User {
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
    }
}