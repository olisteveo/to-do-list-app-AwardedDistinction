// MyApp.java
package com.example.todolist;

import android.app.Application;
import android.util.Log;
import com.example.todolist.OliDB.TasksTable;

public class MyApp extends Application {

    // Database constants
    private static final String DATABASE_NAME = "task_database";
    private static final int DATABASE_VERSION = 1;
    protected TasksTable tasksDB; // Database manager instance
    private static final String TAG = "MyApp"; // Log tag

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate()"); // Log application creation
        tasksDB = TasksTable.initFor(this, DATABASE_NAME, DATABASE_VERSION); // Initialise database manager
        tasksDB.load(); // Load tasks from the database
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "Application onTerminate()"); // Log application termination
    }
}
