package io.github.veeshostak.taskmanager.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vladshostak on 1/17/17.
 */

public class Task {

    public String uid;
    public String title;
    public String body;



    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Task(String uid, String title, String body) {
        this.uid = uid;
        this.title = title;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("title", title);
        result.put("body", body);

        return result;
    }
}
