package io.github.veeshostak.taskmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.github.veeshostak.taskmanager.models.Task;
import io.github.veeshostak.taskmanager.models.User;

/**
 * Created by vladshostak on 1/17/17.
 */

public class NewTaskActivity extends AppCompatActivity {


    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;


    private AutoCompleteTextView mTopicField;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBodyField = (EditText) findViewById(R.id.postBody);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fabSubmitPost);

        // adapter for title/topic field
        ArrayAdapter<String> adapterTopics = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TOPICS); // TOPICS array of strings
        mTopicField = (AutoCompleteTextView) findViewById(R.id.postTopic);
        mTopicField.setAdapter(adapterTopics);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }


    // Can place TOPICS array in a stringsArray.xml file
    private static final String[] TOPICS = new String[] {
            "personal", "work", "shopping", "design", "money",
            "errand", "health", "food", "family", "fun"
    };

    private void submitPost() {
        final String title = mTopicField.getText().toString().trim().toLowerCase();
        final String body = mBodyField.getText().toString();

        // Topic is required
        if (TextUtils.isEmpty(title)) {
            mTopicField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting Task...", Toast.LENGTH_SHORT).show();


        final String userId = getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewTaskActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, title, body);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTopicField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Task task = new Task(userId, title, body);
        Map<String, Object> postValues = task.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/tasks/" + key, postValues);
        childUpdates.put("/user-active-tasks/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
