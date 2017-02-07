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
 * Created by vladshostak on 1/16/17.
 */

public class ListUserCompletedTasks extends Fragment {


    private static final String TAG = "ListUserCompletedTasks";

    // holds database reference
    private DatabaseReference mDatabase;
    private DatabaseReference activeTasksRef;


    private FirebaseRecyclerAdapter<Task, TaskViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    public ListUserCompletedTasks() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.list_of_completed_tasks, container, false);

        // get database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        activeTasksRef = mDatabase.child("user-active-tasks");

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
        mAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(Task.class, R.layout.item_task_completed,
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


                        // write to active

                        // user id
                        activeTasksRef = activeTasksRef.child(getUid());
                        //  post id
                        activeTasksRef = activeTasksRef.child(postRef.getKey());
                        // set values
                        activeTasksRef.child("title").setValue(model.title);
                        activeTasksRef.child("body").setValue(model.body);

                        // reset ref for next task
                        activeTasksRef = mDatabase.child("user-active-tasks");

                        // delete
                        mDatabase.child("completed-user-tasks").child(getUid()).child(postRef.getKey()).removeValue();


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
        return databaseReference.child("completed-user-tasks")
                .child(getUid());
    }

}
