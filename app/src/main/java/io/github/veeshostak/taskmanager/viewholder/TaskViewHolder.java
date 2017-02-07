package io.github.veeshostak.taskmanager.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

import io.github.veeshostak.taskmanager.models.Task;
import io.github.veeshostak.taskmanager.R;

/**
 * Created by vladshostak on 1/17/17.
 */

public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public Button markTask;
    public TextView bodyView;


    public TaskViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);

        markTask = (Button) itemView.findViewById(R.id.mark_task);
    }


    public void bindToPost(Task task, View.OnClickListener starClickListener) {
        titleView.setText(task.title);
        bodyView.setText(task.body);

        markTask.setOnClickListener(starClickListener);
    }
}
