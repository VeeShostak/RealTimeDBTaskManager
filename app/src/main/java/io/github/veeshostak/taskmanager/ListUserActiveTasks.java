package io.github.veeshostak.taskmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import io.github.veeshostak.taskmanager.models.Task;
import io.github.veeshostak.taskmanager.viewholder.TaskViewHolder;

/**
 * Created by vladshostak on 1/17/17.
 */

public class ListUserActiveTasks extends Fragment {

    private static final String TAG = "ListUserActiveTasks";

    // holds database reference
    private DatabaseReference mDatabase;
    private DatabaseReference completedTasksRef;


    private FirebaseRecyclerAdapter<Task, TaskViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public ListUserActiveTasks() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.list_of_active_tasks, container, false);

        // get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        completedTasksRef = mDatabase.child("completed-user-tasks");

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(Task.class, R.layout.item_task_active,
                TaskViewHolder.class, postsQuery) {

            // method populateViewHolder is from
            // com.firebase.ui.database.FirebaseRecyclerAdapter;
            @Override
            protected void populateViewHolder(final TaskViewHolder viewHolder, final Task model, final int position) {
                final DatabaseReference postRef = getRef(position);


                // -executes if there is a click on either upvote or downvote
                // Bind Post to ViewHolder, setting OnClickListener for the up&down vote button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View voteView) {

                        // write to completed

                        // user id
                        completedTasksRef = completedTasksRef.child(getUid());
                        //  post id
                        completedTasksRef = completedTasksRef.child(postRef.getKey());
                        // set values
                        completedTasksRef.child("title").setValue(model.title);
                        completedTasksRef.child("body").setValue(model.body);

                        // reset ref for next task
                        completedTasksRef = mDatabase.child("completed-user-tasks");

                        // delete
                        // Keep all posts, dont delete mDatabase.child("posts").child(postRef.getKey()).removeValue();
                        mDatabase.child("user-active-tasks").child(getUid()).child(postRef.getKey()).removeValue();

                    }
                });

            } // end populate
        };
        mRecycler.setAdapter(mAdapter);
    } // end onActivityCreated



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //public abstract Query getQuery(DatabaseReference databaseReference);

    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-active-tasks")
                .child(getUid());
    }
}
