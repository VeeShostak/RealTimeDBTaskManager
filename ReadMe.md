# RealTimeDBTaskManager

## Program Description:

Android application using cloud based Firebase backend integration. Utilizes real-time data storage
and synchronization, user authentication, and analytics.


**Application Description:**  

In SignInActivity we switch screens using an intent on successful user authentication. If the
user has been successfully authenticated then we switch to the MainActivity, and call finish(), to
terminate the current SignInActivity.

We also use a GoogleSignInApi intent and implement the startActivityForResult to sign in a user
using a Google account. If the result code returned was successful we authenticate firebase with
google, and on FirebaseAuth state change in the FirebaseAuth listener we write the user uid and
email to the database and starts the MainActivity.

MainActivity consists of a FragmentPagerAdapter (from android.support.v4.view.PagerAdapter),
where both fragment tabs are loaded and kept in memory. We have a FloatingActionButton that starts
the NewTaskActivity. Our menu contains a log out action that will take the user back to the
SignInActivity and end this one.

The NewTaskActivity contains an AutoCompleteTextView to give the user faster access to the most
common topics, without them having to type out the whole word. Once the user enter their task, and
pressed the submit fab, the post is written to “active-tasks” and “tasks”, the the activity finishes
and the user finds their self back in main activity. The reason we push to “tasks” is because there
might come a time where we want to add a feature to the application and allow users to post publicly
and see other peoples’ tasks. This “tasks” child will make data retrieval more efficient.

ListUserCompleteTasks and ListUserActiveTasks are both fragments that implement the
RecyclerView. When a user posts a task, the task gets posted to the active-tasks child. However, the
user has the option to mark the task complete by pressing on the “To-Do” button in the active tasks
fragment. This will post the task to the “completed-tasks” in the database, and remove the task from
the “active-tasks” child. If the user presses the “Done” button on the completed tasks fragment this
will add the task to the “active-tasks” and remove it from “completed-tasks”.



**Realtime Database Structure:**


![alt text][database]

[database]: https://github.com/VeeShostak/RealTimeDBTaskManager/blob/master/RealtimeDbStructureA.png "database stucture"
